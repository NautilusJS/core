package com.mindlin.jsast.impl.parser;

import java.util.EnumSet;
import java.util.function.Predicate;

import com.mindlin.jsast.impl.lexer.JSSyntaxKind;
import com.mindlin.jsast.impl.lexer.Token;

/**
 * Commonly used predicates for matching certain classes of tokens
 * @author mailmindlin
 */
public class TokenPredicate {
	public static Predicate<Token> match(JSSyntaxKind kind) {
		return t -> t.matches(kind);
	}
	
	public static Predicate<Token> match(JSSyntaxKind first, JSSyntaxKind second) {
		return t -> t.matchesAny(first, second);
	}
	
	public static Predicate<Token> match(JSSyntaxKind first, JSSyntaxKind...rest) {
		if (rest.length < 8)
			return t -> t.matchesAny(first, rest);
		EnumSet<JSSyntaxKind> set = EnumSet.of(first, rest);
		return t -> set.contains(t.getKind());
	}
	
	public static final Predicate<Token> VARIABLE_START = match(JSSyntaxKind.VAR, JSSyntaxKind.LET, JSSyntaxKind.CONST);
	public static final Predicate<Token> IN_OR_OF = match(JSSyntaxKind.IN, JSSyntaxKind.OF);
	public static final Predicate<Token> PARAMETER_TYPE_START = match(JSSyntaxKind.QUESTION_MARK, JSSyntaxKind.COLON, JSSyntaxKind.ASSIGNMENT);
	public static final Predicate<Token> CALL_SIGNATURE_START = match(JSSyntaxKind.LEFT_PARENTHESIS, JSSyntaxKind.LESS_THAN);
	public static final Predicate<Token> START_OF_PARAMETER = match(JSSyntaxKind.IDENTIFIER, JSSyntaxKind.LEFT_BRACE, JSSyntaxKind.LEFT_BRACKET, JSSyntaxKind.SPREAD);
	public static final Predicate<Token> TYPE_CONTINUATION = match(JSSyntaxKind.VBAR, JSSyntaxKind.AMPERSAND);
	public static final Predicate<Token> SWITCH_MEMBER_START = match(JSSyntaxKind.CASE, JSSyntaxKind.DEFAULT);
	public static final Predicate<Token> CAN_FOLLOW_MODIFIER = match(JSSyntaxKind.ASTERISK, JSSyntaxKind.SPREAD, JSSyntaxKind.LEFT_BRACKET, JSSyntaxKind.LEFT_BRACE, JSSyntaxKind.STRING_LITERAL, JSSyntaxKind.NUMERIC_LITERAL, JSSyntaxKind.IDENTIFIER /* keywords */);
	public static final Predicate<Token> ASSIGNMENT = match(JSSyntaxKind.ASSIGNMENT, JSSyntaxKind.ADDITION_ASSIGNMENT, JSSyntaxKind.SUBTRACTION_ASSIGNMENT, JSSyntaxKind.MULTIPLICATION_ASSIGNMENT, JSSyntaxKind.DIVISION_ASSIGNMENT, JSSyntaxKind.REMAINDER_ASSIGNMENT, JSSyntaxKind.EXPONENTIATION_ASSIGNMENT, JSSyntaxKind.LEFT_SHIFT_ASSIGNMENT, JSSyntaxKind.RIGHT_SHIFT_ASSIGNMENT, JSSyntaxKind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT, JSSyntaxKind.BITWISE_AND_ASSIGNMENT, JSSyntaxKind.BITWISE_OR_ASSIGNMENT, JSSyntaxKind.BITWISE_XOR_ASSIGNMENT);
	
	public static final Predicate<Token> HERITAGE_START = match(JSSyntaxKind.EXTENDS, JSSyntaxKind.IMPLEMENTS);
	public static final Predicate<Token> UPDATE_OPERATOR = match(JSSyntaxKind.INCREMENT, JSSyntaxKind.DECREMENT);
	public static final Predicate<Token> RIGHT_BRACE = match(JSSyntaxKind.RIGHT_BRACE);
	public static final Predicate<Token> START_OF_DESTRUCTURING = match(JSSyntaxKind.LEFT_BRACE, JSSyntaxKind.LEFT_BRACKET);
	
	/**
	 * TokenPredicate can't be instantiated
	 */
	private TokenPredicate() {}
}
