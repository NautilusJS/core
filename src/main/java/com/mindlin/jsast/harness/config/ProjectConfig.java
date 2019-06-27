package com.mindlin.jsast.harness.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.mindlin.jsast.fs.ProjectReference;
import com.mindlin.jsast.harness.CompilerOptions;
import com.mindlin.jsast.i18n.Diagnostic;

/**
 * Configuration for a project
 * @author mailmindlin
 */
public class ProjectConfig {
	private final CompilerOptions options;
	private List<@NonNull ProjectReference> projectReferences = new ArrayList<>();
	private final List<@NonNull String> fileNames = new ArrayList<>();
	private final List<@NonNull Diagnostic> errors = new ArrayList<>();
	
	public ProjectConfig() {
		this(new CompilerOptions());
	}
	
	public ProjectConfig(CompilerOptions options) {
		this.options = options;
	}
	
	public void addFileName(String name) {
		this.fileNames.add(name);
	}
	
	public void reportParseDiagnostic(Diagnostic.Level level, Diagnostic diagnostic) {
		this.errors.add(diagnostic);
	}
	
	/**
	 * @return Options for project
	 */
	public CompilerOptions getOptions() {
		return options;
	}
	
	/**
	 * @return Diagnostics from parsing options
	 */
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
