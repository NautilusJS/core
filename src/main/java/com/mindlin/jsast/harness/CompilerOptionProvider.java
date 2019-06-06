package com.mindlin.jsast.harness;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Service provider for discovering {@link CompilerOption CompilerOptions}.
 * 
 * @author mailmindlin
 */
public interface CompilerOptionProvider {
	@NonNull
	Iterable<CompilerOption<?>> getOptions();
}
