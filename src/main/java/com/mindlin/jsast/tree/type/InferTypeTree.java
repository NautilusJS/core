package com.mindlin.jsast.tree.type;

public interface InferTypeTree extends TypeTree {
	TypeParameterDeclarationTree getParameter();
	
	@Override
	default Kind getKind() {
		return Kind.INFER_TYPE;
	}
	@Override
	
	default <R, D> R accept(TypeTreeVisitor<R, D> visitor, D data) {
		return visitor.visitInferType(this, data);
	}
}
