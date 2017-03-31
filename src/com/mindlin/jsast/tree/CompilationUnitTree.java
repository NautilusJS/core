package com.mindlin.jsast.tree;

import java.util.List;

import com.mindlin.jsast.fs.SourceFile;
import com.mindlin.jsast.impl.tree.LineMap;

public interface CompilationUnitTree extends Tree {
	LineMap getLineMap();
	
	List<StatementTree> getSourceElements();
	
	SourceFile getSourceFile();
	
	boolean isStrict();
	
	@Override
	default Tree.Kind getKind() {
		return Tree.Kind.COMPILATION_UNIT;
	}
	
	@Override
	default <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
		return visitor.visitCompilationUnit(this, data);
	}
}