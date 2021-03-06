package com.mindlin.jsast.impl.validator;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.nautilus.fs.SourceRange;

public interface ErrorReporter {
	void report(ErrorLevel level, Tree target, String format, Object... args);
	void report(ErrorLevel level, SourceRange location, String format, Object... args);
	
	public static enum ErrorLevel {
		ERROR,
		WARNING,
		HINT,
	}
}
