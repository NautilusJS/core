package com.mindlin.jsast.impl.lexer;

/**
 * JS/TS syntax groups that might be emitted by the lexer.
 * 
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators">developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators</a>
 */
public enum JSSyntaxKind {
	// Special
	SEMICOLON,
	@Deprecated
	END_OF_LINE,
	END_OF_FILE,
	IDENTIFIER,
	COMMENT,
	
	// ===== Keywords =====
	ABSTRACT,
	AS,
	ASYNC,
	AWAIT,
	CONSTRUCTOR,
	DECLARE,
	FROM,
	OF,
	READONLY,
	TYPE,
	
	// ===== Reserved words =====
	BREAK("break"),
	CASE("case"),
	CATCH("catch"),
	CLASS("class"),
	CONST("const"),
	CONTINUE("continue"),
	DEBUGGER("debugger"),
	DEFAULT("default"),
	DELETE("delete"),
	DO("do"),
	ELSE("else"),
	ENUM("enum"),
	EXPORT("export"),
	EXTENDS("extends"),
	FALSE("false"),
	FINALLY("finally"),
	FOR("for"),
	FUNCTION("function"),
	IF("if"),
	IMPORT("import"),
	IN("in"),
	INSTANCEOF("instanceof"),
	NEW("new"),
	NULL("null"),
	RETURN("return"),
	SUPER("super"),
	SWITCH("switch"),
	THIS("this"),
	THROW("throw"),
	TRUE("true"),
	TRY("try"),
	TYPEOF("typeof"),
	VAR("var"),
	VOID("void"),
	WHILE("while"),
	WITH("with"),
	
	// ===== Strict mode reserved =====
	IMPLEMENTS("implements"),
	INTERFACE("interface"),
	LET("let"),
	PACKAGE("package"),
	PRIVATE("private"),
	PROTECTED("protected"),
	PUBLIC("public"),
	STATIC("static"),
	YIELD("yield"),
	
	// ===== Operators =====
	// ===== Syntax ====
	AT_SYMBOL("@"),
	LEFT_BRACE("{"),
	RIGHT_BRACE("}"),
	
	// Comparison ops
	EQUAL("=="),
	NOT_EQUAL("!="),
	STRICT_EQUAL("==="),
	STRICT_NOT_EQUAL("!=="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">="),
	LESS_THAN_EQUAL("<="),
	LESS_THAN("<"),

	// Update ops
	INCREMENT("++"),
	DECREMENT("--"),

	// Unary ops
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

	ASSIGNMENT("="),
	ADDITION_ASSIGNMENT("+="),
	SUBTRACTION_ASSIGNMENT("-="),
	MULTIPLICATION_ASSIGNMENT("*="),
	DIVISION_ASSIGNMENT("/="),
	REMAINDER_ASSIGNMENT("%="),
	EXPONENTIATION_ASSIGNMENT("**="),
	LEFT_SHIFT_ASSIGNMENT("<<="),
	RIGHT_SHIFT_ASSIGNMENT(">>="),
	UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(">>>="),
	BITWISE_AND_ASSIGNMENT("&="),
	BITWISE_XOR_ASSIGNMENT("^="),
	BITWISE_OR_ASSIGNMENT("|="),

	QUESTION_MARK("?"),
	COLON(":"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),
	LEFT_BRACKET("["),
	RIGHT_BRACKET("]"),
	COMMA(","),
	LAMBDA("=>"),

	PERIOD("."),
	SPREAD("..."),
	
	// ===== Literals =====
	STRING_LITERAL,
	NUMERIC_LITERAL,
	REGEX_LITERAL,
	BIGINT_LITERAL,
	TEMPLATE_LITERAL,
	;
	
	private final String text;
	
	private JSSyntaxKind() {
		this(null);
	}
	
	private JSSyntaxKind(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public int length() {
		return text == null ? -1 : text.length();
	}
}
