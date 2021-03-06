package com.mindlin.jsast.impl.tree;

import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.nautilus.fs.SourcePosition;
import com.mindlin.nautilus.fs.SourceRange;

public class ThisExpressionTreeImpl extends AbstractTree implements ThisExpressionTree {
	public ThisExpressionTreeImpl(Token t) {
		this(t.getStart(), t.getEnd());
	}
	
	public ThisExpressionTreeImpl(SourcePosition start, SourcePosition end) {
		super(start, end);
	}
	
	public ThisExpressionTreeImpl(SourceRange range) {
		super(range);
	}
}
