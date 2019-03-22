package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mindlin.jsast.tree.SpreadElementTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.Tree.Kind;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;

public class UnaryOperatorTest {
	@Test
	public void testPrefixIncrement() {
		UnaryTree expr = parseExpression("++a", Kind.PREFIX_INCREMENT);
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testPrefixDecrement() {
		UnaryTree expr = parseExpression("--a", Kind.PREFIX_DECREMENT);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testPostfixIncrement() {
		UnaryTree expr = parseExpression("a++", Kind.POSTFIX_INCREMENT);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testPostfixDecrement() {
		UnaryTree expr = parseExpression("a--", Kind.POSTFIX_DECREMENT);
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testUnaryPlus() {
		UnaryTree expr = parseExpression("+a", Kind.UNARY_PLUS);
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testUnaryMinus() {
		UnaryTree expr = parseExpression("-a", Kind.UNARY_MINUS);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testLogicalNot() {
		UnaryTree expr = parseExpression("!a", Kind.LOGICAL_NOT);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testBitwiseNot() {
		UnaryTree expr = parseExpression("~a", Kind.BITWISE_NOT);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testYield() {
		UnaryTree expr = parseExpressionWith("yield a", true, true, false, Kind.YIELD);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testYieldToGenerator() {
		UnaryTree expr = parseExpressionWith("yield* a", true, true, false, Kind.YIELD_GENERATOR);
		assertIdentifier("a", expr.getExpression());
	}

	@Test
	public void testTypeof() {
		UnaryTree expr = parseExpression("typeof a", Kind.TYPEOF);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testTypeofLiteral() {
		UnaryTree expr = parseExpression("typeof 'foo'", Kind.TYPEOF);
		assertLiteral("foo", expr.getExpression());
	}

	@Test
	public void testDelete() {
		UnaryTree expr = parseExpression("delete a", Kind.DELETE);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testVoid() {
		UnaryTree expr = parseExpression("void a", Kind.VOID);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testLonelyVoid() {
		UnaryTree expr = parseExpression("void;", Kind.VOID);
		assertNull(expr.getExpression());
	}
	
	@Test
	public void testSpread() {
		SpreadElementTree expr = parseExpression("...a", Kind.SPREAD);
		assertIdentifier("a", expr.getExpression());
	}
	
	@Test
	public void testInvalidUpdateTargetStringLiteral() {
		final String message = "Cannot update string literal";
		assertExceptionalExpression("++'foo'", message);
		assertExceptionalExpression("'foo'++", message);
	}
	
	@Test
	public void testInvalidUpdateTargetNumericLiteral() {
		final String message = "Cannot update numeric literal";
		assertExceptionalExpression("++4", message);
		assertExceptionalExpression("4++", message);
	}
	
	@Test
	public void testInvalidUpdateTargetBooleanLiteral() {
		final String message = "Cannot update boolean literal";
		assertExceptionalExpression("++true", message);
		assertExceptionalExpression("true++", message);
	}
	
	@Test
	public void testInvalidUpdateTargetNullLiteral() {
		final String message = "Cannot update null literal";
		assertExceptionalExpression("++null", message);
		assertExceptionalExpression("null++", message);
	}
	
	@Test
	public void testInvalidStrictDeleteUnqualifiedIdentifier() {
		assertExceptionalExpression("delete foo", "Cannot delete unqualified identifier in strict mode", true);
	}
}
