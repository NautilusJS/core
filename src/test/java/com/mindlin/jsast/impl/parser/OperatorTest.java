package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class OperatorTest {
	
	@Test
	public void testConditional() {
		ConditionalExpressionTree expr = parseExpression("a?b:c", Kind.CONDITIONAL);
		assertIdentifier("a", expr.getCondition());
		assertIdentifier("b", expr.getTrueExpression());
		assertIdentifier("c", expr.getFalseExpression());
	}
	
	@Test
	public void testNewNoArgs() {
		NewTree newExpr = parseExpression("new X()", Kind.NEW);
		assertIdentifier("X", newExpr.getCallee());
		
		List<? extends ExpressionTree> arguments = newExpr.getArguments();
		assertNotNull("new-with-parens should return empty arguments list", arguments);
		assertEquals(0, arguments.size());
	}
	
	@Test
	public void testNewWithoutParens() {
		NewTree newExpr = parseExpression("new X", Kind.NEW);
		assertIdentifier("X", newExpr.getCallee());
		assertNull("new-without-parens should return null arguments list", newExpr.getArguments());
	}
	
	@Test
	public void testChainedNews() {
		NewTree newExpr = parseExpression("new new X()", Kind.NEW);
		NewTree nested = assertKind(Kind.NEW, newExpr.getCallee());
		assertIdentifier("X", nested.getCallee());
	}
	
	@Test
	public void testNewParams() {
		NewTree newExpr = parseExpression("new X(a,b)", Kind.NEW);
		assertIdentifier("X", newExpr.getCallee());
		
		List<? extends ExpressionTree> arguments = newExpr.getArguments();
		assertEquals(2, arguments.size());
		assertIdentifier("a", arguments.get(0));
		assertIdentifier("b", arguments.get(1));
	}
	
	@Test
	public void testFunctionCall() {
		FunctionCallTree expr = parseExpression("a(b)", Kind.FUNCTION_INVOCATION);
		assertIdentifier("a", expr.getCallee());
		
		List<? extends ExpressionTree> arguments = expr.getArguments();
		assertEquals(1, arguments.size());
		assertIdentifier("b", arguments.get(0));
	}
	
	@Test
	public void testFunctionCallWithSelect() {
		FunctionCallTree expr = parseExpression("foo.bar(baz)", Kind.FUNCTION_INVOCATION);
		BinaryExpressionTree callee = assertKind(Kind.MEMBER_SELECT, expr.getCallee());
		assertIdentifier("foo", callee.getLeftOperand());
		assertIdentifier("bar", callee.getRightOperand());
		
		List<? extends ExpressionTree> arguments = expr.getArguments();
		assertEquals(1, arguments.size());
		assertIdentifier("baz", arguments.get(0));
	}
}
