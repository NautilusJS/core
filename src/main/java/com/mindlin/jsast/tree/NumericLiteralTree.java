package com.mindlin.jsast.tree;

public interface NumericLiteralTree extends LiteralTree<Number>, PropertyName {
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.NUMERIC_LITERAL;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitNumericLiteral(this, data);
	}
	
	@Override
	default <R, D> R accept(ExpressionTreeVisitor<R, D> visitor, D data) {
		return visitor.visitNumericLiteral(this, data);
	}
	
	@Override
	default <R, D> R accept(PropertyNameVisitor<R, D> visitor, D data) {
		return visitor.visitNumericLiteral(this, data);
	}
}
