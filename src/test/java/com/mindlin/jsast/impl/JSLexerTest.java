package com.mindlin.jsast.impl;

import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.lexer.JSSyntaxKind;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.impl.lexer.Token.NumericLiteralToken;
import com.mindlin.jsast.impl.lexer.Token.StringLiteralToken;
import com.mindlin.jsast.impl.util.Characters;

public class JSLexerTest {
	
	protected static void assertToken(JSSyntaxKind kind, Token token) {
		assertEquals(kind, token.getKind());
	}
	
	protected static void assertNumericLiteral(Number expected, Token token) {
		assertNotNull(token);
		assertEquals(JSSyntaxKind.NUMERIC_LITERAL, token.getKind());
		Number value = ((NumericLiteralToken) token).getValue();
		assertNumberEquals(expected, value);
	}
	
	protected static void assertStringLiteral(String text, Token token) {
		assertNotNull(token);
		assertEquals(JSSyntaxKind.STRING_LITERAL, token.getKind());
		assertEquals(text, ((StringLiteralToken) token).getValue());
	}
	
	@Test
	public void testSimpleStringLiteral() {
		JSLexer lexer = new JSLexer("\"Hello, world\"");
		String nextd = lexer.nextStringLiteral();
		assertEquals("Hello, world", nextd);
	}
	
	@Test
	public void testStringLiteralEscapeSequence() {
		JSLexer lexer = new JSLexer("'\\r\\n\\\\\\\nf\\'\\\"\\``'");
		String nextd = lexer.nextStringLiteral();
		assertEquals("\r\n\\f'\"``", nextd);
	}
	
	@Test
	public void testStringLiteralNullEscape() {
		JSLexer lexer = new JSLexer("'\\09'");
		String next = lexer.nextStringLiteral();
		assertEquals("\09", next);
	}
	
	@Test
	public void testStringLiteralEASCIIHexEscape() {
		JSLexer lexer = new JSLexer("'\\x1f'");
		//Java doesn't support hex escapes, but 0o37 == 0x1F
		assertEquals("\37", lexer.nextStringLiteral());
	}
	
	@Test
	public void testStringLiteralEASCIIOctalEscape() {
		JSLexer lexer = new JSLexer("'\\1\\12\\123\\129'");
		String next = lexer.nextStringLiteral();
		assertEquals(5, next.length(), "Unexpected string literal length");
		assertEquals(Characters.SOH, next.charAt(0));
		assertEquals(Characters.LF, next.charAt(1));
		assertEquals('S', next.charAt(2));
		assertEquals(Characters.LF, next.charAt(3));
		assertEquals('9', next.charAt(4));
	}
	
	@Test
	public void testStringLiteralUnicodeEscape() {
		//U+2603 SNOWMAN
		JSLexer lexer = new JSLexer("'\\u2603'");
		String next = lexer.nextStringLiteral();
		assertEquals("\u2603", next);
	}
	
	@Test
	public void testStringLiteralUnicodeEscapeCP() {
		//U+10600 LINEAR A SIGN AB001
		JSLexer lexer = new JSLexer("'\\u{10600}'");
		String next = lexer.nextStringLiteral();
		assertEquals("\uD801\uDE00", next);
	}
	
	@Test
	public void testStringLiteralComplexNewline() {
		JSLexer lexer = new JSLexer(new StringBuilder()
				.append('"').append('\\').append('\n').append('"')
				.append('"').append('\\').append('\r').append('"')
				.append('"').append('\\').append('\r').append('\n').append('"')
				.append('"').append('\\').append('\n').append('\r').append('"')
				.toString());
		assertEquals("", lexer.nextStringLiteral());
		assertEquals("", lexer.nextStringLiteral());
		assertEquals("", lexer.nextStringLiteral());
		assertEquals("", lexer.nextStringLiteral());
	}
	
	@Test
	public void testStringLiteralComplexQuotes() {
		JSLexer lexer = new JSLexer(new StringBuilder(10)
				.append('"').append('\'').append('f').append('\'').append('"')
				.append('\'').append('"').append('g').append('"').append('\'')
				.toString());
		assertEquals("'f'", lexer.nextStringLiteral());
		assertEquals("\"g\"", lexer.nextStringLiteral());
	}
	
	@Test
	public void testNumericLiteralZero() {
		JSLexer lexer = new JSLexer("0 00 0e0 00e0 00e999");
		//0
		assertEquals(0, lexer.nextNumericLiteral().intValue());
		//00
		assertEquals(0, lexer.nextNumericLiteral().intValue());
		//0e0
		assertEquals(0, lexer.nextNumericLiteral().intValue());
		//00e0
		assertEquals(0, lexer.nextNumericLiteral().intValue());
		//00e999
		assertEquals(0, lexer.nextNumericLiteral().intValue());
	}
	
	@Test
	public void testNumericLiteralBinary() {
		// Check case invariance
		{
			JSLexer lexer = new JSLexer("0b1010");
			assertEquals(0b1010, lexer.nextNumericLiteral().intValue());
		}
		{
			JSLexer lexer = new JSLexer("0B1010");
			Number n = lexer.nextNumericLiteral();
			assertEquals(0b1010, n.intValue());
		}
		{
			//Check higher numbers
			JSLexer lexer = new JSLexer("0b102");
			Assertions.assertThrows(
					JSSyntaxException.class,
					() -> lexer.nextNumericLiteral(),
					"Failed to throw exception on invalid syntax");
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("0b0001 0b0010;0b0011\n0b0100\r0b0101\r0b0110");
			assertNumberEquals(0b0001, lexer.nextNumericLiteral());
			assertNumberEquals(0b0010, lexer.nextNumericLiteral());
			assertNumberEquals(0b0011, lexer.nextNumericLiteral());
			assertNumberEquals(0b0100, lexer.nextNumericLiteral());
			assertNumberEquals(0b0101, lexer.nextNumericLiteral());
			assertNumberEquals(0b0110, lexer.nextNumericLiteral());
		}
	}
	
	@Test
	public void testNumericLiteralDecimal() {
		{
			JSLexer lexer = new JSLexer("1010");
			assertEquals(1010, lexer.nextNumericLiteral().intValue());
		}
		{
			//Check invalid other characters numbers
			JSLexer lexer = new JSLexer("10A");
			Assertions.assertThrows(
					JSSyntaxException.class,
					() -> lexer.nextNumericLiteral(),
					"Failed to throw exception on invalid character in decimal literal");
		}
		{
			//Check octal upgrade
			JSLexer lexer = new JSLexer("01019");
			assertNumberEquals(1019L, lexer.nextNumericLiteral(), null);
		}
		{
			//Check decimals starting with 0
			JSLexer lexer = new JSLexer("0.1");
			assertNumberEquals(0.1, lexer.nextNumericLiteral(), null);
		}
		{
			//Check decimals starting with '.'
			JSLexer lexer = new JSLexer(".1");
			assertNumberEquals(0.1, lexer.nextNumericLiteral(), null);
		}
		{
			//Check octal upgrade with decimal
			JSLexer lexer = new JSLexer("0101.");
			assertNumberEquals(101, lexer.nextNumericLiteral(), null);
		}
		{
			//Check all digits are supported
			JSLexer lexer = new JSLexer("1234567890");
			assertNumberEquals(1234567890L, lexer.nextNumericLiteral());
		}
		{
			//Check decimals
			JSLexer lexer = new JSLexer("32.5");
			assertNumberEquals(32.5, lexer.nextNumericLiteral());
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("1 10;11\n100\r101\r110");
			assertNumberEquals(  1, lexer.nextNumericLiteral());
			assertNumberEquals( 10, lexer.nextNumericLiteral());
			assertNumberEquals( 11, lexer.nextNumericLiteral());
			assertNumberEquals(100, lexer.nextNumericLiteral());
			assertNumberEquals(101, lexer.nextNumericLiteral());
			assertNumberEquals(110, lexer.nextNumericLiteral());
		}
	}
	
	@Test
	public void testDecimalLiteralExponent() {
		JSLexer lexer = new JSLexer("1.0e3 .1e4 1e3");
		assertEquals(1.0e3, lexer.nextNumericLiteral().doubleValue(), .0001);
		assertEquals(1.0e3, lexer.nextNumericLiteral().doubleValue(), .0001);
		assertEquals(1.0e3, lexer.nextNumericLiteral().doubleValue(), .0001);
	}
	
	@Test
	public void testOctalUpgrade() {
		//Test implicit octal
		assertNumberEquals(076543210, new JSLexer("076543210").nextNumericLiteral(), "Incorrectly parsed implicit octal");
		//Test implicit upgrade
		assertNumberEquals(876543210, new JSLexer("0876543210").nextNumericLiteral(), "Failed to upgrade implicit octal");
		//Test explicit octal
		assertNumberEquals(076543210, new JSLexer("0o76543210").nextNumericLiteral(), "Incorrectly nextd explicit octal");
		//Test explicit upgrade fail
	}
	
	@Test
	public void testIllegalExplicitOctalUpgrade() {
		Assertions.assertThrows(JSSyntaxException.class, () -> new JSLexer("0o876543210").nextNumericLiteral(), "Explicit octal literal can't be ugraded");
	}
	@Test
	public void testNumericLiteralHexdecimal() {
		{
			JSLexer lexer = new JSLexer("0x0123456789ABCDEF");
			assertNumberEquals(0x0123456789ABCDEFL, lexer.nextNumericLiteral());
		}
		{
			//Check case insensitivity
			JSLexer lexer = new JSLexer("0xabcdefABCDEF");
			assertNumberEquals(0xABCDEFABCDEFL, lexer.nextNumericLiteral());
		}
		{
			//Check invalid other characters numbers
			JSLexer lexer = new JSLexer("0x10AG");
			Assertions.assertThrows(
					JSSyntaxException.class,
					() -> lexer.nextNumericLiteral(),
					"Failed to throw exception on invalid character in hex literal");
		}
		{
			//Check header only
			JSLexer lexer = new JSLexer("0x");
			Assertions.assertThrows(
					JSSyntaxException.class,
					() -> lexer.nextNumericLiteral(),
					"Failed to throw exception on hex literal body missing");
		}
		{
			//Check termination
			JSLexer lexer = new JSLexer("0x1 0x10;0x11\n0x100\r0x101\r0x110");
			assertNumberEquals(  0x1, lexer.nextNumericLiteral());
			assertNumberEquals( 0x10, lexer.nextNumericLiteral());
			assertNumberEquals( 0x11, lexer.nextNumericLiteral());
			assertNumberEquals(0x100, lexer.nextNumericLiteral());
			assertNumberEquals(0x101, lexer.nextNumericLiteral());
			assertNumberEquals(0x110, lexer.nextNumericLiteral());
		}
	}
	
	@Test
	public void testEOF() {
		JSLexer lexer = new JSLexer("'foo'");
		lexer.nextToken();
		
		assertTrue(lexer.isEOF());
		Token eofToken = lexer.nextToken();
		assertEquals(JSSyntaxKind.END_OF_FILE, eofToken.getKind());
	}
	
	@Test
	public void testTokenize() {
		JSLexer lexer = new JSLexer("'bar' for asdd 0xFF");
		
		Token fooStringToken = lexer.nextToken();
		assertStringLiteral("bar", fooStringToken);
		
		Token forKeywordToken = lexer.nextToken();
		assertToken(JSSyntaxKind.FOR, forKeywordToken);
		
		Token asddIdentifierToken = lexer.nextToken();
		assertEquals(JSSyntaxKind.IDENTIFIER, asddIdentifierToken.getKind());
		assertEquals("asdd", asddIdentifierToken.getValue());
		
		Token FFNumberToken = lexer.nextToken();
		assertNumericLiteral(0xFF, FFNumberToken);
		
		Token EofNumberToken = lexer.nextToken();
		assertEquals(JSSyntaxKind.END_OF_FILE, EofNumberToken.getKind());
	}
	
	@Test
	public void testTemplateLiteralNewline() {
		assertEquals("foo\nbar", new JSLexer("`foo\nbar`").nextStringLiteral());
	}
	
	/**
	 * Test that the lexer correctly throws an exception upon an EOF in the
	 * middle of the string literal
	 */
	@Test
	public void testStringLiteralEOF() {
		Assertions.assertThrows(
				JSSyntaxException.class,
				() -> new JSLexer("'foo").nextStringLiteral(),
				"Failed to throw exception upon unexpected EOF");
		
		Assertions.assertThrows(
				JSSyntaxException.class,
				() -> new JSLexer("'foo\\'").nextStringLiteral(),
				"Failed to throw exception upon unexpected EOF");
	}
	
	@Test
	public void testBooleanLiteral() {
		JSLexer lexer = new JSLexer("true false");
		Token trueToken = lexer.nextToken();
		assertEquals(JSSyntaxKind.TRUE, trueToken.getKind());
		assertEquals("true", trueToken.getText());
		
		Token falseToken = lexer.nextToken();
		assertEquals(JSSyntaxKind.FALSE, falseToken.getKind());
		assertEquals("false", falseToken.getText());
	}
	
	@Test
	public void testYield() {
		JSLexer lexer = new JSLexer("yield yield* yield *");
		
		Token next = lexer.nextToken();
		assertEquals(JSSyntaxKind.YIELD, next.getKind());
		
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.YIELD, next.getKind());
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.ASTERISK, next.getKind());
		
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.YIELD, next.getKind());
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.ASTERISK, next.getKind());
	}
	
	@Test
	public void testGenerator() {
		JSLexer lexer = new JSLexer("function function* function *");
		Token next = lexer.nextToken();
		assertToken(JSSyntaxKind.FUNCTION, next);
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.FUNCTION, next.getKind());
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.ASTERISK, next.getKind());
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.FUNCTION, next.getKind());
		
		next = lexer.nextToken();
		assertEquals(JSSyntaxKind.ASTERISK, next.getKind());
	}

	@Test
	public void testMarkReset() {
		JSLexer lexer = new JSLexer("1 2 3 4");
		assertNumericLiteral(1, lexer.peek());
		
		assertNumericLiteral(1, lexer.nextToken());
		
		lexer.mark();
		assertNumericLiteral(2, lexer.peek());
		assertNumericLiteral(2, lexer.nextToken());
		assertNumericLiteral(3, lexer.peek());
		lexer.reset();
		assertNumericLiteral(2, lexer.peek());
		
	}
	
	@Test
	public void testLookaheadMultiple() {
		JSLexer lexer = new JSLexer("1 2 3 4");
		assertNumericLiteral(1, lexer.peek());
		assertNumericLiteral(1, lexer.peek(0));
		assertNumericLiteral(2, lexer.peek(1));
		assertNumericLiteral(3, lexer.peek(2));
		assertNumericLiteral(4, lexer.peek(3));
		
		assertNumericLiteral(1, lexer.nextToken());
		
		lexer.mark();
		assertNumericLiteral(2, lexer.peek());
		assertNumericLiteral(3, lexer.peek(1));
		assertNumericLiteral(4, lexer.peek(2));
		
		assertNumericLiteral(2, lexer.nextToken());
		assertNumericLiteral(3, lexer.peek());
		lexer.reset();
		assertNumericLiteral(2, lexer.peek());
	}
	
	@Test
	public void testCompleteUpdateTokens() {
		JSLexer lexer = new JSLexer("++x--");
		
		assertToken(JSSyntaxKind.INCREMENT, lexer.nextToken());
		assertToken(JSSyntaxKind.IDENTIFIER, lexer.nextToken());
		assertToken(JSSyntaxKind.DECREMENT, lexer.nextToken());
	}
}
