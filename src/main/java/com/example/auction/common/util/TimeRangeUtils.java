package com.example.auction.common.util;

public class TimeRangeUtils {

	private static final long BLOCK_DURATION = 10 * 60 * 1000; // 10분 in milliseconds

	//현재 시간 기준으로 이전 블록의 시작과 끝을 구함
	//지금이 10:06이면 -> 09:50~10:00 블록 반환
	public static TimeRange getPreviousTimeBlock(long currentMillis) {
		long currentBlock = currentMillis / BLOCK_DURATION;
		long previousBlockStart = (currentBlock - 1) * BLOCK_DURATION;
		long previousBlockEnd = previousBlockStart + BLOCK_DURATION;

		return new TimeRange(previousBlockStart, previousBlockEnd);
	}

	//현재 시간 기준으로 속한 블록의 키를 반환 (캐시 키로 활용 가능)
	public static long getCurrentBlockKey(long currentMillis) {

		return (currentMillis / BLOCK_DURATION) - 1;
	}

	//시간 범위를 담는 내부 클래스
	public static class TimeRange {
		public final long start;
		public final long end;

		public TimeRange(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}
}
