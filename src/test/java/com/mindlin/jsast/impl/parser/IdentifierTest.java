package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

public class IdentifierTest {
	static void testIdentifier(String expected, String identifier) {
		IdentifierTree id = parseExpression(identifier, Kind.IDENTIFIER);
		assertIdentifier(expected, id);
	}
	
	@Test
	public void testSimpleIdentifier() {
		testIdentifier("a", "a");
	}
	
	@Test
	public void testNumbersInIdentifier() {
		testIdentifier("s1234567890", "s1234567890");
	}
	
	@Test
	public void test$InIdentifier() {
		testIdentifier("$", "$");
		testIdentifier("$foo", "$foo");
		testIdentifier("bar$", "bar$");
	}
	
	@Test
	public void testInvalidIdentifiers() {
		ExpressionTree expr = parseExpression("null");
		assertNotEquals(Tree.Kind.IDENTIFIER, expr.getKind());
		
		//TODO expound
	}
}
