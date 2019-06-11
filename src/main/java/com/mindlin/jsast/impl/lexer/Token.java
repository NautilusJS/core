package com.mindlin.jsast.impl.lexer;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.mindlin.jsast.exception.JSUnexpectedTokenException;
import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.fs.SourceRange;

@NonNullByDefault
public class Token {
	public static final int FLAG_PRECEDEING_NEWLINE = 1 << 0;
	public static final int FLAG_PRECEDING_JSDOC = 1 << 1;
	//TODO: use EnumSet-type wrapper in the future?
	protected final int flags;
	protected final JSSyntaxKind kind;
	protected final SourceRange range;
	protected final CharSequence text;
	
	public Token(int flags, SourceRange range, JSSyntaxKind kind, CharSequence text) {
		this.flags = flags;
		this.range = Objects.requireNonNull(range);
		this.kind = Objects.requireNonNull(kind);
		this.text = text;
	}
	
	public boolean hasPrecedingNewline() {
		return (this.flags & FLAG_PRECEDEING_NEWLINE) != 0;
	}
	
	public boolean hasPrecedingJSDoc() {
		return (this.flags & FLAG_PRECEDING_JSDOC) != 0;
	}
	
	public SourceRange getRange() {
		return this.range;
	}

	public SourcePosition getStart() {
		return this.range.getStart();
	}

	public SourcePosition getEnd() {
		return this.range.getEnd();
	}

	public CharSequence getText() {
		return this.text;
	}

	public JSSyntaxKind getKind() {
		return this.kind;
	}

	public Object getValue() {
		return null;
	}

	public boolean isIdentifier() {
		return this.getKind() == JSSyntaxKind.IDENTIFIER;
	}
	
	public boolean hasFlag(int flag) {
		//TODO: enum-based impl?
		return (this.flags & flag) != 0;
	}
	
	public boolean matches(JSSyntaxKind kind) {
		return getKind() == kind;
	}
	
	public boolean matchesAny(JSSyntaxKind first, JSSyntaxKind second) {
		JSSyntaxKind kind = this.getKind();
		return kind == first || kind == second;
	}
	
	public boolean matchesAny(JSSyntaxKind first, JSSyntaxKind...rest) {
		JSSyntaxKind kind = this.getKind();
		if (kind == first)
			return true;
		for (int i = 0; i < rest.length; i++)
			if (kind == rest[i])
				return true;
		return false;
	}
	
	public boolean matchesIdentifier(String text) {
		//TODO: finish
		return false;
	}

	public IdentifierToken reinterpretAsIdentifier() {
		String name = this.getKind().getText();
		if (name == null)
			throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		return new IdentifierToken(this.flags, this.range, this.getText(), name);
	}

	public void expect(JSSyntaxKind kind) {
		if (this.getKind() != kind)
			throw new JSUnexpectedTokenException(this, kind);
	}

	@Override
	public String toString() {
		//@formatter:off
		StringBuilder sb = new StringBuilder(70)//High end of expected outputs
				.append(this.getClass().getSimpleName())
				.append("{kind=").append(getKind())
				.append(",value=").append(getValue())
				.append(",range=").append(getRange());
		//@formatter:on

		if (getText() == null)
			sb.append(",text=null");
		else
			sb.append(",text=\"").append(getText()).append('"');

		sb.append('}');
		return sb.toString();
	}
	
	public static class IdentifierToken extends Token {
		protected final String name;
		
		public IdentifierToken(int flags, SourceRange range, CharSequence text, String name) {
			super(flags, range, JSSyntaxKind.IDENTIFIER, text);
			this.name = name;
		}
		
		@Override
		public boolean matchesIdentifier(String text) {
			return Objects.equals(this.getValue(), text);
		}
		
		@Override
		public String getValue() {
			return this.name;
		}
		
		@Override
		public IdentifierToken reinterpretAsIdentifier() {
			return this;
		}
		
	}
	
	public static class StringLiteralToken extends Token {
		protected final String value;
		
		public StringLiteralToken(int flags, SourceRange range, CharSequence text, String value) {
			super(flags, range, JSSyntaxKind.STRING_LITERAL, text);
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return this.value;
		}
		
		@Override
		public IdentifierToken reinterpretAsIdentifier() {
			throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		}
	}
	
	public static class NumericLiteralToken extends Token {
		protected final Number value;
		
		public NumericLiteralToken(int flags, SourceRange range, CharSequence text, Number value) {
			super(flags, range, JSSyntaxKind.NUMERIC_LITERAL, text);
			this.value = value;
		}
		
		@Override
		public Number getValue() {
			return this.value;
		}
		
		@Override
		public IdentifierToken reinterpretAsIdentifier() {
			throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		}
	}
	
	@NonNullByDefault
	public static class RegExpToken extends Token {
		protected final String pattern;
		protected final String rxFlags;
		
		public RegExpToken(int flags, SourceRange range, CharSequence text, String pattern, String rxFlags) {
			super(flags, range, JSSyntaxKind.REGEX_LITERAL, text);
			this.pattern = pattern;
			this.rxFlags = rxFlags;
		}

		public String getPattern() {
			return this.pattern;
		}

		public String getRxFlags() {
			return this.rxFlags;
		}
		
		@Override
		public IdentifierToken reinterpretAsIdentifier() {
			throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		}
	}
	
	public static class TemplateLiteralToken extends Token {
		protected final boolean head;
		protected final boolean tail;
		protected final String cooked;
		
		public TemplateLiteralToken(int flags, SourceRange range, CharSequence text, boolean head, boolean tail, String cooked) {
			super(flags, range, JSSyntaxKind.TEMPLATE_LITERAL, text);
			this.head = head;
			this.tail = tail;
			this.cooked = cooked;
		}
		
		@Override
		public IdentifierToken reinterpretAsIdentifier() {
			throw new UnsupportedOperationException(this + " cannot be reinterpreted as an identifier");
		}

		public boolean isHead() {
			return this.head;
		}

		public boolean isTail() {
			return this.tail;
		}

		public String getCooked() {
			return this.cooked;
		}
	}
}