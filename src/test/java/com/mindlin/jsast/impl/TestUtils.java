package com.mindlin.jsast.impl;

import java.util.Arrays;

import org.junit.Assume;
import org.junit.jupiter.api.Assertions;

import junit.framework.ComparisonFailure;

public class TestUtils {
	public static final double EPSILON = .0001;
	
	public static final void assertNumberEquals(Number expected, Number actual, String message) {
		Assume.assumeNotNull(expected);
		Assertions.assertNotNull(actual, message);
		
		if (expected.longValue() != actual.longValue() && Math.abs(expected.doubleValue() - actual.doubleValue()) > EPSILON)
			throw new ComparisonFailure(message, expected.toString(), actual.toString());
	}
	
	public static final void assertNumberEquals(Number expected, Number actual) {
		if (expected.longValue() != actual.longValue() && Math.abs(expected.doubleValue() - actual.doubleValue()) > EPSILON) {
			ComparisonFailure e = new ComparisonFailure(null, expected.toString(), actual.toString());
			StackTraceElement[] trace = getStackTraceRelative(1);
			e.setStackTrace(trace);
			throw e;
		}
	}
	
	static final StackTraceElement[] getStackTraceRelative(int rel) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		return Arrays.copyOf(trace, trace.length - rel - 2);
	}
}
