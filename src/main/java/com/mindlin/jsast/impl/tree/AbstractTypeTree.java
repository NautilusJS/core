package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.type.TypeTree;
import com.mindlin.nautilus.fs.SourcePosition;

public abstract class AbstractTypeTree extends AbstractTree implements TypeTree {
	public AbstractTypeTree(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
	
}
