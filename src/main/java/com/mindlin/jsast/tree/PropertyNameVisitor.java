package com.mindlin.jsast.tree;

public interface PropertyNameVisitor<R, D> {
	R visitComputedPropertyKey(ComputedPropertyKeyTree node, D d);
	
	R visitIdentifier(IdentifierTree node, D d);
	
	R visitNumericLiteral(NumericLiteralTree node, D d);
	
	R visitStringLiteral(StringLiteralTree node, D d);
}
