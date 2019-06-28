package com.mindlin.jsast.exception;

public class NotFinishedException extends RuntimeException {

	public NotFinishedException() {
		super();
	}

	public NotFinishedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFinishedException(String message) {
		super(message);
	}

	public NotFinishedException(Throwable cause) {
		super(cause);
	}
	
}
