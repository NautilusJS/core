package com.mindlin.jsast.tree;

public interface ComputedPropertyKeyTree extends PropertyName {
	
	ExpressionTree getExpression();
	
	@Override
	default Kind getKind() {
		return Kind.COMPUTED_PROPERTY_KEY;
	}
	
	@Override
	default <R, D> R accept(PropertyNameVisitor<R, D> visitor, D data) {
		return visitor.visitComputedPropertyKey(this, data);
	}
}
