package com.example.auction.common.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.searchLog.entity.SearchLog;
import com.example.auction.domain.searchLog.service.SearchLogService;
import com.example.auction.domain.topKeyword.service.TopKeywordService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchBulkScheduler {

	private final SearchLogService searchLogService;
	private final TopKeywordService topKeywordService;
	private final RedisService redisService;

	@Scheduled(cron = "0 */10 * * * *")
	public void insertData() {

		ScanOptions options = ScanOptions.scanOptions().match("search_logs:*").count(100).build();

		try (Cursor<byte[]> cursor = redisService.getRedisConnection().keyCommands().scan(options)) {
			while (cursor.hasNext()) {
				String key = new String(cursor.next());

				Set<ZSetOperations.TypedTuple<String>> zSetObject = redisService.getZSetRangeByScoreTuple(key, 0L, -1L);

				List<SearchLog> logs = zSetObject.stream()
					.map(log -> {
						String keyword = log.getValue().split(":")[0].replaceAll("^\"|\"$", "");
						LocalDateTime time = Instant.ofEpochMilli(log.getScore().longValue())
							.atZone(ZoneId.systemDefault())
							.toLocalDateTime();
						return SearchLog.of(keyword, time);
					})
					.collect(Collectors.toList());

				searchLogService.saveSearchLogs(logs);

			}
		}

	}
}
