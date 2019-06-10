package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.LabeledStatementTree;
import com.mindlin.jsast.tree.StatementTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class LabeledStatementTreeImpl extends AbstractControlStatementTree implements LabeledStatementTree {
	protected final IdentifierTree name;
	
	public LabeledStatementTreeImpl(SourcePosition start, SourcePosition end, IdentifierTree name, StatementTree statement) {
		super(start, end, statement);
		this.name = name;
	}
	
	@Override
	public IdentifierTree getName() {
		return this.name;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getStatement());
	}
	
}
