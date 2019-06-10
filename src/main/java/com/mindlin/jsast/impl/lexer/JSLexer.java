package com.mindlin.jsast.impl.lexer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mindlin.jsast.exception.JSEOFException;
import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.fs.SourceRange;
import com.mindlin.jsast.impl.lexer.ParsedNumber.ParsedDouble;
import com.mindlin.jsast.impl.lexer.ParsedNumber.ParsedInteger;
import com.mindlin.jsast.impl.lexer.Token.IdentifierToken;
import com.mindlin.jsast.impl.lexer.Token.NumericLiteralToken;
import com.mindlin.jsast.impl.lexer.Token.RegExpToken;
import com.mindlin.jsast.impl.lexer.Token.StringLiteralToken;
import com.mindlin.jsast.impl.lexer.Token.TemplateLiteralToken;
import com.mindlin.jsast.impl.tree.LineMap;
import com.mindlin.jsast.impl.tree.LineMap.LineMapBuilder;
import com.mindlin.jsast.impl.util.CharacterArrayStream;
import com.mindlin.jsast.impl.util.CharacterStream;
import com.mindlin.jsast.impl.util.Characters;
import com.mindlin.nautilus.fs.SourcePosition;

public class JSLexer implements Supplier<Token> {
	
	protected static String decodeEASCII(byte value) {
		Charset easciiCharset = Charset.forName("EASCII");
		CharBuffer buffer = easciiCharset.decode(ByteBuffer.wrap(new byte[]{ value }));
		return buffer.toString();
	}
	
	protected final CharacterStream chars;
	protected Token lookahead = null;
	protected LinkedList<Token> lookaheads = new LinkedList<>();
	//TODO: supply source file
	protected final LineMapBuilder lines;
	
	public JSLexer(String src) {
		this(new CharacterArrayStream(src));
	}
	
	public JSLexer(char[] chars) {
		this(new CharacterArrayStream(chars));
	}
	
	public JSLexer(CharacterStream chars) {
		this(null, chars);
	}
	
	public JSLexer(SourceFile source) {
		this(source, source.getSourceStream());
	}
	
	public JSLexer(SourceFile source, CharacterStream chars) {
		this.lines = new LineMapBuilder(source);
		this.chars = chars;
	}
	
	protected void markNewline(long position) {
		this.lines.putNewline(position);
	}
	
	protected void error(SourcePosition location, String text, Object...args) {
		
	}
	
	protected void error(SourceRange range, String text, Object...args) {
		
	}
	
	protected void errorEOF(SourcePosition position, String text, Object...args) {
		
	}
	
	protected void errorEOF(SourceRange range, String text, Object...args) {
		
	}
	
	public LineMap getLines() {
		return this.lines;
	}
	
	public SourcePosition getPosition() {
		return this.resolvePosition(this.getPositionOffset());
	}
	
	public SourcePosition resolvePosition(long position) {
		return this.lines.lookup(position);
	}
	
	public long getPositionOffset() {
		return this.chars.position();
	}
	
	/**
	 * Get start of next token. Equivalent to {@code this.peek().getStart()}.
	 * 
	 * @return Start of next token.
	 */
	public SourcePosition getNextStart() {
		return this.peek().getStart();
	}
	
	public CharacterStream getCharacters() {
		return this.chars;
	}
	
	protected void invalidateLookaheads(long clobberIdx) {
		if (this.lookahead == null) {
			// No-op
		} else if (clobberIdx <= this.lookahead.getEnd().getOffset()) {
			this.lookahead = null;
			this.lookaheads.clear();
		} else if (!this.lookaheads.isEmpty()) {
			// Clobber some
			for (Iterator<Token> iter = this.lookaheads.descendingIterator(); iter.hasNext(); ) {
				Token current = iter.next();
				if (clobberIdx > current.getEnd().getOffset())
					break;
				iter.remove();
			}
		}
	}
	
	public String nextStringLiteral() {
		chars.skipWhitespace();
		if (chars.hasNext())
			return nextStringLiteral(chars.next());
		return null;
	}
	
	protected String readOctalEASCII(char c) {
		//TODO: test if lookahead exists?
		int val = c - '0';
		if (chars.peek() >= '0' && chars.peek() <= '7') {
			val = (val << 3) | (chars.next() - '0');
			if (val < 32 && chars.peek() >= '0' && chars.peek() <= '7')
				val = (val << 3) | (chars.next() - '0');
		}
		return JSLexer.decodeEASCII((byte) val);
	}
	
	protected String readHexEASCII() {
		// EASCII hexdecimal character escape
		// In \xXX form 
		if (!chars.hasNext(2))
			throw new JSEOFException(JSLexerMessages.EASCII_EOF, this.resolvePosition(this.getPositionOffset() - 2));
		String text = chars.copyNext(2).toString();
		int val;
		try {
			val = Integer.parseInt(text, 16);
		} catch (NumberFormatException e) {
			SourceRange range = new SourceRange(this.resolvePosition(this.getPositionOffset() - 4), this.getPosition());
			throw new JSSyntaxException("Invalid Extended ASCII escape sequence (\\x" + text + ")", range, e);
		}
		
		return JSLexer.decodeEASCII((byte) val);
	}
	
	/**
	 * Read an escape sequence in the form of either {@code uXXXX} or <code>u{X...XXX}</code>.
	 * 
	 * It is expected that the 'u' at the start of the escape sequence has already been consumed.
	 * 
	 * @return character read
	 * @see <a href="https://tc39.github.io/ecma262/#prod-UnicodeEscapeSequence">ECMAScript 262 &sect; 11.8.4</a>
	 * @see <a href="https://mathiasbynens.be/notes/javascript-escapes">JavaScript character escape sequences � Mathias Bynens</a>
	 */
	protected String readUnicodeEscapeSequence() {
		long start = this.getPositionOffset();
		int value = 0;
		if (chars.peek() == '{') {
			// In u{X...XX} form
			chars.skip(1);
			return this.readExtendedUnicodeEscape();
		} else {
			//Escape in uXXXX form
			if (!chars.hasNext(4))
				throw new JSEOFException(JSLexerMessages.UNICODE_ESCAPE_EOF, this.getPosition());
			for (int i = 0; i < 4; i++) {
				char c = chars.next();
				int digit = Characters.asHexDigit(c);
				if (digit < 0)
					throw new JSSyntaxException(JSLexerMessages.format(JSLexerMessages.INVALID_CHARACTER_IN_UNICODE_ESCAPE, c), this.getPosition());
				value = (value << 4) | digit;
			}
		}
		
		try {
			return new String(new int[] { value }, 0, 1);
		} catch (IllegalArgumentException e) {
			SourceRange range = new SourceRange(this.resolvePosition(start), this.getPosition());
			throw new JSSyntaxException(JSLexerMessages.format(JSLexerMessages.INVALID_UNICODE_CODE_POINT, value), range, e);
		}
	}
	
	/**
	 * Read escape sequence in for <code>u{X...XXX}</code>.
	 * @return Decoded text
	 */
	protected String readExtendedUnicodeEscape() {
		long start = this.getPositionOffset();
		//Max unicode code point is 0x10FFFF, which fits within a 32-bit integer
		ParsedInteger value = this.readHexDigits(1, true, false);
		boolean valid = true;
		if (value.length < 1) {
			this.error(this.getPosition(), JSLexerMessages.HEX_DIGIT_EXPECTED);
			valid = false;
		} else if (value.longValue() > 0x10FFFF) {
			this.error(this.getPosition(), JSLexerMessages.INVALID_UNICODE_CODE_POINT, value.longValue());
			valid = false;
		}
		
		if (!chars.hasNext()) {
			this.errorEOF(this.getPosition(), JSLexerMessages.UNICODE_ESCAPE_EOF);
			valid = false;
		} else if (chars.peek() == '}') {
			chars.skip(1);
		} else {
			this.error(this.getPosition(), JSLexerMessages.UNTERMINATED_UNICODE_ESCAPE);
			valid = false;
		}
		
		if (valid) {
			try {
				return new String(new int[] { value.intValue() }, 0, 1);
			} catch (IllegalArgumentException e) {
				SourceRange range = new SourceRange(this.resolvePosition(start), this.getPosition());
				//TODO: error?
				throw new JSSyntaxException(JSLexerMessages.format(JSLexerMessages.INVALID_UNICODE_CODE_POINT, value.longValue()), range, e);
			}
		}
		
		return "";
	}
	
	protected String readEscapeSequence(char c) {
		switch (c) {
			case '\'':
			case '"':
			case '\\':
			case '`':
				return Character.toString(c);
			case 'n':
				return "\n";
			case 'r':
				return "\r";
			case 'v':
				return Character.toString(Characters.VT);// vertical tab; it's a thing.
			case 't':
				return "\t";
			case 'b':
				return "\b";
			case 'f':
				return "\f";
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
				//EASCII octal escape
				return this.readOctalEASCII(c);
			case '\n':
				this.markNewline(chars.position());
				if (chars.hasNext() && chars.peek() == '\r')
					chars.skip(1);
				return "";
			case '\r':
				if (chars.hasNext() && chars.peek() == '\n') {
					this.markNewline(chars.position());
					chars.skip(1);
				}
				return "";
			case 'u':
				//Unicode escape
				return this.readUnicodeEscapeSequence();
			case 'x':
				return this.readHexEASCII();
			default:
				throw new JSSyntaxException("Invalid escape sequence: \\" + c, this.resolvePosition(this.getPositionOffset() - 2));
		}
	}
	
	protected TemplateTokenInfo nextTemplateLiteral() {
		boolean tail = false;
		boolean escaped = false;
		StringBuilder cooked = new StringBuilder();
		
		loop:
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a template literal", this.getPosition());
			char c = chars.next();
			if (escaped) {
				escaped = false;
				cooked.append(this.readEscapeSequence(c));
				continue;
			}
			switch (c) {
				case '`':
					tail = true;
					break loop;
				case '$': {
					if (chars.hasNext() && chars.peek() == '{') {
						chars.next();
						break loop;
					}
					cooked.append('$');
					break;
				}
				case '\\':
					escaped = true;
					continue;
				case '\r':
					// '\r\n' is treated as a single character
					if (chars.hasNext() && chars.peek() == '\n')
						chars.skip(1);
					//$FALL-THROUGH$
				case '\n':
					cooked.append('\n');
					break;
				default:
					cooked.append(c);
					break;
			}
		}
		
		return new TemplateTokenInfo(false, tail, cooked.toString());
	}
	
	public String nextStringLiteral(final char startChar) {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSEOFException("Unexpected EOF while parsing a string literal (" + sb + ")", this.getPosition());
			char c = chars.next();
			if (isEscaped) {
				isEscaped = false;
				sb.append(readEscapeSequence(c));
				continue;
			} else if (c == '\\') {
				isEscaped = true;
				continue;
			} else if (c == '\r' || c == '\n') {
				//TODO remove (template literals are handled elsewhere)
				if (startChar == '`') {
					//Newlines are allowed as part of a template literal
					if (chars.hasNext() && ((c == '\r' && chars.peek() == '\n') || chars.peek() == '\r'))
						chars.skip(1);
					sb.append('\n');
					continue;
				}
				throw new JSSyntaxException("Illegal newline in the middle of a string literal", this.getPosition());
			} else if (c == startChar) {
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public boolean isEOF() {
		return !chars.hasNext();
	}
	
	//Numeric literals
	protected ParsedInteger readHexDigits(int minCount, boolean greedy, boolean separators) throws JSSyntaxException {
		boolean allowSeparator = false;
		boolean usedSeparators = false;
		int read = 0;
		long result = 0;
		while (chars.hasNext() && (read < minCount || greedy)) {
			char lookahead = chars.peek();
			if (separators && lookahead == '_') {
				if (!separators)
					throw new JSSyntaxException(JSLexerMessages.NUMERIC_SEPARATORS_DISALLOWED, this.getPosition());
				if (!allowSeparator)
					throw new JSSyntaxException(JSLexerMessages.CONSECUTIVE_NUMERIC_SEPARATORS, this.getPosition());

				usedSeparators = true;
				allowSeparator = false;
				chars.skip(1);
				continue;
			} else if (Characters.isHexDigit(lookahead)) {
				result = (result << 4) | Characters.asHexDigit(chars.next());
				read++;
			} else {
				break;
			}
			allowSeparator = separators;
		}
		
		return new ParsedInteger(NumericLiteralType.HEXDECIMAL, result, read, usedSeparators);
	}
	
	protected ParsedInteger readOctalDigits() {
		int length = 0;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead < '0' || '7' < lookahead) {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead))
					throw new JSSyntaxException("Unexpected character: " + lookahead, this.getPosition());
				break;
			}
			result = (result << 3) | (chars.next() - '0');
			length++;
		}
		
		return new ParsedInteger(NumericLiteralType.OCTAL, result, length, false);
	}
	
	protected ParsedInteger nextHexLiteral() throws JSSyntaxException {
		//TODO: fix for BigInt
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (!Characters.isHexDigit(lookahead)) {
				if (Characters.canStartIdentifier(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", this.getPosition());
				break;
			}
			isEmpty = false;
			//TODO: check for overflows
			result = (result << 4) | Characters.asHexDigit(chars.next());
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in hex literal", this.getPosition());
		
		return new ParsedInteger(NumericLiteralType.HEXDECIMAL, result, -1, false);//TODO: finish
	}
	
	protected ParsedInteger nextBinaryLiteral() throws JSSyntaxException {
		//TODO: fix for BigInt
		boolean isEmpty = true;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead != '0' && lookahead != '1') {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead) || isEmpty)
					throw new JSSyntaxException("Unexpected token", this.getPosition());
				break;
			}
			isEmpty = false;
			//TODO: check for overflows
			result = (result << 1) | (chars.next() - '0');
		}
		if (isEmpty)
			throw new JSEOFException("Unexpected EOF in binary literal", this.getPosition());
		return new ParsedInteger(NumericLiteralType.BINARY, result, -1, false);//TODO: finish
	}
	
	protected ParsedInteger nextOctalLiteral() throws JSSyntaxException {
		//TODO: fix for BigInt
		int length = 0;
		long result = 0;
		while (chars.hasNext()) {
			char lookahead = chars.peek();
			if (lookahead < '0' || '7' < lookahead) {
				if (Characters.canStartIdentifier(lookahead) || Characters.isDecimalDigit(lookahead))
					throw new JSSyntaxException("Unexpected character: " + lookahead, this.getPosition());
				break;
			}
			result = (result << 3) | (chars.next() - '0');
			length++;
		}
		if (length == 0)
			throw new JSEOFException("Unexpected EOF in hex literal", this.getPosition());
		return new ParsedInteger(NumericLiteralType.OCTAL, result, -1, false);//TODO: finish
	}
	
	protected ParsedInteger readDecimalDigits() {
		int length = 0;
		long result = 0;
		//TODO: check for overflow
		while (chars.hasNext() && Characters.isDecimalDigit(chars.peek())) {
			result = result * 10 + (chars.next() - '0');
			length++;
		}
		return new ParsedInteger(NumericLiteralType.DECIMAL, result, length, false);
	}
	
	protected ParsedInteger nextDecimalLiteral() throws JSSyntaxException {
		this.readDecimalDigits();
//		while (chars.hasNext() && Characters.isDecimalDigit(chars.peek()))
//			iPart = Math.addExact(Math.multiplyExact(iPart, 10), chars.next() - '0');
		
		return null;
	}
	
	public ParsedNumber nextNumericLiteral() throws JSSyntaxException {
		//TODO: fix for BigInt
		NumericLiteralType type = NumericLiteralType.DECIMAL;
		
		boolean isPositive = true;
		if (!chars.hasNext())
			throw new JSEOFException("Unexpected EOF while parsing numeric literal", this.getPosition());
		
		char c;
		//skip non-number stuff
		//TODO check if this is correct
		while (!Characters.canStartNumber(chars.peek()))
			chars.next();
		
		if ((c = chars.peek()) == '-') {
			isPositive = false;
			chars.next();
		} else if (c == '+') {
			//Ignore
			chars.next();
		}
		
		if (chars.peek() == '0') {
			if (!chars.hasNext(2)) {
				//Is '0'
				chars.skip(1);
				return new ParsedInteger(NumericLiteralType.DECIMAL, 0, 1, false);
			}
			
			switch (chars.peek(2)) {
				case 'X':
				case 'x':
					type = NumericLiteralType.HEXDECIMAL;
					chars.next(2);
					return nextHexLiteral();
				case 'b':
				case 'B':
					type = NumericLiteralType.BINARY;
					chars.next(2);
					return nextBinaryLiteral();
				case 'o':
				case 'O':
					type = NumericLiteralType.OCTAL;
					chars.next(2);
					return nextOctalLiteral();
				default:
					//Number starts with a '0', but can be upgraded to a DECIMAL type
					type = NumericLiteralType.OCTAL_IMPLICIT;
					break;
			}
		}
		
		
		chars.mark();
		boolean hasDecimal = false;
		boolean hasExponent = false;
		
		outer:
		while (chars.hasNext()) {
			c = chars.next();
			if (c == '.') {
				if ((type == NumericLiteralType.DECIMAL || type == NumericLiteralType.OCTAL_IMPLICIT)) {
					if (!hasDecimal) {
						hasDecimal = true;
						//Possibly upgrade from OCTAL_IMPLICIT
						type = NumericLiteralType.DECIMAL;
						continue;
					} else {
						//TODO try to remove backwards skips
						chars.skip(-1);
						break;
					}
				}
				chars.unmark();
				throw new JSSyntaxException("Unexpected number", this.getPosition());
			}
			
			if (c == ';' || !Character.isJavaIdentifierPart(c)) {
				chars.skip(-1);
				break;
			}
			
			boolean isValid = false;
			if (c >= '0') {
				switch (type) {
					case OCTAL_IMPLICIT:
						if (c > '7' && c <= '9') {
							//Upgrade to decimal if possible
							type = NumericLiteralType.DECIMAL;
							isValid = true;
							break;
						} else if (c == 'e' || c == 'E') {
							hasExponent = true;
							//Upgrade to decimal, parse exponent
							//TODO refactor with other exponent-parsing code
							//TODO optimize with single pass
							//Read optional '+' or '-' at start of exponent
							if ((c = chars.peek()) == '-')
								chars.next();
							else if (c == '+')
								chars.next();
							
							while (chars.hasNext() && (c = chars.peek()) >= '0' && c <= '9')
								chars.next();
							break outer;
						}
						//$FALL-THROUGH$
					case OCTAL:
						isValid = c <= '7';
						break;
					case DECIMAL:
						if (isValid = (c <= '9'))
							break;
						if (c == 'e' || c == 'E') {
							hasExponent = true;
							//Parse exponent
							//TODO optimize with single pass
							//Read optional '+' or '-' at start of exponent
							if ((c = chars.peek()) == '-')
								chars.next();
							else if (c == '+')
								chars.next();
							
							while (chars.hasNext() && (c = chars.peek()) >= '0' && c <= '9')
								chars.next();
							break outer;
						}
				}
			}
			if (!isValid) {
				chars.unmark();
				throw new JSSyntaxException("Unexpected identifier in numeric literal (" + type + "): " + Character.getName(c), this.getPosition());
			}
		}
		//Build the string for Java to parse (It might be slightly faster to calculate the value of the digit while parsing,
		//but it causes problems with OCTAL_IMPLICIT upgrades.
		String nmb = (isPositive ? "" : "-") + chars.copyFromMark();
		int length = nmb.length();
		
		//Return number. Tries to cast down to the smallest size that it can losslessly
		if (hasDecimal || hasExponent) {
			double result = Double.parseDouble(nmb);
			//TODO reorder return options
			return new ParsedDouble(type, result, length, false);
		}
		long result = Long.parseLong(nmb, type.getExponent());
		return new ParsedInteger(type, result, length, false);
	}
	
	public Token expect(JSSyntaxKind kind) {
		Token t = this.nextToken();
		if (t.getKind() != kind)
			throw new JSUnexpectedTokenException(t, kind);
		return t;
	}
	
	public Token expectAny(JSSyntaxKind first, JSSyntaxKind...kinds) {
		Token t = this.nextToken();
		if (!t.matchesAny(first, kinds))
			//TODO: fix type
			throw new JSUnexpectedTokenException(t, kinds);
		return t;
	}
	
	public String nextIdentifier() {
		if (!chars.hasNext())
			return null;
		
		StringBuilder sb = new StringBuilder();
		
		//Start character
		{
			char startChar = chars.peek();
			if (Characters.canStartIdentifier(startChar)) {
				chars.skip(1);
				sb.append(startChar);
			} else if (startChar == '\\' && chars.peek(2) == 'u') {
				chars.skip(2);
				sb.append(readUnicodeEscapeSequence());
			} else
				//Not start of identifier
				return null;
		}
		
		while (chars.hasNext()) {
			char c = chars.peek();
			if (Characters.isIdentifierPart(c)) {
				chars.skip(1);
				sb.append(c);
			} else if (c == '\\'  && chars.peek(2) == 'u') {
				chars.skip(2);
				sb.append(readUnicodeEscapeSequence());
			} else
				break;
		}
		//TODO: intern string?
		return sb.toString();
	}
	
	public String nextRegularExpression() {
		StringBuilder sb = new StringBuilder();
		boolean isEscaped = false;
		while (true) {
			if (!chars.hasNext())
				throw new JSSyntaxException("Unexpected EOF while parsing a regex literal", this.getPosition());
			char c = chars.next();
			
			if (isEscaped)
				isEscaped = false;
			else if (c == '\\')
				isEscaped = true;
			else if (c == '/')
				break;
			
			sb.append(c);
		}
		return sb.toString();
	}
	
	protected String scanRegExpBody(String st) {
		StringBuffer sb = new StringBuffer();
		boolean escaped = false;
		int classes = 0;
		int groups = 0;
		for (int i = 1, l = st.length(); i < l; i++) {
			char ch = st.charAt(i);
			if (ch == '\\')
				escaped = true;
			sb.append(ch);
		}
		while (!isEOF()) {
			char ch = chars.next();
			if (!escaped && ch == '/')
				break;
			sb.append(ch);
			if (escaped) {
				escaped = false;
				continue;
			}
			switch (ch) {
				case '\\':
					escaped = true;
					break;
				case '[':
					classes++;
					break;
				case '(':
					groups++;
					break;
				case ']':
					if (--classes < 0)
						throw new JSSyntaxException("Illegal regular expression character class end", this.getPosition());
					break;
				case ')':
					if (--groups < 0)
						throw new JSSyntaxException("Illegal regular expression group end", this.getPosition());
					break;
				default:
					break;
			}
		}
		if (chars.current() != '/')
			throw new JSEOFException("Unexpected EOF in regex literal", this.getPosition());
		return sb.toString();
	}
	
	protected String scanRegExpFlags() {
		StringBuilder sb = new StringBuilder();
		while (!isEOF()) {
			char c = chars.peek();
			if (c != 'g' && c!= 'i' && c!= 'm' && c != 'y')
				break;
			sb.append(chars.next());
		}
		return sb.toString();
	}
	
	public RegExpToken finishRegExpLiteral(Token start) {
		Objects.requireNonNull(start);
		if (!start.matches(JSSyntaxKind.DIVISION) && !start.matches(JSSyntaxKind.DIVISION_ASSIGNMENT))
			throw new JSSyntaxException("Regular expression must start with a slash", start.getRange());
		
		this.invalidateLookaheads(start.getEnd().getOffset());
		
		chars.mark();
		
		String body = scanRegExpBody(start.text.toString());
		String flags = scanRegExpFlags();
		
		SourceRange range = new SourceRange(start.getStart(), this.getPosition());
		String text = start.text + chars.copyFromMark();
		return new RegExpToken(start.flags, range, text, body, flags);
	}
	
	public TemplateLiteralToken finishTemplate(Token start) {
		Objects.requireNonNull(start);
		if (!start.matches(JSSyntaxKind.RIGHT_BRACE))
			throw new JSSyntaxException("Template continuation must start with a right brace", start.getRange());
		
		this.invalidateLookaheads(start.getEnd().getOffset());
		
		chars.mark();
		
		TemplateTokenInfo data = this.nextTemplateLiteral();
		
		SourceRange range = new SourceRange(start.getStart(), this.getPosition());
		CharSequence text = start.getText() + chars.copyFromMark();
		return new TemplateLiteralToken(start.flags, range, text, false, data.tail, data.cooked);
	}
	
	public String nextComment(final boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		while (true) {
			if (!chars.hasNext()) {
				if (singleLine)
					break;
				throw new JSEOFException("Unexpected EOF while parsing comment", this.getPosition());
			}
			char c = chars.next();
			
			//Mark newline
			if (c == '\n')
				this.lines.putNewline(chars.position());
			
			//End conditions
			if (singleLine) {
				if (c == '\n' || c == '\r')
					break;
			} else if (c == '*' && chars.hasNext() && chars.peek() == '/') {
				chars.skip(1);
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	protected Token finishToken(long start, int flags, JSSyntaxKind kind) {
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		String text = chars.copyFromMark();
		return new Token(flags, range, kind, text);
	}
	
	protected Token finishStringLiteralToken(long start, int flags) {
		String value = this.nextStringLiteral();
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		String text = chars.copyFromMark();
		return new StringLiteralToken(flags, range, text, value);
	}
	
	protected Token finishTemplateToken(long start, int flags) {
		chars.skip(1);// Skip over the leading '`'
		TemplateTokenInfo value = this.nextTemplateLiteral();
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		String text = chars.copyFromMark();
		return new TemplateLiteralToken(flags, range, text, true, value.tail, value.cooked);
	}
	
	protected Token finishIdentifierToken(long start, int flags, String name) {
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		String text = chars.copyFromMark();
		return new IdentifierToken(flags, range, text, name.intern());
	}
	
	protected Token finishNumericLiteralToken(long start, int flags) {
		ParsedNumber value = this.nextNumericLiteral();
		
		SourceRange range = new SourceRange(this.resolvePosition(start + 1), this.getPosition());
		String text = chars.copyFromMark();
		
		return new NumericLiteralToken(flags, range, text, value);
	}
	
	protected boolean isSingleLineCommentStart() {
		return chars.hasNext(2) && chars.peek() == '/' && chars.peek(2) == '/';
	}
	
	protected boolean isMultiLineCommentStart() {
		return chars.hasNext(2) && chars.peek() == '/' && chars.peek(2) == '*';
	}
	
	/**
	 * Get hint for reading next token. For the most part, the
	 * {@link JSSyntaxKind} returned from this will indicate the next token
	 * read, except for the following cases:
	 * <dl>
	 * 	<dt>{@link JSSyntaxKind#STRING_LITERAL}</dt>
	 * 		<dd>A string literal should be completed</dd>
	 * 	<dt>{@link JSSyntaxKind#NUMERIC_LITERAL}</dt>
	 * 		<dd>A numeric/bigint literal should be completed</dd>
	 * 	<dt>{@link JSSyntaxKind#TEMPLATE_LITERAL}</dt>
	 * 		<dd>A template literal should be started</dd>
	 * 	<dt>{@link JSSyntaxKind#IDENTIFIER}</dt>
	 * 		<dd>An identifier should be attempted (may fail if it starts with an escape). This identifier may be a keyword.</dd>
	 * 	<dt>{@link JSSyntaxKind#COMMENT}</dt>
	 * 		<dd>A single/multi-line comment should be consumed.</dd>
	 * </dl>
	 * @return Hint for reading next token.
	 */
	protected JSSyntaxKind getTokenHint() {
		char c = chars.peek();
		char d = chars.peekSafe(2);
		switch (c) {
			case '"':
			case '\'':
				return JSSyntaxKind.STRING_LITERAL;
			case '`':
				return JSSyntaxKind.TEMPLATE_LITERAL;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return JSSyntaxKind.NUMERIC_LITERAL;
			// Simple punctuation
			case ':':
				return JSSyntaxKind.COLON;
			case ';':
				return JSSyntaxKind.SEMICOLON;
			case '?':
				return JSSyntaxKind.QUESTION_MARK;
			case '[':
				return JSSyntaxKind.LEFT_BRACKET;
			case ']':
				return JSSyntaxKind.RIGHT_BRACKET;
			case '(':
				return JSSyntaxKind.LEFT_PARENTHESIS;
			case ')':
				return JSSyntaxKind.RIGHT_PARENTHESIS;
			case '{':
				return JSSyntaxKind.LEFT_BRACE;
			case '}':
				//TODO: template head?
				return JSSyntaxKind.RIGHT_BRACE;
			case ',':
				return JSSyntaxKind.COMMA;
			case '~':
				return JSSyntaxKind.BITWISE_NOT;
			case '@':
				return JSSyntaxKind.AT_SYMBOL;
			// 2 option punctuation (assignment variants)
			case '%':
				return (d == '=') ? JSSyntaxKind.REMAINDER_ASSIGNMENT : JSSyntaxKind.REMAINDER;
			case '^':
				return (d == '=') ? JSSyntaxKind.BITWISE_XOR_ASSIGNMENT : JSSyntaxKind.BITWISE_XOR;
			// 3+ option punctuation
			case '+':
				if (d == '=') // '+='
					return JSSyntaxKind.ADDITION_ASSIGNMENT;
				else if (d == '+') // '++'
					return JSSyntaxKind.INCREMENT;
				else
					return JSSyntaxKind.PLUS;
			case '-':
				if (d == '=') // '-='
					return JSSyntaxKind.SUBTRACTION_ASSIGNMENT;
				else if (d == '-') // '--'
					return JSSyntaxKind.DECREMENT;
				else
					return JSSyntaxKind.MINUS;
			case '.':
				if (Characters.isDecimalDigit(d)) // Fractional numeric literal
					return JSSyntaxKind.NUMERIC_LITERAL;
				else if (d == '.' && chars.peekSafe(3) == '.') // '...'
					return JSSyntaxKind.SPREAD;
				else // '.'
					return JSSyntaxKind.PERIOD;
			case '/':
				if (d == '/') // Single-line comment start '//'
					return JSSyntaxKind.COMMENT;
				else if (d == '*') // Multi-line comment start '/*'
					return JSSyntaxKind.COMMENT;
				else if (d == '=') // '/='
					return JSSyntaxKind.DIVISION_ASSIGNMENT;
				else // '/'
					return JSSyntaxKind.DIVISION;
			case '!':
				if (d == '=') { // '!='
					if (chars.peekSafe(3) == '=') // '!=='
						return JSSyntaxKind.STRICT_NOT_EQUAL;
					return JSSyntaxKind.NOT_EQUAL;
				} else { // '!'
					return JSSyntaxKind.LOGICAL_NOT;
				}
			case '=':
				if (d == '>') // '=>'
					return JSSyntaxKind.LAMBDA;
				else if (d != '=') // '='
					return JSSyntaxKind.ASSIGNMENT;
				else if (chars.peekSafe(3) != '=') // '=='
					return JSSyntaxKind.EQUAL;
				else // '==='
					return JSSyntaxKind.STRICT_EQUAL;
			case '<':
				//TODO: HTML-style comment start '<!--'
				//TODO: Git merge tokens '<<<<<<<'
				if (d == '=') // '<='
					return JSSyntaxKind.LESS_THAN_EQUAL;
				else if (d != '<') // '<'
					return JSSyntaxKind.LESS_THAN;
				else if (chars.peekSafe(3) != '=') // '<<'
					return JSSyntaxKind.LEFT_SHIFT;
				else // '<<='
					return JSSyntaxKind.LEFT_SHIFT_ASSIGNMENT;
			case '>': {
				//TODO: Git merge tokens '>>>>>>>'
				if (d == '=') // '>='
					return JSSyntaxKind.GREATER_THAN_EQUAL;
				else if (d != '>') // '>'
					return JSSyntaxKind.GREATER_THAN;
				
				char e = chars.peekSafe(3);
				if (e == '=') // '>>='
					return JSSyntaxKind.RIGHT_SHIFT_ASSIGNMENT;
				else if (e != '>') // '>>'
					return JSSyntaxKind.RIGHT_SHIFT;
				
				// '>>>' / '>>>='
				if (chars.peekSafe(4) == '=') // '>>>='
					return JSSyntaxKind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT;
				else // '>>>'
					return JSSyntaxKind.UNSIGNED_RIGHT_SHIFT;
			}
			case '&':
				if (d == '&') // '&&'
					return JSSyntaxKind.LOGICAL_AND;
				else if (d == '=') // '&='
					return JSSyntaxKind.BITWISE_AND_ASSIGNMENT;
				else // '&'
					return JSSyntaxKind.AMPERSAND;
			case '|':
				if (d == '|') // '||'
					return JSSyntaxKind.LOGICAL_OR;
				else if (d == '=') // '|='
					return JSSyntaxKind.BITWISE_OR_ASSIGNMENT;
				else // '|'
					return JSSyntaxKind.VBAR;
			case '*':
				if (d == '=') // '*='
					return JSSyntaxKind.MULTIPLICATION_ASSIGNMENT;
				else if (d != '*') // '*'
					return JSSyntaxKind.ASTERISK;
				return (chars.peekSafe(3) == '=') ? JSSyntaxKind.EXPONENTIATION_ASSIGNMENT : JSSyntaxKind.EXPONENTIATION;
			case '\\':
				// Assume escape that starts identifier
				return JSSyntaxKind.IDENTIFIER;
			default:
				if (Characters.canStartIdentifier(c))
					return JSSyntaxKind.IDENTIFIER;
				//TODO: other cases?
				return null;
		}
	}
	
	protected JSSyntaxKind lookupKeyword(String name) {
		switch (name) {
			case "abstract":
				return JSSyntaxKind.ABSTRACT;
			case "as":
				return JSSyntaxKind.AS;
			case "async":
				return JSSyntaxKind.ASYNC;
			case "await":
				return JSSyntaxKind.AWAIT;
			case "bigint":
				break;//TODO
			case "boolean":
				break;//TODO
			case "break":
				return JSSyntaxKind.BREAK;
			case "case":
				return JSSyntaxKind.CASE;
			case "catch":
				return JSSyntaxKind.CATCH;
			case "class":
				return JSSyntaxKind.CLASS;
			case "continue":
				return JSSyntaxKind.CONTINUE;
			case "const":
				return JSSyntaxKind.CONST;
			case "constructor":
				return JSSyntaxKind.CONSTRUCTOR;
			case "debugger":
				return JSSyntaxKind.DEBUGGER;
			case "declare":
				return JSSyntaxKind.DECLARE;
			case "default":
				return JSSyntaxKind.DEFAULT;
			case "delete":
				return JSSyntaxKind.DELETE;
			case "do":
				return JSSyntaxKind.DO;
			case "else":
				return JSSyntaxKind.ELSE;
			case "enum":
				return JSSyntaxKind.ENUM;
			case "export":
				return JSSyntaxKind.EXPORT;
			case "extends":
				return JSSyntaxKind.EXTENDS;
			case "false":
				return JSSyntaxKind.FALSE;
			case "finally":
				return JSSyntaxKind.FINALLY;
			case "for":
				return JSSyntaxKind.FOR;
			case "from":
				return JSSyntaxKind.FROM;
			case "function":
				return JSSyntaxKind.FUNCTION;
			case "get":
				return JSSyntaxKind.GET;
			case "global":
				break;//TODO
			case "if":
				return JSSyntaxKind.IF;
			case "implements":
				return JSSyntaxKind.IMPLEMENTS;
			case "import":
				return JSSyntaxKind.IMPORT;
			case "in":
				return JSSyntaxKind.IN;
			case "infer":
				break;//TODO
			case "instanceof":
				return JSSyntaxKind.INSTANCEOF;
			case "interface":
				return JSSyntaxKind.INTERFACE;
			case "is":
				break;//TODO
			case "keyof":
				break;//TODO
			case "let":
				return JSSyntaxKind.LET;
			case "module":
				break;//TODO
			case "namespace":
				break;//TODO
			case "never":
				break;//TODO
			case "new":
				return JSSyntaxKind.NEW;
			case "null":
				return JSSyntaxKind.NULL;
			case "number":
				break;//TODO
			case "object":
				break;//TODO
			case "of":
				return JSSyntaxKind.OF;
			case "package":
				return JSSyntaxKind.PACKAGE;
			case "private":
				return JSSyntaxKind.PRIVATE;
			case "protected":
				return JSSyntaxKind.PROTECTED;
			case "public":
				return JSSyntaxKind.PUBLIC;
			case "readonly":
				return JSSyntaxKind.READONLY;
			case "require":
				break;//TODO
			case "return":
				return JSSyntaxKind.RETURN;
			case "set":
				return JSSyntaxKind.SET;
			case "static":
				return JSSyntaxKind.STATIC;
			case "string":
				break;//TODO
			case "super":
				return JSSyntaxKind.SUPER;
			case "switch":
				return JSSyntaxKind.SWITCH;
			case "symbol":
				break;//TODO
			case "this":
				return JSSyntaxKind.THIS;
			case "throw":
				return JSSyntaxKind.THROW;
			case "true":
				return JSSyntaxKind.TRUE;
			case "try":
				return JSSyntaxKind.TRY;
			case "type":
				return JSSyntaxKind.TYPE;
			case "typeof":
				return JSSyntaxKind.TYPEOF;
			case "undefined":
				break;//TODO
			case "unique":
				break;//TODO
			case "unknown":
				break;//TODO
			case "var":
				return JSSyntaxKind.VAR;
			case "void":
				return JSSyntaxKind.VOID;
			case "while":
				return JSSyntaxKind.WHILE;
			case "with":
				return JSSyntaxKind.WITH;
			case "yield":
				return JSSyntaxKind.YIELD;
			default:
				break;
		}
		return null;
	}
	
	protected Token readToken() {
		//Skip whitespace until token
		final long scanStart = Math.max(this.getPositionOffset(), -1);
		int flags = 0;
		while (chars.hasNext() && Characters.isJsWhitespace(chars.peek())) {
			if (Characters.isLineBreak(chars.next())) {
				flags |= Token.FLAG_PRECEDEING_NEWLINE;
				//TODO: does this cause problems if we're backtracking?
				this.lines.putNewline(chars.position());
			}
		}
		
		//Special EOF token
		if (isEOF()) {
			SourceRange range = new SourceRange(this.getPosition(), this.getPosition());
			Token result = new Token(flags, range, JSSyntaxKind.END_OF_FILE, null);
			if (this.lookahead == null)
				this.lookahead = result;
			return result;
		}
		
		while (true) {
			if (this.isEOF()) {
				SourceRange range = new SourceRange(this.getPosition(), this.getPosition());
				Token result = new Token(flags, range, JSSyntaxKind.END_OF_FILE, null);
				if (this.lookahead == null)
					this.lookahead = result;
				return result;
			}
			
			if (Characters.isJsWhitespace(chars.peek())) {
				if (Characters.isLineBreak(chars.next())) {
					flags |= Token.FLAG_PRECEDEING_NEWLINE;
					//TODO: does this cause problems if we're backtracking?
					//TODO: Only one newline for '\r\n'
					this.lines.putNewline(chars.position());
				}
				continue;
			}

			chars.mark();
			long start = Math.max(this.getPositionOffset(), -1);
			JSSyntaxKind kind = this.getTokenHint();
			if (kind == null) {
				//TODO: handle?
				Objects.requireNonNull(kind);
			}
			
			switch (kind) {
				case NUMERIC_LITERAL:
					return this.finishNumericLiteralToken(start, flags);
				case STRING_LITERAL:
					return this.finishStringLiteralToken(start, flags);
				case TEMPLATE_LITERAL:
					return this.finishTemplateToken(start, flags);
				case IDENTIFIER:
					String name = this.nextIdentifier();
					if (name == null) {
						//Couldn't even parse an identifier
						throw new JSSyntaxException("Illegal syntax", this.resolvePosition(start));
					}
					kind = this.lookupKeyword(name);
					if (kind != null)
						return this.finishToken(start, flags, kind);
					return this.finishIdentifierToken(start, flags, name);
				case COMMENT:
					//TODO: track comments
					this.nextComment(chars.peek(2) != '*');
					this.unmark();
					continue;
				default:
					if (kind.length() < 0)
						//TODO: better exception type
						throw new IllegalStateException(""+kind);
					chars.skip(kind.length());
					return this.finishToken(start, flags, kind);
			}
		}
	}
	
	public Token nextToken() {
		//Return lookahead if available
		if (this.lookahead != null) {
			Token result = this.lookahead;
			//EOF token is sticky
			if (!result.matches(JSSyntaxKind.END_OF_FILE))
				skip(result);
			return result;
		}
		return this.readToken();
	}
	
	/**
	 * Take & return next token if it matches the provided kind and value.
	 * @param kind
	 * @return next token if it matches, else null
	 */
	public Token nextTokenIf(JSSyntaxKind kind) {
		Token lookahead = peek();
		if (lookahead.getKind() == kind) {
			skip(lookahead);
			return lookahead;
		}
		return null;
	}
	
	public Token nextTokenIfAny(JSSyntaxKind first, JSSyntaxKind...rest) {
		Token lookahead = peek();
		if (lookahead.matchesAny(first, rest))
			return this.skip(lookahead);
		return null;
	}
	
	public Token nextTokenIf(Predicate<Token> acceptor) {
		Token lookahead = peek();
		if (acceptor.test(lookahead)) {
			skip(lookahead);
			return lookahead;
		}
		return null;
	}
	

	/**
	 * Take the next token iff it matches the provided kind & value. If it matches, consume it
	 * @param kind
	 * @return if the next token matches
	 * @see #nextTokenIf(JSSyntaxKind)
	 */
	public boolean nextTokenIs(JSSyntaxKind kind) {
		return this.nextTokenIf(kind) != null;
	}
	
	public boolean nextTokenIsAny(JSSyntaxKind first, JSSyntaxKind...rest) {
		return this.nextTokenIfAny(first, rest) != null;
	}
	
	public Token peek() {
		if (this.lookahead != null)
			return this.lookahead;
		chars.mark();
		this.lookahead = this.readToken();
		chars.resetToMark();
		return this.lookahead;
	}
	
	public Token peek(int ahead) {
		if (ahead == 0)
			return peek();
		if (ahead < 0)
			throw new IllegalArgumentException();
		if (this.lookaheads.size() < ahead) {
			// Get last-consumed offset
			Token last = this.lookaheads.isEmpty() ? this.lookahead : this.lookaheads.getLast();
			if (last.matches(JSSyntaxKind.END_OF_FILE))// EOF is sticky
				return last;
			
			chars.mark();
			chars.position(last.getEnd().getOffset());
			while (this.lookaheads.size() < ahead)
				this.lookaheads.add(this.readToken());
			chars.resetToMark();
		}
		return this.lookaheads.get(ahead - 1);
	}
	
	public Token skip(Token token) {
		if (token != this.lookahead)
			throw new IllegalArgumentException("Skipped token " + token + " is not lookahead");
		if (token.matches(JSSyntaxKind.END_OF_FILE))
			throw new IllegalStateException("Cannot skip EOF token " + token);
		
		this.lookahead = this.lookaheads.isEmpty() ? null : this.lookaheads.removeFirst();
		
		chars.position(token.getEnd().getOffset());
		return token;
	}
	
	public void mark() {
		chars.mark();
	}
	
	public void reset() {
		chars.resetToMark();
		this.invalidateLookaheads(chars.position());
	}
	
	public void unmark() {
		chars.unmark();
	}
	
	@Override
	public Token get() {
		return nextToken();
	}
	
	public static class TemplateTokenInfo {
		public final boolean head;
		public final boolean tail;
		public final String cooked;
		TemplateTokenInfo(boolean head, boolean tail, String cooked) {
			this.head = head;
			this.tail = tail;
			this.cooked = cooked;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.head, this.tail, this.cooked);
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (other == null || !(other instanceof TemplateTokenInfo))
				return false;
			
			TemplateTokenInfo o = (TemplateTokenInfo) other;
			return this.head == o.head && this.tail == o.tail && Objects.equals(this.cooked, o.cooked);
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append(this.getClass().getSimpleName())
					.append("{head=").append(this.head)
					.append(",tail=").append(this.tail)
					.append(",cooked=\"").append(this.cooked)
					.append("\"}").toString();
		}
	}
}