package com.example.auction.common.scheduler;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCountScheduler {

    private final ProductService productService;
    private final RedisService redisService;

    @Scheduled(cron = "0 */10 * * * *")
    public void updateCount() {
        ScanOptions options = ScanOptions.scanOptions().match("product:count:*").count(100).build();

        try (Cursor<byte[]> cursor = redisService.getRedisConnection()
            .keyCommands().scan(options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());

                Long count = redisService.getKeyLongValue(key);
                productService.updateCount(Long.valueOf(key.split("product:count:")[1]), count);

                redisService.deleteKeyValue(key);
            }
        }
    }
}
