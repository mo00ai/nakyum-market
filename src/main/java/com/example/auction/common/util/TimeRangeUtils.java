package com.example.auction.common.util;

public class TimeRangeUtils {

	private static final long BLOCK_DURATION = 10 * 60 * 1000;

	public static TimeRange getPreviousTimeBlock(long currentMillis) {
		long currentBlock = currentMillis / BLOCK_DURATION;
		long previousBlockStart = (currentBlock - 1) * BLOCK_DURATION;
		long previousBlockEnd = previousBlockStart + BLOCK_DURATION;

		return new TimeRange(previousBlockStart, previousBlockEnd);
	}

	public static long getCurrentBlockKey(long currentMillis) {
		return (currentMillis / BLOCK_DURATION) - 1;
	}

	public static class TimeRange {
		public final long start;
		public final long end;

		public TimeRange(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}
}
