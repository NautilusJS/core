package com.mindlin.jsast.impl.util;

import java.nio.InvalidMarkException;
import java.util.EmptyStackException;

public abstract class AbstractCharacterStream implements CharacterStream {
	protected final LongStack marks = new LongStack();
	
	@Override
	public String copyNext(long len) {
		StringBuilder sb = new StringBuilder(Math.toIntExact(len));
		while (len-- > 0)
			sb.append(next());
		return sb.toString();
	}
	
	@Override
	public AbstractCharacterStream mark() {
		long pos = position();
		this.marks.push(pos);
		return this;
	}
	
	protected long popMark() throws InvalidMarkException {
		try {
			return this.marks.pop();
		} catch (EmptyStackException e0) {
			InvalidMarkException e = new InvalidMarkException();
			e.addSuppressed(e0);
			throw e;
		}
	}
	
	@Override
	public AbstractCharacterStream resetToMark() throws InvalidMarkException {
		long pos = this.popMark();
		this.position(pos);
		return this;
	}
	
	@Override
	public AbstractCharacterStream unmark() throws InvalidMarkException {
		this.popMark();
		return this;
	}
}