package com.mindlin.jsast.impl.util;

import java.nio.InvalidMarkException;

public interface CharacterStream {
	/**
	 * Get current character (the character at the current {@link #position()}
	 * in the stream).
	 * 
	 * @return current character
	 * 
	 * @throws IllegalStateException If the CharacterStream hasn't started
	 */
	default char current() throws IllegalStateException {
		try {
			return peek(0);
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalStateException(e);
		}
	}

	default char next() throws IndexOutOfBoundsException {
		return next(1);
	}

	default char next(long offset) throws IndexOutOfBoundsException {
		skip(offset);
		return current();
	}

	default char peek() throws IndexOutOfBoundsException {
		return peek(1);
	}

	char peek(long offset) throws IndexOutOfBoundsException;
	
	default char peekSafe() {
		return peekSafe(1);
	}
	
	default char peekSafe(long offset) {
		return peek(offset, '\0');
	}
	
	default char peek(long offset, char defaultValue) {
		return hasNext(offset) ? peek(offset) : defaultValue;
	}

	CharacterStream skip(long offset) throws IndexOutOfBoundsException;

	/**
	 * Get current position in the stream.
	 * @return Current position, or -1 if the stream has not yet been read from.
	 */
	long position();

	CharacterStream position(long pos) throws IndexOutOfBoundsException;

	/**
	 * Whether a call to {@link #next()} is valid (i.e., this CharacterStream
	 * has at least 1 more character).
	 * <br/>
	 * This method should be equivalent to {@link #hasNext(long) hasNext(1)}.
	 * 
	 * @return Whether there is a next character
	 */
	default boolean hasNext() {
		return hasNext(1);
	}

	/**
	 * Whether this CharacterStream has at least {@code num} more
	 * characters.
	 * 
	 * @param num
	 *            Number of characters to test for
	 * @return Whether there are that many characters remaining
	 */
	boolean hasNext(long num);
	
	@Deprecated
	default boolean isEOL() {
		if (!hasNext() || position() < 0)
			return false;
		
		final char c = current();
		return c == '\r' || c == '\n';
	}

	@Deprecated
	default boolean isWhitespace() {
		return position() >= 0 && hasNext() && Characters.isJsWhitespace(current());
	}

	@Deprecated
	default CharacterStream skipWhitespace() {
		return skipWhitespace(true);
	}
	
	@Deprecated
	CharacterStream skipWhitespace(boolean passNewlines);
	
	CharacterStream mark();

	CharacterStream resetToMark() throws InvalidMarkException;
	
	CharacterStream unmark() throws InvalidMarkException;
	
	@Deprecated
	default CharacterStream skipTo(final char c) {
		while (next() != c)
			;
		return this;
	}

	String copyNext(long len) throws IndexOutOfBoundsException;
	
	String copyFromMark() throws InvalidMarkException;
}