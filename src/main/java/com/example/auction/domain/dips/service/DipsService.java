package com.example.auction.domain.dips.service;


import static com.example.auction.common.exception.ErrorCode.DB_LOCK_CONFLICT;
import static com.example.auction.domain.dips.exception.DipsErrorCode.NOT_FOUND_DIPS;
import static com.example.auction.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.dips.entity.Dips;
import com.example.auction.domain.dips.repository.DipsRepository;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.user.auth.security.CustomUserDetails;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DipsService {

    private final RedisService redisService;
    private final DipsRepository dipsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final int MAX_COUNT = 5;
    private final int COUNT_TIME = 5;
    private final int LOCK_TIME = 10;

    @Transactional
    public boolean save(UserDetails userDetails, Long productId) {
        User user = findByUserOrNotFoundThrow(userDetails.getUsername());
        // 유저 별 고유 ID
        String lockKey = "dips:lock:" + user.getId();
        String dipsKey = "dips:like:" + user.getId();
        String countKey = "dips:count" + user.getId();
        // DB 락 체크
        if (redisService.getKeyValue(lockKey) != null) {
            throw new CustomException(DB_LOCK_CONFLICT, DB_LOCK_CONFLICT.getMessage());
        }

        Product product = productRepository.findById(productId).orElseThrow(() ->
            new CustomException(PRODUCT_NOT_FOUND));

        boolean isClick = redisService.isOpsForSet(dipsKey, product.getId());

        // 첫 클릭
        Long count = redisService.getKeyLongValue(countKey);
        if (count == null) {
            redisService.setKeyValue(countKey, 1L, Duration.ofMinutes(COUNT_TIME));
        } else {
            count = redisService.incrementValue(countKey); // count + 1
        }

        // 짧은 시간 내 5번 이상 클릭 시 DB 락
        if (count != null && count >= MAX_COUNT) {
            redisService.setKeyValue(lockKey, user.getId(), Duration.ofMinutes(LOCK_TIME));
        }

        if (isClick) {
            redisService.removeOpsForSet(dipsKey, product.getId());
        } else {
            redisService.setOpsForSet(dipsKey, product.getId());
        }
        return !isClick;
    }

    @Transactional
    public void removeRedis(CustomUserDetails userDetail) {
        User user = findByUserOrNotFoundThrow(userDetail.getUsername());

        String dipsKey = "dips:like:" + user.getId();
        Set<Object> dates = redisService.findOpsForSet(dipsKey);

        List<Product> productList = dates.stream()
            .map(data -> ((Integer) data).longValue()) // Integer → Long 변환
            .map(productId -> productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND)))
            .toList();
        List<Dips> dipsList = productList.stream().map(product->Dips.of(user,product)).toList();
        dipsRepository.saveAll(dipsList);


        redisService.removeOpsForSetALL(dipsKey);
    }


    public List<ProductResponseDto> findDips(UserDetails userDetails) {
        User user = findByUserOrNotFoundThrow(userDetails.getUsername());

        String dipsKey = "dips:like:" + user.getId();
        Set<Object> dates = redisService.findOpsForSet(dipsKey);

        if (dates == null || dates.isEmpty()) {
            throw new CustomException(NOT_FOUND_DIPS);
        }

        List<Product> productList = dates.stream()
            .map(Object::toString)
            .map(Long::valueOf)
            .map(id -> productRepository.findById(id)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND)))
            .toList();

        return productList
            .stream()
            .map(product -> ProductResponseDto.from(
                    product.getImage().getUploadFileName(),
                    product,
                    redisService.getKeyLongValue("product:count:" + product.getId())
                )
            ).toList();
    }

    private User findByUserOrNotFoundThrow(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() ->
            new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
    }

}
