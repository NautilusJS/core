package com.mindlin.jsast.harness.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.mindlin.jsast.harness.CompilerOptions;

@NonNullByDefault
public class ParsedCommandLine extends ProjectConfig {
	public ParsedCommandLine() {
		this(new CompilerOptions());
	}
	
	public ParsedCommandLine(CompilerOptions options) {
		super(options);
	}
}
