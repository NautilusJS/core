package com.mindlin.jsast.impl.writer;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import com.mindlin.jsast.fs.SourcePosition;
import com.mindlin.jsast.writer.JSWriterOptions;
import com.mindlin.jsast.writer.StandardJSWriterOptions;

class WriterHelper implements Closeable {
	private final JSWriterOptions options;
	protected final Writer parent;
	protected int indentLevel;
	private String indent = "";
	private int newlineBacklog = 0;
	protected Stack<WriterHelperContext> context = new Stack<>();
	
	public WriterHelper(JSWriterOptions options, Writer parent) {
		this.options = options;
		this.indentLevel = options.baseIndent;
		this.parent = parent;
		this.context.push(new WriterHelperContext());
	}
	
	// ===== SourceMap stuff =====
	
	public void beginRegion(SourcePosition srcStart) {
		// TODO impl
	}
	
	public void endRegion(SourcePosition srcEnd) {
		
	}
	
	public void pushIndent() {
		this.indentLevel++;
		this.indent += this.options.indentStyle;
	}
	
	public void popIndent() {
		if (--this.indentLevel < this.options.baseIndent) {
			this.indentLevel++;
			return;
		}
		this.indent = this.indent.substring(0, this.indent.length() - this.options.indentStyle.length());
	}
	
	public void pushContext() {
		this.context.push(new WriterHelperContext(this.context.peek()));
	}
	
	public void popContext() {
		this.context.pop();
	}
	
	public void doFinishWithNewline(boolean enableNewline) {
		this.context.peek().noNewline = !enableNewline;
	}
	
	public void finishStatement(boolean semicolon) {
		if (this.context.peek().noNewline)
			return;
		if (semicolon)
			this.append(';');
		this.newline();
	}
	
	@Override
	public void close() {
		try {
			this.parent.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void flush() {
		flushNewlines();
		try {
			this.parent.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public WriterHelper append(long srcStart, long srcEnd, String s) {
		flushNewlines();
		try {
			this.parent.append(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public WriterHelper append(char c) {
		flushNewlines();
		try {
			this.parent.append(c);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public WriterHelper append(CharSequence csq, int start, int end) {
		flushNewlines();
		return doAppend(csq, start, end);
	}
	
	public WriterHelper append(CharSequence csq) {
		flushNewlines();
		return doAppend(csq, 0, csq.length());
	}
	
	public WriterHelper appendIsolated(CharSequence csq) {
		space();
		append(csq);
		space();
		return this;
	}
	
	public WriterHelper appendOptIsolated(CharSequence csq) {
		optionalSpace();
		append(csq);
		optionalSpace();
		return this;
	}
	
	public WriterHelper appendOptIsolated(char c) {
		optionalSpace();
		append(c);
		optionalSpace();
		return this;
	}
	
	protected void flushNewlines() {
		if (this.newlineBacklog == 0)
			return;
		String newline = "\n" + this.indent;
		while (this.newlineBacklog > 0) {
			this.newlineBacklog--;
			doAppend(newline, 0, newline.length());
		}
	}
	
	protected WriterHelper doAppend(CharSequence csq, int start, int end) {
		try {
			this.parent.append(csq, start, end);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public WriterHelper newline() {
		this.newlineBacklog++;
		return this;
	}
	
	public WriterHelper space() {
		append(this.options.space);
		return this;
	}
	
	public WriterHelper optionalSpace() {
		if (!this.options.get(StandardJSWriterOptions.MINIFY))
			space();
		return this;
	}
	
	protected class WriterHelperContext {
		boolean noNewline = false;
		
		protected WriterHelperContext() {
			
		}
		
		protected WriterHelperContext(WriterHelperContext parent) {
			this.noNewline = parent.noNewline;
		}
	}
}