package com.mindlin.jsast.fs;

import com.mindlin.nautilus.fs.SourceFile;

/**
 * Mapping for an identifier to a source file
 * @author mailmindlin
 */
public interface SourceMapping {
	/**
	 * Get the name of the identifier
	 * @return
	 */
	String identifierName();
	SourceFile sourceFile();
	int lineNumber();
	int column();
}
