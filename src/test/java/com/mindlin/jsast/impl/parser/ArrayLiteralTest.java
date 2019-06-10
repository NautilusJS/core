package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertKind;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertLiteral;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class ArrayLiteralTest {
	
	@Test
	public void testEmpty() {
		ArrayLiteralTree arr = parseExpression("[]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(0, elements.size());
	}

	@Test
	public void testSingleValue() {
		ArrayLiteralTree arr = parseExpression("[1]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(1, elements.size());
		assertLiteral(1, elements.get(0));
	}

	@Test
	public void testSingleUndefined() {
		ArrayLiteralTree arr = parseExpression("[,]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(1, elements.size());
		assertNull(elements.get(0));
	}

	@Test
	public void testCommaAfterValue() {
		ArrayLiteralTree arr = parseExpression("[1,]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(1, elements.size());
		assertLiteral(1, elements.get(0));
	}

	@Test
	public void testCommaBeforeValue() {
		ArrayLiteralTree arr = parseExpression("[,1]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(2, elements.size());
		assertNull(elements.get(0));
		assertLiteral(1, elements.get(1));
	}

	@Test
	public void testMultipleValues() {
		ArrayLiteralTree arr = parseExpression("[1,2]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(2, elements.size());
		assertLiteral(1, elements.get(0));
		assertLiteral(2, elements.get(1));
	}

	@Test
	public void testNestedArrayLiteral() {
		ArrayLiteralTree arr = parseExpression("[[]]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(1, elements.size());
		ArrayLiteralTree nested = (ArrayLiteralTree) elements.get(0);
		assertEquals(0, nested.getElements().size());
	}
	
	@Test
	public void testNestedObjectLiteral() {
		ArrayLiteralTree arr = parseExpression("[{}]", Kind.ARRAY_LITERAL);
		List<? extends ExpressionTree> elements = arr.getElements();
		assertEquals(1, elements.size());
		
		ObjectLiteralTree nested = assertKind(Kind.OBJECT_LITERAL, elements.get(0));
		assertEquals(0, nested.getProperties().size());
	}
}
