package com.example.auction.product;

import com.example.auction.common.scheduler.ProductCountScheduler;
import com.example.auction.common.service.RedisService;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/sparta_auction",
    "spring.datasource.username=root",
    "spring.datasource.password=1234",
    "JWT_SECRET_KEY=5BlIH6F+HFOKuuWk/R7I6TlAExHzw6Ac2bickLcgc90=",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "file.upload-dir=C:\\Users\\dnjs7\\OneDrive\\사진\\Feedback\\"
})
public class productScheduleTest {
    @Autowired
    ProductCountScheduler productCountScheduler;
    @Autowired
    RedisService redisService;


    @Test
    void 조회수_스케쥴_테스트(){
        redisService.setKeyValue("product:count:1","", Duration.ofSeconds(30));
        redisService.setKeyValue("product:count:2","", Duration.ofSeconds(30));
        redisService.setKeyValue("product:count:3","", Duration.ofSeconds(30));

        System.out.println("2");

        productCountScheduler.updateCount();
    }

}
