package com.mindlin.jsast.impl.parser;

public enum JSOperator {
	// Reference:
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators
	AT_SYMBOL("@"),
	EQUAL("=="),
	NOT_EQUAL("!="),
	STRICT_EQUAL("==="),
	STRICT_NOT_EQUAL("!=="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN_EQUAL("<="),
	LESS_THAN("<"),

	INCREMENT("++"),
	DECREMENT("--"),

	PLUS("+"),
	MINUS("-"),
	ASTERISK("*"),
	DIVISION("/"),
	REMAINDER("%"),
	EXPONENTIATION("**"),
	LEFT_SHIFT("<<"),
	RIGHT_SHIFT(">>"),
	UNSIGNED_RIGHT_SHIFT(">>>"),

	AMPERSAND("&"),
	BITWISE_XOR("^"),
	VBAR("|"),
	BITWISE_NOT("~"),

	LOGICAL_AND("&&"),
	LOGICAL_OR("||"),
	LOGICAL_NOT("!"),

	ASSIGNMENT(true, "="),
	ADDITION_ASSIGNMENT(true, "+="),
	SUBTRACTION_ASSIGNMENT(true, "-="),
	MULTIPLICATION_ASSIGNMENT(true, "*="),
	DIVISION_ASSIGNMENT(true, "/="),
	REMAINDER_ASSIGNMENT(true, "%="),
	EXPONENTIATION_ASSIGNMENT(true, "**="),
	LEFT_SHIFT_ASSIGNMENT(true, "<<="),
	RIGHT_SHIFT_ASSIGNMENT(true, ">>="),
	UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(true, ">>>="),
	BITWISE_AND_ASSIGNMENT(true, "&="),
	BITWISE_XOR_ASSIGNMENT(true, "^="),
	BITWISE_OR_ASSIGNMENT(true, "|="),

	QUESTION_MARK("?"),
	COLON(":"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),
	LEFT_BRACKET("["),
	RIGHT_BRACKET("]"),
	LEFT_BRACE("{"),
	RIGHT_BRACE("}"),
	COMMA(","),
	LAMBDA("=>"),

	PERIOD("."),
	SPREAD("..."),;

	final String operator;
	final int precedence;
	final boolean assignment;

	JSOperator(boolean assignment, int precedence, String operator) {
		this.assignment = assignment;
		this.operator = operator;
		this.precedence = precedence;
	}

	JSOperator(boolean assignment, String operator) {
		this(assignment, -1, operator);
	}

	JSOperator(int precedence, String operator) {
		this(false, precedence, operator);
	}

	JSOperator(String operator) {
		this(-1, operator);
	}
	
	JSOperator() {
		this(true, -1, "");
	}
	
	public String getText() {
		return operator;
	}

	public int length() {
		return operator.length();
	}

	public boolean isAssignment() {
		return this.assignment;
	}

	public int precedence() {
		return this.precedence;
	}
}
