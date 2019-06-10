package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.jsast.tree.WhileLoopTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class WhileLoopTreeImpl extends AbstractConditionalLoopTree implements WhileLoopTree {
	public WhileLoopTreeImpl(SourcePosition start, SourcePosition end, ExpressionTree condition, StatementTree statement) {
		super(start, end, condition, statement);
	}
}
