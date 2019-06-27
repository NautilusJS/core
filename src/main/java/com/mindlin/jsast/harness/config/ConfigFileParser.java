package com.mindlin.jsast.harness.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;

import com.mindlin.jsast.harness.CompilerOptions;

/**
 * Read data from config file
 * @author mailmindlin
 */
public abstract class ConfigFileParser {
	protected final Path configFile;
	
	public ConfigFileParser(Path configFile) {
		this.configFile = configFile;
	}
	
	public void createWatch() {
		WatchService watch;
		try {
			watch = configFile.getFileSystem().newWatchService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract CompilerOptions load();
	
	/**
	 * Read config from {@code tsconfig.json} file.
	 * 
	 * @author mailmindlin
	 */
	public static class TSConfigFileParser extends ConfigFileParser {
		public TSConfigFileParser(Path configFile) {
			super(configFile);
		}
		
		protected void read(Path file, CompilerOptions options) {
			try (BufferedReader br = Files.newBufferedReader(file)) {
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public CompilerOptions load() {
			
			
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
