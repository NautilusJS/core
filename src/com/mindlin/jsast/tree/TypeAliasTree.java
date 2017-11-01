package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.tree.type.GenericTypeTree;
import com.mindlin.jsast.tree.type.TypeTree;

public interface TypeAliasTree extends StatementTree {
	IdentifierTree getAlias();
	
	List<GenericTypeTree> getGenericParameters();
	
	TypeTree getValue();
	

	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.TYPE_ALIAS;
	}

	@Override
	default <R, D> R accept(StatementTreeVisitor<R, D> visitor, D data) {
		return visitor.visitTypeAlias(this, data);
	}
}
