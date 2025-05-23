package com.example.auction.auctionbid;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.test.repository.TestAuctionBidRepository;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class AuctionBidTest {
    @Autowired
    RedisService redisService;
    @Autowired
    TestAuctionBidRepository testAuctionBidRepository;
    @Autowired
    TestService testService;

    @Test
    void 레디스만료이벤트받아서낙찰내역저장하기(){
        //given
        redisService.setKeyValue("AuctionBid:","", Duration.ofSeconds(10));
    }
}
