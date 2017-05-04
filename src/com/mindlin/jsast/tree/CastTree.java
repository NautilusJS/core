package com.mindlin.jsast.tree;

public interface CastTree extends ExpressiveExpressionTree {
	/**
	 * Get the type that the expression was cast to
	 * @return
	 */
	TypeTree getType();

	@Override
	default Kind getKind() {
		return Kind.CAST;
	}

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitCast(this, data);
	}
	
}
