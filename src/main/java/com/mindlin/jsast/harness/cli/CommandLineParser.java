package com.mindlin.jsast.harness.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.exception.NotFinishedException;
import com.mindlin.jsast.harness.CompilerOption;
import com.mindlin.jsast.harness.config.ParsedCommandLine;

@NonNullByDefault
public abstract class CommandLineParser {
	protected abstract @Nullable CompilerOption<?> getOptionByName(String name);
	
	protected void parseResponseFile(ParsedCommandLine result, Path base, String path) {
		Path resolved = base.resolve(path);
		assert resolved != null;
		this.parseResponseFile(result, resolved);
	}
	
	@SuppressWarnings("null")
	protected void parseResponseFile(ParsedCommandLine result, Path filePath) {
		// Get new base directory
		Path base = filePath.getParent();
		try (Scanner scanner = new Scanner(Files.newBufferedReader(filePath))) {
			this.parseArgs(result, base, scanner);
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
			//XXX emit diagnostic
			throw new NotFinishedException();
		}
	}
	
	protected void parseArgs(ParsedCommandLine result, Path base, Iterator<? extends String> args) {
		while (args.hasNext()) {
			@SuppressWarnings("null")
			String command = args.next();
			this.parseArg(result, base, command, args);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void parseArg(ParsedCommandLine result, Path base, String command, Iterator<? extends String> argIterator) {
		if (command.startsWith("@")) {
			// Parse file
			String fileName = command.substring(1);
			assert fileName != null;
			this.parseResponseFile(result, base, fileName);
		} else if (command.startsWith("-")) {
			String commandName = command.substring(command.startsWith("--") ? 2 : 1);
			assert commandName != null;
			CompilerOption<?> option = this.getOptionByName(commandName);
			if (option == null) {
				//TODO: emit diagnostic
				throw new NotFinishedException();
			}
			
			@Nullable Object value;
			try {
				value = option.parse(result::reportParseDiagnostic, command, argIterator);
			} catch (RuntimeException e) {
				//TODO handle
				e.printStackTrace();
				throw new NotFinishedException();
			}
			if (value != null)
				result.getOptions().set((CompilerOption<Object>) option, value);
		} else {
			result.addFileName(command);
		}
	}
	
	@SuppressWarnings("null")
	public ParsedCommandLine apply(Path base, List<String> args) {
		ParsedCommandLine result = new ParsedCommandLine();
		
		this.parseArgs(result, base, args.iterator());
		
		return result;
	}
}
