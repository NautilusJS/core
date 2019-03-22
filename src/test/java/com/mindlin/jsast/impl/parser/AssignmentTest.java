package com.mindlin.jsast.impl.parser;

import static org.junit.Assert.*;
import static com.mindlin.jsast.impl.parser.JSParserTest.*;
import org.junit.Test;

import com.mindlin.jsast.exception.JSException;
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
	
	@Test(expected=JSException.class)
	public void testInvalidAssignmentToStringLiteral() {
		parseExpression("'hello' = x'");
	}
	
	@Test(expected=JSException.class)
	public void testInvalidAssignmentToBooleanLiteral() {
		parseExpression("true = x'");
	}
	
	@Test(expected=JSException.class)
	public void testInvalidAssignmentToNumericLiteral() {
		parseExpression("5 = x'");
	}
	
	@Test(expected=JSException.class)
	public void testInvalidAssignmentToOpaqueExpression() {
		parseExpression("~x = y'");
	}
}
