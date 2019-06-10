package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.ParameterTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class FunctionTest {
	
	@Test
	public void testFunctionExpression() {
		FunctionExpressionTree fn = parseExpression("function(){}", Kind.FUNCTION_EXPRESSION);
		List<? extends ParameterTree> parameters = fn.getParameters();
		assertEquals(0, parameters.size());
	}
	
	@Test
	public void testSingleParameter() {
		FunctionExpressionTree fn = parseExpression("function(a){}", Kind.FUNCTION_EXPRESSION);
		List<? extends ParameterTree> parameters = fn.getParameters();
		assertEquals(1, parameters.size());
		assertIdentifier("a", parameters.get(0).getName());
	}
	
	@Test
	public void testMultipleParameters() {
		FunctionExpressionTree fn = parseExpression("function(a, b, c){}", Kind.FUNCTION_EXPRESSION);
		List<? extends ParameterTree> parameters = fn.getParameters();
		assertEquals(3, parameters.size());
		assertIdentifier("a", parameters.get(0).getName());
		assertIdentifier("b", parameters.get(1).getName());
		assertIdentifier("c", parameters.get(2).getName());
	}
	
	@Test
	public void testRestParameter() {
		FunctionExpressionTree fn = parseExpression("function(...c){}", Kind.FUNCTION_EXPRESSION);
		List<? extends ParameterTree> parameters = fn.getParameters();
		assertEquals(1, parameters.size());
		assertIdentifier("c", parameters.get(0).getName());
	}
	
	@Test
	public void testMultipleWithRestParameter() {
		FunctionExpressionTree fn = parseExpression("function(a, b, ...c){}", Kind.FUNCTION_EXPRESSION);
		List<? extends ParameterTree> parameters = fn.getParameters();
		assertEquals(3, parameters.size());
		assertIdentifier("a", parameters.get(0).getName());
		assertIdentifier("b", parameters.get(1).getName());
		assertIdentifier("c", parameters.get(2).getName());
		assertTrue(parameters.get(2).isRest());
	}
}
