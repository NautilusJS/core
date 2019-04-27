package com.mindlin.jsast.impl.util;

import java.util.Arrays;

public final class Characters {
	public static final char NULL                  = '\0';
	public static final char SOH                   = '\u0001';
	public static final char STX                   = '\u0002';
	public static final char ETX                   = '\u0003';
	public static final char EOT                   = '\u0004';
	public static final char ENQ                   = '\u0005';
	public static final char ACK                   = '\u0006';
	public static final char BEL                   = '\u0007';
	public static final char BS                    = '\u0008';
	public static final char TAB                   = '\u0009';
	public static final char LF                    = '\n';
	public static final char VT                    = '\u000b';
	public static final char FF                    = '\u000c'; // Form feed (ctrl+l)
	public static final char CR                    = '\r';
	public static final char SO                    = '\u000e';
	public static final char SI                    = '\u000f';
	public static final char SPACE                 = '\u0020';
	public static final char NBSP                  = '\u00a0'; // Latin-1 space
	public static final char OGHAM                 = '\u1680';
	public static final char EN_QUAD               = '\u2000';
	public static final char EM_QUAD               = '\u2001';
	public static final char EN_SPACE              = '\u2002';
	public static final char EM_SPACE              = '\u2003';
	public static final char THREE_PER_EM_SPACE    = '\u2004';
	public static final char FOUR_PER_EM_SPACE     = '\u2005';
	public static final char SIX_PER_EM_SPACE      = '\u2006';
	public static final char FIGURE_SPACE          = '\u2007';
	public static final char PUNCTUATION_SPACE     = '\u2008';
	public static final char THIN_SPACE            = '\u2009';
	public static final char HAIR_SPACE            = '\u200a';
	public static final char ZERO_WIDTH_SPACE      = '\u200b';
	public static final char LINE_SEPARATOR        = '\u2028';
	public static final char PARAGRAPH_SEPARATOR   = '\u2029';
	public static final char NARROW_NO_BREAK_SPACE = '\u202f';
	public static final char MEDIUM_MATH_SPACE     = '\u205f';
	public static final char IDEOGRAPHIC_SPACE     = '\u3000';
	public static final char BYTE_ORDER_MARK       = '\ufeff';
	
	/**
	 * Array of JS whitespace chars.
	 * 
	 * Must be sorted
	 */
	private static final char[] JS_WHITESPACE = new char[] {
			TAB,
			LF,
			VT,
			FF,
			CR,
			SPACE,
			NBSP,
			OGHAM,
			EN_QUAD,
			EM_QUAD,
			EN_SPACE,
			EM_SPACE,
			THREE_PER_EM_SPACE,
			FOUR_PER_EM_SPACE,
			SIX_PER_EM_SPACE,
			FIGURE_SPACE,
			PUNCTUATION_SPACE,
			THIN_SPACE,
			HAIR_SPACE,
			ZERO_WIDTH_SPACE,
			LINE_SEPARATOR,
			PARAGRAPH_SEPARATOR,
			NARROW_NO_BREAK_SPACE,
			MEDIUM_MATH_SPACE,
			IDEOGRAPHIC_SPACE,
			BYTE_ORDER_MARK,
	};
	
	public static final int ZWNJ = 0x200C;
	public static final int ZWJ = 0x200D;
	
	public static boolean isLineBreak(final char c) {
		switch (c) {
			case Characters.LF:
			case Characters.CR:
			case Characters.LINE_SEPARATOR:
			case Characters.PARAGRAPH_SEPARATOR:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isJsWhitespace(final char c) {
		return Arrays.binarySearch(JS_WHITESPACE, c) >= 0;
	}
	
	public static boolean isDecimalDigit(char c) {
		return '0' <= c && c <= '9';
	}
	
	public static boolean canStartNumber(char c) {
		return isDecimalDigit(c) || (c == '.') || (c == '+') || (c == '-');
	}
	
	public static boolean isHexDigit(char c) {
		return ('0' <= c) && ((c <= '9') || (('a' <= c) && (c <= 'f')) || (('A' <= c) && (c <= 'F'))); 
	}
	
	public static boolean canStartIdentifier(final int c) {
		return Character.isJavaIdentifierStart(c);
	}
	
	public static boolean isIdentifierPart(final int c) {
		return Character.isUnicodeIdentifierPart(c) || Character.getType(c) == Character.CURRENCY_SYMBOL || c == '_' || c == ZWNJ || c == ZWJ;
	}

	/**
	 * Converts a character to a hexdecimal digit. If passed character is not a
	 * hexdecimal character (not {@code [0-9a-fA-F]}), this method returns
	 * -1.
	 * 
	 * @param c
	 *            Digit character to parse
	 * @return Value of hex digit ({@code 0-16}), or -1 if invalid.
	 */
	public static int asHexDigit(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		if (c >= 'a' && c <= 'f')
			return c - 'a' + 10;
		if (c >= 'A' && c <= 'F')
			return c - 'A' + 10;
		return -1;
	}
}