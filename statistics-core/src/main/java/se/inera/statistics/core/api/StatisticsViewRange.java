package se.inera.statistics.core.api;

import java.util.EnumSet;

public enum StatisticsViewRange {
	DAILY(0),
	WEEKLY(1),
	MONTHLY(2),
	YEARLY(3);
	
	private final int code;
	
	private StatisticsViewRange(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static StatisticsViewRange fromCode(final int code) {
		for (final StatisticsViewRange vr : EnumSet.allOf(StatisticsViewRange.class)) {
			if (vr.getCode() == code) {
				return vr;
			}
		}
		
		throw new IllegalArgumentException("Code: " + code + " not present in enum " + StatisticsViewRange.class.getSimpleName());
	}
}
