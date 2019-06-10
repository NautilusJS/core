package com.mindlin.jsast.transform;

import java.util.List;

import com.mindlin.jsast.impl.util.Scope;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.nautilus.util.Pair;

public interface VariableContext {
	public static interface VariableInfo {
		TypeTree getDeclaredType();
		TypeTree getAssignedType();
		List<Pair<Scope, Tree>> getAssignments();
	}
}
