package com.mindlin.jsast.tree;

/**
 * Marker interface for valid elements of an {@link ObjectLiteralTree object literal}.
 * @author mailmindlin
 */
public interface ObjectLiteralElement extends DeclarationTree {
	<R, D> R accept(ObjectLiteralElementVisitor<R, D> visitor, D data);
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return this.accept((ObjectLiteralElementVisitor<R, D>) visitor, data);
	}
}
