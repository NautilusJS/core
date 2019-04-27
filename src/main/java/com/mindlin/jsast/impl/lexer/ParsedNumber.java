package com.mindlin.jsast.impl.lexer;

import java.util.Objects;

public abstract class ParsedNumber extends Number {
	protected int length;
	protected boolean separators;
	
	public ParsedNumber(int length, boolean separators) {
		this.length = length;
		this.separators = separators;
	}

	public abstract NumericLiteralType getBase();
	
	public int length() {
		return this.length;
	}

	public boolean hasSeparators() {
		return this.separators;
	}

	@Override
	public int intValue() {
		return Math.toIntExact(this.longValue());
	}

	@Override
	public float floatValue() {
		return (float) this.doubleValue();
	}

	@Override
	public double doubleValue() {
		return (double) this.longValue();
	}
	
	public static class ParsedInteger extends ParsedNumber {
		protected NumericLiteralType base;
		protected long value;
		
		public ParsedInteger(NumericLiteralType base, long value, int length, boolean separators) {
			super(length, separators);
			this.base = Objects.requireNonNull(base);
			this.value = value;
		}
		
		@Override
		public long longValue() {
			return this.value;
		}
		
		@Override
		public NumericLiteralType getBase() {
			return this.base;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Number)
				return this.value == ((Number) obj).longValue();
			return super.equals(obj);
		}
		
		@Override
		public String toString() {
			return String.format("ParsedInteger{base=%s, value=%d}", this.base, this.value);
		}
	}
	
	public static class ParsedDouble extends ParsedNumber {
		protected NumericLiteralType base;
		protected double value;
		
		public ParsedDouble(NumericLiteralType base, double value, int length, boolean separators) {
			super(length, separators);
			this.base = Objects.requireNonNull(base);
			this.value = value;
		}
		
		@Override
		public double doubleValue() {
			return this.value;
		}
		
		@Override
		public long longValue() {
			return (long) value;
		}

		@Override
		public NumericLiteralType getBase() {
			return this.base;
		}
		
	}
	
}
