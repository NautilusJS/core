package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.TestUtils.assertNumberEquals;
import static org.junit.Assert.*;
//import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.opentest4j.AssertionFailedError;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnsupportedException;
import com.mindlin.jsast.fs.SourceFile.NominalSourceFile;
import com.mindlin.jsast.impl.lexer.JSLexer;
import com.mindlin.jsast.impl.parser.JSParser.Context;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.Modifiers;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;

import com.mindlin.jsast.tree.type.SpecialTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.jsast.tree.type.SpecialTypeTree.SpecialType;

@RunWith(Suite.class)
@SuiteClasses({ArrayLiteralTest.class, AssignmentTest.class, BinaryExpressionTest.class, ClassDeclarationTest.class, DoLoopTest.class, ForLoopTest.class, IdentifierTest.class, ImportStatementTest.class, InterfaceDeclarationTest.class, LambdaTest.class, OperatorTest.class, RegExpLiteralTest.class, StatementTest.class, TemplateLiteralTest.class, TypeTest.class, UnaryOperatorTest.class, VariableDeclarationTest.class })
public class JSParserTest {
	
	protected static final void assertLiteral(String value, ExpressionTree expr) {
		assertEquals(Kind.STRING_LITERAL, expr.getKind());
		assertEquals(value, ((StringLiteralTree)expr).getValue());
	}
	
	/**
	 * Assert if a number literal matches an expected value. Doubles are considered to be
	 * equivalent with a .0001 tolerance.
	 * @param value
	 * @param expr
	 */
	protected static final void assertLiteral(Number value, ExpressionTree expr) {
		NumericLiteralTree exprN = assertKind(Kind.NUMERIC_LITERAL, expr);
		Number actual = exprN.getValue();
		assertNumberEquals(actual, value);
	}
	
	protected static final void assertIdentifier(String name, Tree expr) {
		IdentifierTree id = assertKind(Kind.IDENTIFIER, expr);
		assertEquals(name, id.getName());
	}
	
	protected static final void assertIdentifier(String name, IdentifierTree expr) {
		assertNotNull(expr);
		assertEquals(Kind.IDENTIFIER, expr.getKind());//TODO: this should never fail
		assertEquals(name, expr.getName());
	}
	
	/**
	 * Compare modifiers.
	 * <br/>
	 * <strong>Note:</strong> Expected value after actual value here, because we use varargs.
	 * @param actual Actual modifiers
	 * @param expected Expected modifiers (union)
	 */
	protected static final void assertModifiers(Modifiers actual, Modifiers...expected) {
		assertModifiers(actual, Modifiers.union(expected));
	}
	
	/**
	 * Compare modifiers.
	 * <br/>
	 * <strong>Note:</strong> Expected value after actual value here, because we use varargs.
	 * @param actual Actual modifiers
	 * @param expected Expected modifiers (union)
	 */
	protected static final void assertModifiers(Modifiers actual, Modifiers expected) {
		assertNotNull(actual);
		Objects.requireNonNull(expected);
		
		if (actual.equals(expected))
			return;
		
		throw new AssertionFailedError("Modifier mismatch", actual, expected);
	}
	
	/**
	 * Helper for the (pretty common) special case where we have a list that should contain a single element
	 * of some kind.
	 */
	protected static final <T extends Tree> T assertSingleElementKind(Tree.Kind kind, List<? extends Tree> nodes) {
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		return assertKind(kind, nodes.get(0));
	}
	
	@SuppressWarnings("unchecked")
	protected static final <T extends Tree> T assertKind(Tree.Kind kind, Tree tree) {
		assertEquals(kind, tree.getKind());
		return (T) tree;
	}
	
	protected static final void assertSpecialType(SpecialType value, TypeTree type) {
		assertEquals(Kind.SPECIAL_TYPE, type.getKind());
		assertEquals(value, ((SpecialTypeTree) type).getType());
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg) {
		assertExceptionalExpression(expr, errorMsg, false);
	}
	
	protected static void assertExceptionalExpression(String expr, String errorMsg, boolean strict) {
		try {
			JSLexer lexer = createLexer(expr);
			Context ctx = new Context();
			if (strict)
				ctx.enterStrict();
			new JSParser().parseNextExpression(lexer, ctx);
			if (lexer.isEOF())
				fail(errorMsg);
		} catch (JSSyntaxException e) {
			//Expected
		}
	}
	
	protected static void assertExceptionalStatement(String stmt, String errorMsg) {
		assertThrows(
				JSSyntaxException.class,
				() -> parseStatement(stmt, null, true),
				errorMsg);
	}
	
	public static <T extends StatementTree> T parseStatement(String stmt, Kind kind) {
		return parseStatement(stmt, Objects.requireNonNull(kind), true);
	}
	
	@SuppressWarnings("unchecked")
	static <T extends StatementTree> T parseStatement(String stmt, Kind kind, boolean complete) {
		JSLexer lexer = createLexer(stmt);
		T result = (T) new JSParser().parseStatement(lexer, new Context());
		
		if (complete)
			assertTrue(lexer.isEOF(), () -> "Not all of statement was consumed. Read until " + lexer.getPosition());
		
		if (kind != null)
			assertEquals(kind, result.getKind());
		
		return result;
	}
	
	public static <T extends ExpressionTree> T parseExpression(String expr) {
		return parseExpression(expr, null, true);
	}
	
	public static <T extends ExpressionTree> T parseExpression(String expr, Kind kind) {
		return parseExpression(expr, Objects.requireNonNull(kind), true);
	}
	
	protected static <T extends ExpressionTree> T parseExpression(String expr, Kind kind, boolean complete) {
		return parseExpression(expr, kind, new Context(), complete);
	}
	
	public static <T extends ExpressionTree> T parseExpressionWith(String expr, boolean in, boolean yield, boolean await, Kind kind) {
		Context context = new Context();
		if (yield)
			context.pushGenerator();
		
		if (in)
			context.allowIn();
		else
			context.disallowIn();
		
		context.allowAwait(await);
		
		return parseExpression(expr, kind, context, true);
	}
	
	@SuppressWarnings("unchecked")
	static <T extends ExpressionTree> T parseExpression(String expr, Kind kind, Context context, boolean complete) {
		JSLexer lexer = createLexer(expr);
		
		T result;
		try {
			result = (T) new JSParser().parseNextExpression(lexer, context);
		} catch (JSUnsupportedException e) {
			fail(e.getLocalizedMessage());
			throw e;
		}
		
		if (complete)
			assertTrue(lexer.isEOF(), () -> "Not all of expression was consumed. Read until " + lexer.getPosition());
		
		if (kind != null)
			assertEquals(kind, result.getKind());
		
		return result;
	}
	
	public static <T extends TypeTree> T parseType(String type) {
		return parseType(type, null, true);
	}
	
	public static <T extends TypeTree> T parseType(String expr, Kind expectedKind) {
		return parseType(expr, Objects.requireNonNull(expectedKind), true);
	}
	
	@SuppressWarnings("unchecked")
	static <T extends TypeTree> T parseType(String expr, Kind expectedKind, boolean complete) {
		JSLexer lexer = createLexer(expr);
		
		T result;
		try {
			result = (T) new JSParser().parseType(lexer, new Context());
		} catch (JSUnsupportedException e) {
			fail(e.getLocalizedMessage());
			throw e;
		}
		
		if (complete)
			assertTrue(lexer.isEOF(), () -> "Not all of type expression was consumed. Read until " + lexer.getPosition());
		
		if (expectedKind != null)
			assertEquals(expectedKind, result.getKind());
		
		return result;
	}
	
	public static String getTestName() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (int i = 3; i < stack.length; i++)
			if (stack[i].getMethodName().startsWith("test"))
				return stack[i].getMethodName();
		return "test???";
	}
	
	static JSLexer createLexer(String code) {
		return new JSLexer(new NominalSourceFile(getTestName(), code));
	}
}
