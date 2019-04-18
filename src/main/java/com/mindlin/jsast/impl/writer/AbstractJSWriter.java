package com.mindlin.jsast.impl.writer;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.writer.JSWriterOptions;

public abstract class AbstractJSWriter<E extends Tree> implements Closeable {
	protected final JSWriterOptions options;
	
	
	public AbstractJSWriter(JSWriterOptions options) {
		//TODO clone
		this.options = options;
	}
	
	protected String stringify(Number value) {
		double dValue = value.doubleValue();
		if (!Double.isFinite(dValue)) {
			if (Double.isNaN(dValue))
				return "NaN";
			if (Double.isInfinite(dValue))
				return (dValue == Double.POSITIVE_INFINITY) ? "Infinity" : "-Infinity";
			throw new IllegalArgumentException("Unknown non-finite value " + value);
		}
		
		String result = value.toString();
		if (result.length() < 3)
			return result;
		
		//TODO finish
		return result;
	}
	
	public abstract void write(E value, WriterHelper out);
	
	/**
	 * Write a comma-separated list with the given values
	 * @param values
	 * @param out
	 */
	public void writeList(List<? extends E> values, WriterHelper out) {
		boolean isFirst = true;
		for (E value : values) {
			if (!isFirst)
				out.append(',').optionalSpace();
			else
				isFirst = false;
			
			if (value != null)
				this.write(value, out);
		}
	}
	
	public <T> void writeList(List<T> values, WriterHelper out, BiConsumer<T, WriterHelper> serializer, Consumer<WriterHelper> delimiter) {
		boolean isFirst = true;
		for (T value : values) {
			if (!isFirst)
				delimiter.accept(out);
			else
				isFirst = false;
			
			serializer.accept(value, out);
		}
	}


	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
