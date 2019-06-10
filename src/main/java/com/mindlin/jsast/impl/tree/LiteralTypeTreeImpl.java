package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.tree.LiteralTree;
import com.mindlin.jsast.tree.type.LiteralTypeTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class LiteralTypeTreeImpl<T> extends AbstractTypeTree implements LiteralTypeTree<T> {
	protected final LiteralTree<T> value;
	
	public LiteralTypeTreeImpl(SourcePosition start, SourcePosition end, LiteralTree<T> value) {
		super(start, end);
		this.value = value;
	}
	
	public LiteralTypeTreeImpl(LiteralTree<T> value) {
		this(value.getStart(), value.getEnd(), value);
	}
	
	@Override
	public LiteralTree<T> getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
	
}
