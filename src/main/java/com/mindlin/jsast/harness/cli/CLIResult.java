package com.mindlin.jsast.harness.cli;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Result codes from {@link CLIRunner}.
 * 
 * @author mailmindlin
 */
@NonNullByDefault
public enum CLIResult {
	/**
	 * Success, no output generated
	 */
	SUCCESS_NO_OUTPUT(0),
	/**
	 * Success, output generated
	 */
	SUCCESS(0),
	/**
	 * Failure, no output emitted
	 */
	FAILURE_NO_OUTPUT(1),
	/**
	 * Failure, but partial output emitted
	 */
	FAILURE(2),
	/**
	 * Unexpected failure
	 */
	REALLY_BAD(-1),
	;
	private static @Nullable CLIResult[][] MERGE_TABLE;
	protected static CLIResult merge(CLIResult first, CLIResult second) {
		CLIResult[][] table;
		if ((table = MERGE_TABLE) == null) {
			table = MERGE_TABLE = new CLIResult[][] {
				//                        SUCCESS_NO_OUTPUT  SUCCESS     FAILURE_NO_OUTPUT  FAILURE     REALLY_BAD
				/* SUCCESS_NO_OUTPUT */ { SUCCESS_NO_OUTPUT, SUCCESS,    FAILURE_NO_OUTPUT, FAILURE,    REALLY_BAD, },
				/* SUCCESS */           { SUCCESS,           SUCCESS,    FAILURE,           FAILURE,    REALLY_BAD, },
				/* FAILURE_NO_OUTPUT */ { FAILURE_NO_OUTPUT, FAILURE,    FAILURE_NO_OUTPUT, FAILURE,    REALLY_BAD, },
				/* FAILURE */           { FAILURE,           FAILURE,    FAILURE,           FAILURE,    REALLY_BAD, },
				/* REALLY_BAD */        { REALLY_BAD,        REALLY_BAD, REALLY_BAD,        REALLY_BAD, REALLY_BAD, },
			};
		}
		
		try {
			return table[first.ordinal()][second.ordinal()];
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			String message = String.format("Unable to merge %s with %s", first, second);
			throw new IllegalArgumentException(message, e);
		}
	}
	
	public static CLIResult merge(CLIResult first, CLIResult...rest) {
		CLIResult result = first;
		for (CLIResult value : rest)
			result = merge(result, value);
		return result;
	}
	
	private final int code;
	
	CLIResult(int code) {
		this.code = code;
	}

	public boolean hasOutput() {
		return this != SUCCESS_NO_OUTPUT && this != FAILURE_NO_OUTPUT;
	}
	public int getCode() {
		return code;
	}
}
