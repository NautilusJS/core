package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertExceptionalStatement;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertKind;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertLiteral;
import static com.mindlin.jsast.impl.parser.JSParserTest.createLexer;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseStatement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.BreakTree;
import com.mindlin.jsast.tree.ContinueTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.SwitchCaseTree;
import com.mindlin.jsast.tree.SwitchTree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.WithTree;

public class StatementTest {
	
	@Test
	public void testConsumeSemicolons() {
		JSParser parser = new JSParser();
		JSLexer lexer = createLexer("a;");
		assertNotNull(parser.parseStatement(lexer, new Context()));
		assertNull(parser.parseStatement(lexer, new Context()));
	}
	
	@Test
	public void testEmptySwitch() {
		SwitchTree st = parseStatement("switch(foo){}", Kind.SWITCH);
		assertIdentifier("foo", st.getExpression());
		assertEquals(0, st.getCases().size());
	}
	
	@Test
	public void testSwitchDefault() {
		final String code = "switch (foo) {"
				+ "default:"
				+ "}";
		
		SwitchTree st = parseStatement(code, Kind.SWITCH);
		assertIdentifier("foo", st.getExpression());
		
		List<? extends SwitchCaseTree> cases = st.getCases();
		assertEquals(1, cases.size());
		
		SwitchCaseTree case0 = cases.get(0);
		assertTrue(case0.isDefault());
	}
	
	@Test
	public void testSwitchSingleCase() {
		final String code = "switch (foo) {"
				+ "case 'a':"
				+ "bar();"
				+ "}";
		
		SwitchTree st = parseStatement(code, Kind.SWITCH);
		assertIdentifier("foo", st.getExpression());
		
		assertEquals(1, st.getCases().size());
		//TODO: check cases
	}
	
	@Test
	public void testSwitchInvalid() {
		assertExceptionalStatement("switch(foo){notCaseNorDefault:}", "Parsed invalid switch statement");
		assertExceptionalStatement("switch(foo){var:}", "Parsed invalid switch statement");
	}
	
	@Test
	public void testDebuggerStatement() {
		parseStatement("debugger;", Kind.DEBUGGER);
		//There is literally nothing to test
	}
	
	@Test
	public void testUnlabeledBreak() {
		BreakTree breakTree = parseStatement("break;", Kind.BREAK);
		assertNull(breakTree.getLabel());
	}
	
	@Test
	public void testUnlabeledContinue() {
		ContinueTree continueTree = parseStatement("continue;", Kind.CONTINUE);
		assertNull(continueTree.getLabel());
	}
	
	@Test
	public void testLabeledBreak() {
		BreakTree breakTree = parseStatement("break everything;", Kind.BREAK);
		assertIdentifier("everything", breakTree.getLabel());
	}
	
	@Test
	public void testLabeledContinue() {
		ContinueTree continueTree = parseStatement("continue later;", Kind.CONTINUE);
		assertIdentifier("later", continueTree.getLabel());
	}
	
	@Test
	public void testWith() {
		WithTree with = parseStatement("with(0);", Kind.WITH);
		
		assertLiteral(0, with.getScope());
	}
	
	@Test
	public void testLabelledStatements() {
		LabeledStatementTree labelled = parseStatement("x:;", Kind.LABELED_STATEMENT);
		
		assertIdentifier("x", labelled.getName());
		assertKind(Kind.EMPTY_STATEMENT, labelled.getStatement());
		
		
		labelled = parseStatement("x:y:;", Kind.LABELED_STATEMENT);
		
		assertIdentifier("x", labelled.getName());
		
		labelled = assertKind(Kind.LABELED_STATEMENT, labelled.getStatement());
		assertIdentifier("y", labelled.getName());
		
		assertKind(Kind.EMPTY_STATEMENT, labelled.getStatement());
	}
}
