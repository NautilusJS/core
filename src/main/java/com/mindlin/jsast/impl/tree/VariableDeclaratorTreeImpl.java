package com.mindlin.jsast.impl.tree;

import java.util.Objects;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.impl.lexer.Token;
import com.mindlin.jsast.tree.DeclarationName;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.VariableDeclaratorTree;
import com.mindlin.jsast.tree.type.TypeTree;

public class VariableDeclaratorTreeImpl extends AbstractTree implements VariableDeclaratorTree {
	protected final DeclarationName identifier;
	protected final ExpressionTree initializer;
	protected final TypeTree type;
	
	public VariableDeclaratorTreeImpl(SourcePosition start, SourcePosition end, DeclarationName identifier, TypeTree type, ExpressionTree initializer) {
		super(start, end);
		this.identifier = identifier;
		this.type = type;
		this.initializer = initializer;
	}
	
	@Override
	public ExpressionTree getInitializer() {
		return this.initializer;
	}
	
	@Override
	public TypeTree getType() {
		return this.type;
	}
	
	@Override
	public DeclarationName getName() {
		return this.identifier;
	}
	
	@Override
	protected int hash() {
		return Objects.hash(getKind(), getName(), getType(), getInitializer());
	}
	
}
