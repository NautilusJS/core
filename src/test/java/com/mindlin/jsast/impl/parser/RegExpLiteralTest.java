package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;


public class RegExpLiteralTest {
	
	@Test
	public void testEmptyRegExp() {
		RegExpLiteralTree regex = parseExpression("/x/", Kind.REGEXP_LITERAL);
		
		assertEquals("x", regex.getBody());
		assertEquals("", regex.getFlags());
	}
}
