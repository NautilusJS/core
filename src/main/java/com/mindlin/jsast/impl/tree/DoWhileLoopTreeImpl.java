package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.DoWhileLoopTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class DoWhileLoopTreeImpl extends AbstractConditionalLoopTree implements DoWhileLoopTree {
	public DoWhileLoopTreeImpl(SourcePosition start, SourcePosition end, StatementTree statement, ExpressionTree condition) {
		super(start, end, condition, statement);
	}
}
