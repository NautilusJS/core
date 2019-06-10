package com.mindlin.jsast.harness.cli;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.mindlin.jsast.exception.NotFinishedException;
import com.mindlin.jsast.harness.CompilerOptions;

public class ConfigFileWriter implements Flushable, Closeable {
	protected @Nullable Appendable out;
	
	public ConfigFileWriter(@NonNull Appendable out) {
		this.out = out;
	}
	
	protected void assertOpen() {
		if (this.out == null)
			throw new IllegalStateException();
	}
	
	public void write(CompilerOptions options) {
		//TODO finish
		throw new NotFinishedException();
	}
	
	@Override
	public void flush() throws IOException {
		if (this.out instanceof Flushable) {
			((Flushable) this.out).flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		if (this.out != null) {
			if (this.out instanceof Closeable)
				((Closeable) this.out).close();
			this.out = null;
		}
	}
}
