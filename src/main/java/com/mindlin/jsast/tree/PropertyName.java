package com.mindlin.jsast.tree;

public interface PropertyName extends DeclarationName {
	<R, D> R accept(PropertyNameVisitor<R, D> visitor, D data);

	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((PropertyNameVisitor<R, D>) visitor, data);
	}
}
