package com.mindlin.jsast.impl.lexer;

public class JSLexerMessages {
	public static final String INVALID_CHARACTER = "Invalid character '%c'";
	public static final String HEX_DIGIT_EXPECTED = "Hex digit expected";
	public static final String N_HEX_DIGITS_EXPECTED = "%d hex digit(s) expected";
	public static final String INVALID_CHARACTER_IN_UNICODE_ESCAPE = "Invalid character '%c' in unicode code point escape sequence";
	public static final String UNICODE_ESCAPE_EOF = "Unexpected EOF in unicode code point escape sequence";
	public static final String UNTERMINATED_UNICODE_ESCAPE = "Unterminated unicode escape sequence";
	public static final String INVALID_UNICODE_CODE_POINT = "Invalid Unicode code point: %#x (must be 0...0x10FFFF)";
	public static final String EASCII_EOF = "Invalid Extended ASCII escape sequence (EOF)";
	public static final String CONSECUTIVE_NUMERIC_SEPARATORS = "Numeric separators may not be consecutive";
	public static final String NUMERIC_SEPARATORS_DISALLOWED = "Numeric separators aren't allowed here";
	
	public static final String format(String pattern, Object...args) {
		return String.format(pattern, args);
	}
}
