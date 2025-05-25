package com.example.auction.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.repository.ImageRepository;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.entity.UserRole;
import com.example.auction.domain.user.repository.UserRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    private final Long productId = 1L;
    private final int threadCount = 1000;


    @Test
    void 동시에_여러_유저가_조회시_정확한_조회수_증가() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1;
            executorService.submit(() -> {
                try {
                    productService.findProduct(productId, userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 종료 대기

        // 결과 확인
        Long viewCount = redisService.getKeyLongValue("product:count:" + productId);
        System.out.println("최종 조회수: " + viewCount);

        assertThat(viewCount).isEqualTo(threadCount); // 각 userId는 1번만 증가해야 함
    }

    @Test
    void 유저넣기(){
        Image image = imageRepository.findById(1L).get();

        for (int i = 1; i <= 1000; i++) {
            User user = User.builder()
                .email("user" + i + "@example.com")
                .password("password" + i)
                .nickname("유저" + i)
                .userRole(UserRole.USER)
                .address("서울시 강남구 " + i + "번지")
                .image(image)
                .provider("local")
                .build();
            userRepository.save(user);
        }

        System.out.println("✅ 1000명의 유저가 저장되었습니다.");
    }
}
