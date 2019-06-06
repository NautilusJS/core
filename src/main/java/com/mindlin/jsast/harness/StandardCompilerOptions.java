package com.mindlin.jsast.harness;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;

import com.google.auto.service.AutoService;
import com.mindlin.jsast.i18n.DiagnosticConsumer;

@AutoService(CompilerOptionProvider.class)
public class StandardCompilerOptions implements CompilerOptionProvider {
	
	// Options that change how the compiler runs
	public static final @NonNull CompilerOption<Boolean> PRINT_HELP = new AbstractCompilerOption.Flag("help", null, null, "h");
	public static final @NonNull CompilerOption<Boolean> PRINT_VERSION = new AbstractCompilerOption.Flag("version", null, null, "v");
	public static final @NonNull CompilerOption<Boolean> PRINT_PLUGINS = new AbstractCompilerOption.Flag("plugins", null, null);
	public static final @NonNull CompilerOption<Boolean> INIT = new AbstractCompilerOption.Flag("init", null, null);
	public static final @NonNull CompilerOption<Boolean> BUILD = new AbstractCompilerOption.Flag("build", null, null, "b");
	
	// Very far-reaching fields (that effect a bunch of the output)
	public static final @NonNull CompilerOption<Locale> LOCALE = new AbstractCompilerOption.SimpleField<Locale>("locale", null, null) {
		@Override
		protected Locale parse(DiagnosticConsumer errorHandler, String command, String value) {
			return Locale.forLanguageTag(value);
		}
	};
	
	public static final @NonNull CompilerOption<Charset> SOURCE_ENCODING = new AbstractCompilerOption.CharsetField("source-encoding", null, null);
	public static final @NonNull CompilerOption<Charset> OUTPUT_ENCODING = new AbstractCompilerOption.CharsetField("output-encoding", null, null);
	
	// Major switches
	public static final @NonNull CompilerOption<Boolean> WATCH = new AbstractCompilerOption.Flag("watch", null, null, "w");
	public static final @NonNull CompilerOption<Boolean> INCREMENTAL = new AbstractCompilerOption.Flag("incremental", null, null);
	public static final @NonNull CompilerOption<Boolean> COMPOSITE = new AbstractCompilerOption.Flag("composite", null, null);
	public static final @NonNull CompilerOption<Path> PROJECT = new AbstractCompilerOption.PathField("project", null, null, "p");
	public static final @NonNull CompilerOption<Path> ROOT_DIR = new AbstractCompilerOption.PathField("rootDir", null, null);
	
	// Feature flags
	public static final @NonNull CompilerOption<String> SOURCE_LANGUAGE = new AbstractCompilerOption.StringField("source", null, null);
	public static final @NonNull CompilerOption<String> TARGET_LANGUAGE = new AbstractCompilerOption.StringField("target", null, null);
	public static final @NonNull CompilerOption<Boolean> REMOVE_COMMENTS = new AbstractCompilerOption.Flag("removeComments", null, null);
	
	// Debug flags
	public static final @NonNull CompilerOption<Boolean> PRINT_FILES = new AbstractCompilerOption.Flag("print-files", null, null, "listFiles");
	public static final @NonNull CompilerOption<Boolean> PRINT_OUTPUT_FILES = new AbstractCompilerOption.Flag("print-output-files", null, null, "listEmittedFiles");
	public static final @NonNull CompilerOption<Boolean> PRINT_CONFIG = new AbstractCompilerOption.Flag("print-config", null, null, "showConfig");
	public static final @NonNull CompilerOption<Boolean> PRINT_MODULE_RESOLUTION = new AbstractCompilerOption.Flag("print-module-resolution", null, null, "showConfig");
	public static final @NonNull CompilerOption<Boolean> PRINT_DIAGNOSTICS = new AbstractCompilerOption.Flag("print-diagnostics", null, null, "diagnostics");
	public static final @NonNull CompilerOption<Boolean> PRINT_TIMING = new AbstractCompilerOption.Flag("print-timing", null, null, "extendedDiagnostics");
	
	@Override
	@SuppressWarnings("null")
	public Iterable<CompilerOption<?>> getOptions() {
		return Arrays.asList(
				PRINT_HELP,
				PRINT_VERSION,
				PRINT_PLUGINS,
				INIT,
				BUILD,
				LOCALE,
				SOURCE_ENCODING,
				WATCH,
				INCREMENTAL,
				COMPOSITE,
				PROJECT,
				ROOT_DIR,
				SOURCE_LANGUAGE,
				TARGET_LANGUAGE,
				REMOVE_COMMENTS,
				PRINT_FILES,
				PRINT_OUTPUT_FILES,
				PRINT_CONFIG,
				PRINT_MODULE_RESOLUTION);
	}
}
