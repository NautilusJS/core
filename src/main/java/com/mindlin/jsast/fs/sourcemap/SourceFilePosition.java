package com.mindlin.jsast.fs.sourcemap;

import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.nautilus.fs.SourcePosition;

/**
 * Mapping for an identifier to a source file
 * @author mailmindlin
 */
public interface SourceFilePosition {
	/**
	 * @return The original position (may be invalid)
	 */
	@Nullable SourcePosition getPosition();
	
	/**
	 * @return The original identifier name
	 */
	@Nullable String identifierName();
}
