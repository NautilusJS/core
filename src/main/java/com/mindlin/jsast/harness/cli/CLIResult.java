package com.mindlin.jsast.harness.cli;

public enum CLIResult {
	SUCCESS(0),
	SUCCESS_NO_OUTPUT(0),
	FAILURE_NO_OUTPUT(1),
	FAILURE(2),
	REALLY_BAD(-1),
	;
	private final int code;
	
	CLIResult(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
