package com.mindlin.jsast.tree;

public interface PropertyNameVisitor<R, D> {
	R visitComputedPropertyKey(ComputedPropertyKeyTree node, D context);
	
	R visitIdentifier(IdentifierTree node, D context);
	
	R visitNumericLiteral(NumericLiteralTree node, D context);
	
	R visitStringLiteral(StringLiteralTree node, D context);
}
