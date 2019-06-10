package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.DebuggerTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class DebuggerTreeImpl extends AbstractTree implements DebuggerTree {
	public DebuggerTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
}
