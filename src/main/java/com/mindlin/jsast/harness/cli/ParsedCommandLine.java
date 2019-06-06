package com.mindlin.jsast.harness.cli;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import com.mindlin.jsast.fs.ProjectReference;
import com.mindlin.jsast.harness.CompilerOptions;
import com.mindlin.jsast.i18n.Diagnostic;

@NonNullByDefault
public class ParsedCommandLine {
	private final CompilerOptions options;
	private List<@NonNull ProjectReference> projectReferences = new ArrayList<>();
	private final List<@NonNull String> fileNames = new ArrayList<>();
	private final List<@NonNull Diagnostic> errors = new ArrayList<>();
	
	public ParsedCommandLine() {
		this(new CompilerOptions());
	}
	
	public ParsedCommandLine(CompilerOptions options) {
		this.options = options;
	}
	
	public void addFileName(String name) {
		this.fileNames.add(name);
	}
	
	public void reportParseDiagnostic(Diagnostic diagnostic) {
		this.errors.add(diagnostic);
	}
	
	public CompilerOptions getOptions() {
		return options;
	}
	
	public List<? extends Diagnostic> getOptionDiagnostics() {
		return this.errors;
	}

	public List<? extends String> getFileNames() {
		return this.fileNames;
	}

	public List<? extends ProjectReference> getProjectReferences() {
		return projectReferences;
	}
}
