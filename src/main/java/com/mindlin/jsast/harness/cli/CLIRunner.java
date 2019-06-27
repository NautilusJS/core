package com.mindlin.jsast.harness.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.ToIntFunction;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.exception.NotFinishedException;
import com.mindlin.jsast.fs.ProjectReference;
import com.mindlin.jsast.harness.AbstractDiagnosticReporter;
import com.mindlin.jsast.harness.AbstractDiagnosticReporter.SimpleDiagnosticReporter;
import com.mindlin.jsast.harness.CompilerOption;
import com.mindlin.jsast.harness.CompilerOptionProvider;
import com.mindlin.jsast.harness.CompilerOptions;
import com.mindlin.jsast.harness.NautilusCompiler;
import com.mindlin.jsast.harness.NautilusCompiler.CompilationHandle;
import com.mindlin.jsast.harness.config.CommandLineParser;
import com.mindlin.jsast.harness.config.ConfigFileWriter;
import com.mindlin.jsast.harness.config.ParsedCommandLine;
import com.mindlin.jsast.harness.StandardCompilerOptions;
import com.mindlin.jsast.harness.discovery.Program;
import com.mindlin.jsast.harness.discovery.ProgramBuilder;
import com.mindlin.jsast.harness.plugin.PluginManager;
import com.mindlin.jsast.i18n.Diagnostic;

/**
 * Main entry point for CLI.
 * 
 * @see StandardCompilerOptions For CLI options
 * @author mailmindlin
 */
@NonNullByDefault
public class CLIRunner implements ToIntFunction<String[]> {
	public static final String CONFIG_FILE_NAME = "tsconfig.json";
	public static final String VERSION = "0.0.1-alpha";
	
	@Nullable
	protected Locale locale = null;
	@Nullable
	protected Path cwd = null;
	@SuppressWarnings("null")
	protected PrintStream out = System.out;
	@SuppressWarnings("null")
	protected PrintStream err = System.err;
	@SuppressWarnings("null")
	protected PrintStream debug = System.out;
	
	@Nullable
	protected AbstractDiagnosticReporter reporter;
	protected final PluginManager plugins = new PluginManager();
	
	public CLIRunner() {
	}
	
	// ===== Getters & setters =====
	
	/**
	 * @return Version string
	 */
	public String getVersion() {
		Package pkg = getClass().getPackage();
		String result = pkg.getImplementationVersion();
		if (result == null)
			result = VERSION;
		return result;
	}
	
	protected Path getBaseDirectory() {
		if (this.cwd == null) {
			// Get entry point base if not provided
			this.cwd = Paths.get("").toAbsolutePath();
		}
		assert this.cwd != null;
		return this.cwd;
	}
	
	public void setBaseDirectory(Path cwd) {
		this.cwd = cwd;
	}
	
	public PrintStream getOutStream() {
		return this.out;
	}
	
	public void setOutStream(PrintStream out) {
		this.out = Objects.requireNonNull(out);
	}
	
	public PrintStream getErrorStream() {
		return this.err;
	}
	
	public void setErrorStream(PrintStream err) {
		this.err = Objects.requireNonNull(err);
	}
	
	public PrintStream getDebugStream() {
		return this.debug;
	}
	
	public void setDebugStream(PrintStream debug) {
		this.debug = Objects.requireNonNull(debug);
	}
	
	/**
	 * @return Locale to use when formatting messages
	 */
	protected Locale getLocale() {
		Locale locale = this.locale;
		if (locale == null) {
			this.locale = locale = Locale.getDefault();
			assert locale != null;
		}
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	// ===== Helper methods =====
	
	protected AbstractDiagnosticReporter getReporter() {
		// TODO cache
		return new SimpleDiagnosticReporter(this.err, this.getBaseDirectory(), "\n", path -> path.toString(), this.getLocale());
	}
	
	protected CompilerOptions getOptions() {
		CompilerOptions result = new CompilerOptions();
		// Discover options
		// TODO: plugins?
		for (CompilerOptionProvider provider : ServiceLoader.load(CompilerOptionProvider.class))
			provider.getOptions().forEach(result::register);
		
		return result;
	}
	
	protected Path resolvePath(Path source) {
		Path result = this.getBaseDirectory().resolve(source);
		assert result != null;
		return result;
	}
	
	protected @NonNull ParsedCommandLine parseCLI(CompilerOptions options, List<String> args) {
		Map<String, CompilerOption<?>> optionLUT = new HashMap<>();
		for (CompilerOption<?> option : options.getOptions()) {
			if (option.getAttributes().contains(CompilerOption.Attribute.NO_CLI))
				continue;
			optionLUT.put(option.name(), option);
			for (String alias : option.aliases())
				optionLUT.put(alias, option);
		}
		
		return new CommandLineParser() {
			@Override
			protected @Nullable CompilerOption<?> getOptionByName(String name) {
				return optionLUT.get(name);
			}
		}.apply(this.getBaseDirectory(), args);
	}
	
	protected @Nullable Path findConfigFile() {
		for (Path current = this.getBaseDirectory().normalize(); current.getNameCount() > 0; current = current.getParent()) {
			Path candidate = current.resolve(CONFIG_FILE_NAME);
			if (Files.exists(candidate))
				return candidate;
		}
		return null;
	}
	
	protected ParsedCommandLine parseConfigFile(Path configFile, CompilerOptions optionsBase) {
		// TODO: finish
		throw new NotFinishedException();
	}
	
	protected boolean isIncrementalCompilation(CompilerOptions options) {
		return options.get(StandardCompilerOptions.INCREMENTAL, false) || options.get(StandardCompilerOptions.COMPOSITE, false);
	}
	
	// ===== Execution options =====
	
	/**
	 * Print version to stdout
	 * 
	 * @return Return code
	 */
	public CLIResult printVersion() {
		out.printf("nautilus-core version %s\n", this.getVersion());
		return CLIResult.SUCCESS_NO_OUTPUT;
	}
	
	/**
	 * Print help text to stdout
	 * 
	 * @param all
	 *            TODO
	 * @return Return code
	 */
	public CLIResult printHelp(CompilerOptions options, boolean all) {
		// TODO: finish
		out.println("<<help message>>");
		for (CompilerOption<?> option : options.getOptions()) {
			//TODO
		}
		return CLIResult.SUCCESS;
	}
	
	protected CLIResult build(List<String> rawArgs) {
		// TODO: finish
		return CLIResult.SUCCESS_NO_OUTPUT;
	}
	
	public CLIResult writeConfigFile(CompilerOptions options) {
		Path outFile = this.resolvePath(Paths.get("tsconfig.json"));
		if (Files.exists(outFile)) {
			// TODO: log
			return CLIResult.FAILURE_NO_OUTPUT;
		}
		try (Writer out = Files.newBufferedWriter(outFile, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			return this.writeConfigFile(options, out);
		} catch (IOException e) {
			e.printStackTrace(this.err);
			// TODO: emit diagnostics?
			return CLIResult.FAILURE_NO_OUTPUT;
		}
	}
	
	public CLIResult writeConfigFile(CompilerOptions options, @NonNull Appendable out) {
		try (ConfigFileWriter writer = new ConfigFileWriter(out)) {
			writer.write(options);
			return CLIResult.SUCCESS;
		} catch (IOException e) {
			e.printStackTrace(this.err);
			// TODO: emit diagnostics?
			return CLIResult.FAILURE;
		}
	}
	
	protected CLIResult compileIncrmental(ParsedCommandLine args) {
		//TODO
		throw new NotFinishedException();
	}
	
	protected CLIResult compile(ParsedCommandLine args) {
		return this.compile(args.getFileNames(), args.getProjectReferences(), args.getOptions());
	}
	
	protected NautilusCompiler buildCompiler(CompilerOptions options) {
		// TODO finish
		return null;
	}
	
	protected Program createSimpleProgram(List<? extends String> rootFiles, List<? extends ProjectReference> projectRefs, CompilerOptions options) {
		ProgramBuilder builder = new ProgramBuilder();
		builder.setOptions(options);
		//TODO finish
		return builder.build();
	}
	
	public CLIResult compile(List<? extends String> rootFiles, List<? extends ProjectReference> projectRefs, CompilerOptions options) {
		// TODO: build program
		NautilusCompiler compiler;
		try {
			compiler = this.buildCompiler(options);
		} catch (Exception e) {
			return CLIResult.FAILURE_NO_OUTPUT;
		}
		
		Program program;
		try {
			program = this.createSimpleProgram(rootFiles, projectRefs, options);
		} catch (Exception e) {
			return CLIResult.FAILURE_NO_OUTPUT;
		}
		
		try {
			CompilationHandle resultHandle = compiler.run(program);
			resultHandle.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// TODO: special return code for when we want to keep running (e.g.,
		// watch)?
		throw new NotFinishedException();
	}
	
	protected CLIResult callInternal(String... rawArgs) throws RuntimeException {
		// Parse arguments
		CompilerOptions options = this.getOptions();
		@SuppressWarnings("null")
		ParsedCommandLine cliArgs = this.parseCLI(options, Arrays.asList(rawArgs));
		
		// Update locale
		Locale newLocale = options.getIfPresent(StandardCompilerOptions.LOCALE, this.getLocale());
		if (this.locale != newLocale) {
			this.setLocale(newLocale);
		} else if (!options.isPresent(StandardCompilerOptions.LOCALE)) {
			options.set(StandardCompilerOptions.LOCALE, this.getLocale());
		}
		
		List<? extends Diagnostic> optionDiagnostics = cliArgs.getOptionDiagnostics();
		if (!optionDiagnostics.isEmpty()) {
			AbstractDiagnosticReporter reporter = this.getReporter();
			optionDiagnostics.forEach(reporter);
			reporter.flush();
			return CLIResult.FAILURE_NO_OUTPUT; // TODO: better code?
		}
		
		if (options.get(StandardCompilerOptions.INIT))
			return this.writeConfigFile(options);
		
		if (options.get(StandardCompilerOptions.PRINT_VERSION))
			return this.printVersion();
		
		if (options.get(StandardCompilerOptions.PRINT_HELP))
			return this.printHelp(options, false);
		
		Path configFile = null;
		Path projectDir = cliArgs.getOptions().get(StandardCompilerOptions.PROJECT);
		if (projectDir != null) {
			if (!cliArgs.getFileNames().isEmpty()) {
				// TODO: resport diagnostic
				return CLIResult.FAILURE_NO_OUTPUT;
			}
			Path resolvedDir = this.resolvePath(projectDir);
			if (Files.isDirectory(resolvedDir)) {
				configFile = resolvedDir.resolve(CONFIG_FILE_NAME);
				if (!Files.exists(configFile)) {
					// TODO: report diagnostic
					return CLIResult.FAILURE_NO_OUTPUT;
				}
			} else {
				configFile = resolvedDir;
				if (!Files.exists(configFile)) {
					// TODO: report diagnostic
					return CLIResult.FAILURE_NO_OUTPUT;
				}
			}
		} else if (cliArgs.getFileNames().isEmpty()) {
			// Search for config file
			configFile = this.findConfigFile();
		}
		
		if (cliArgs.getFileNames().isEmpty() && configFile == null) {
			// We don't have any files to process
			CLIResult res1 = this.printVersion();
			CLIResult res2 = this.printHelp(options, false);// TODO: only command line
			return CLIResult.merge(res1, res2);
		}
		
		if (configFile != null) {
			// TODO: load from config file
			ParsedCommandLine configArgs = this.parseConfigFile(configFile, cliArgs.getOptions());
			if (cliArgs.getOptions().get(StandardCompilerOptions.PRINT_CONFIG, false)) {
				this.writeConfigFile(configArgs.getOptions(), this.getOutStream());
				return CLIResult.SUCCESS_NO_OUTPUT;
			}
			
			if (configArgs.getOptions().get(StandardCompilerOptions.WATCH)) {
				// TODO: set watch of files
				throw new NotFinishedException();
			} else if (this.isIncrementalCompilation(configArgs.getOptions())) {
				return this.compileIncrmental(configArgs);
			} else {
				return this.compile(cliArgs.getFileNames(), cliArgs.getProjectReferences(), cliArgs.getOptions());
			}
		} else {
			if (cliArgs.getOptions().get(StandardCompilerOptions.PRINT_CONFIG, false)) {
				this.writeConfigFile(cliArgs.getOptions(), this.getOutStream());
				return CLIResult.SUCCESS_NO_OUTPUT;
			}
			
			if (cliArgs.getOptions().get(StandardCompilerOptions.WATCH)) {
				// TODO: set watch of files
				throw new NotFinishedException();
			} else if (this.isIncrementalCompilation(cliArgs.getOptions())) {
				return this.compileIncrmental(cliArgs);
			} else {
				// cliArgs.getProjectReferences() should be empty
				return this.compile(cliArgs.getFileNames(), cliArgs.getProjectReferences(), cliArgs.getOptions());
			}
		}
	}
	
	@Override
	public int applyAsInt(String[] args) {
		try {
			return this.callInternal(args).getCode();
		} catch (Exception e) {
			e.printStackTrace(this.err);
			return CLIResult.REALLY_BAD.getCode();
		}
	}
	
	public static void main(String... args) {
		CLIRunner runner = new CLIRunner();
		
		int result;
		try {
			result = runner.applyAsInt(args);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		System.exit(result);
	}
}
