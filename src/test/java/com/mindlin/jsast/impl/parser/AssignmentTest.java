package com.mindlin.jsast.impl.parser;

import static com.mindlin.jsast.impl.parser.JSParserTest.assertExceptionalExpression;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertIdentifier;
import static com.mindlin.jsast.impl.parser.JSParserTest.assertKind;
import static com.mindlin.jsast.impl.parser.JSParserTest.parseExpression;

import org.junit.Test;

import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class AssignmentTest {
	@Test
	public void testAssignment() {
		AssignmentTree assignment = parseExpression("x=y", Kind.ASSIGNMENT);
		assertIdentifier("x", assignment.getVariable());
		assertIdentifier("y", assignment.getValue());
	}
	
	@Test
	public void testChainedAssignment() {
		AssignmentTree assignment = parseExpression("x=y=z", Kind.ASSIGNMENT);
		assertIdentifier("x", assignment.getVariable());
		
		AssignmentTree yzAssignment = assertKind(Kind.ASSIGNMENT, assignment.getValue());
		assertIdentifier("y", yzAssignment.getVariable());
		assertIdentifier("z", yzAssignment.getValue());
	}
	
	@Test
	public void testInvalidAssignmentToStringLiteral() {
		assertExceptionalExpression("'hello' = x", "Illegal assignment to string literal");
	}

	@Test
	public void testInvalidAssignmentToBooleanLiteral() {
		assertExceptionalExpression("true = x", "Illegal assignment to boolean literal");
	}

	@Test
	public void testInvalidAssignmentToNumericLiteral() {
		assertExceptionalExpression("5 = x", "Illegal assignment to number literal");
	}

	@Test
	public void testInvalidAssignmentToOpaqueExpression() {
		assertExceptionalExpression("~x = y", "Illegal assignment to opaque expression");
	}
}
