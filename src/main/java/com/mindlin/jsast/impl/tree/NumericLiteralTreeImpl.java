package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.impl.lexer.Token.NumericLiteralToken;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class NumericLiteralTreeImpl extends AbstractTree implements NumericLiteralTree {
	protected final Number value;
	
	public NumericLiteralTreeImpl(NumericLiteralToken t) {
		this(t.getStart(), t.getEnd(), t.getValue());
	}
	
	public NumericLiteralTreeImpl(SourcePosition start, SourcePosition end, Number value) {
		super(start, end);
		this.value = value;
	}
	
	@Override
	public Number getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
}
