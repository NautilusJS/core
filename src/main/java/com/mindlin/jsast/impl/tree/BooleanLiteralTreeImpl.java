package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.impl.lexer.JSSyntaxKind;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.nautilus.fs.SourcePosition;

public class BooleanLiteralTreeImpl extends AbstractTree implements BooleanLiteralTree {
	protected final boolean value;
	public BooleanLiteralTreeImpl(Token t) {
		this(t.getStart(), t.getEnd(), t.getKind() == JSSyntaxKind.TRUE);
	}
	public BooleanLiteralTreeImpl(SourcePosition start, SourcePosition end, boolean value) {
		super(start, end);
		this.value = value;
	}

	@Override
	public Boolean getValue() {
		return value;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getValue());
	}
}

