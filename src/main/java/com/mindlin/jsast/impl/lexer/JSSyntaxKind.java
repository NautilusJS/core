package com.mindlin.jsast.impl.lexer;

/**
 * JS/TS syntax groups that might be emitted by the lexer.
 * 
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators">developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators</a>
 */
public enum JSSyntaxKind {
	// Special
	SEMICOLON,
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
	BREAK,
	CASE,
	CATCH,
	CLASS,
	CONST,
	CONTINUE,
	DEBUGGER,
	DEFAULT,
	DELETE,
	DO,
	ELSE,
	ENUM,
	EXPORT,
	EXTENDS,
	FALSE,
	FINALLY,
	FOR,
	FUNCTION,
	IF,
	IMPORT,
	IN,
	INSTANCEOF,
	NEW,
	NULL,
	RETURN,
	SUPER,
	SWITCH,
	THIS,
	THROW,
	TRUE,
	TRY,
	TYPEOF,
	VAR,
	VOID,
	WHILE,
	WITH,
	
	// ===== Strict mode reserved =====
	IMPLEMENTS,
	INTERFACE,
	LET,
	PACKAGE,
	PRIVATE,
	PROTECTED,
	PUBLIC,
	STATIC,
	YIELD,
	
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
	REGEXP_LITERAL,
	BIGINT_LITERAL,
	// ===== Templates =====
	TEMPLATE_HEAD,
	TEMPLATE_MIDDLE,
	TEMPLATE_TAIL,
	;
	
	private JSSyntaxKind() {
		// TODO Auto-generated constructor stub
	}
	
	private JSSyntaxKind(String text) {
		
	}
}
