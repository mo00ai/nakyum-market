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
import java.util.stream.Collectors;
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
    private final String LOCK_KEY = "dips:lock:";
    private final String LIKE_KEY = "dips:like:";
    private final String COUNT_KEY = "dips:count:";
    private final String DB_LIKE_KEY = "dips:like:db:";

    @Transactional
    public boolean save(UserDetails userDetails, Long productId) {
        User user = findByUserOrNotFoundThrow(userDetails.getUsername());
        // 유저 별 고유 ID
        String lockKey = LOCK_KEY + user.getId();
        String dipsKey = LIKE_KEY + user.getId();
        String countKey = COUNT_KEY + user.getId();
        String dbLikeKey = DB_LIKE_KEY + user.getId(); // db 에서 가져온 찜 체크용

        // DB 락 체크
        if (redisService.getKeyValue(lockKey) != null) {
            throw new CustomException(DB_LOCK_CONFLICT, DB_LOCK_CONFLICT.getMessage());
        }

        productRepository.findById(productId).orElseThrow(() ->
            new CustomException(PRODUCT_NOT_FOUND));

        Long userId = user.getId();

        boolean isClick = redisService.isOpsForSet(dipsKey, productId);

        setRock(countKey, lockKey, userId);

        // 로그인하고 첫 클릭이면 db에서 데이터 가져옴
        if (!redisService.hasKey(dipsKey) || redisService.getSetSize(dipsKey) == 0) {
            if (firstSignIn(userId, dipsKey, dbLikeKey, productId)) return false; // 첫 클릭이 이미 db에 있던 상품이면
        }

        if (isClick) {
            redisService.removeOpsForSet(dipsKey, productId);
            // 원래 DB에 있던 찜이라면 DB 에서도 삭제
            if (redisService.isOpsForSet(dbLikeKey, productId)) {
                Dips dips = dipsRepository.findByUserIdAndProductIdOrElseThrow(userId, productId);
                dipsRepository.delete(dips);
                redisService.removeOpsForSet(dbLikeKey, productId); // 체크용 레디스 삭제
            }
        } else {
            redisService.setOpsForSet(dipsKey, productId);
        }
        return !isClick;
    }

    public void setRock(String countKey, String lockKey, Long userId) {
        // 상품 첫 클릭
        Long count = redisService.getKeyLongValue(countKey);
        if (count == null) {
            redisService.setKeyValue(countKey, 1L, Duration.ofSeconds(COUNT_TIME));
        } else {
            count = redisService.incrementValue(countKey); // count + 1
        }
        // 짧은 시간 내 5번 이상 클릭 시 DB 락
        if (count != null && count >= MAX_COUNT) {
            redisService.setKeyValue(lockKey, userId, Duration.ofSeconds(LOCK_TIME));
        }
    }

    /**
     *  첫 로그인일 때 동작하는 로직 db 에서 가져와서 redis 에 반영하고 만약 첫 클릭이 db에 있던 값이면 db 에서 삭제함
     * @param userId
     * @param dipsKey
     * @param dbLikeKey
     * @param productId
     * @return true 면 db 에서 삭제 false 면 정상
     */
    private boolean firstSignIn(Long userId, String dipsKey, String dbLikeKey, Long productId) {
        for (Dips dipsData : dipsRepository.findAllDipsByUserId(userId)) {
            Long pid = dipsData.getProduct().getId();

            if (productId.equals(pid)) {
                dipsRepository.delete(dipsData);
                return true;
            }
            redisService.setOpsForSet(dipsKey, pid);
            redisService.setOpsForSet(dbLikeKey, pid);
        }
        return false;
    }


    @Transactional
    public void removeRedis(CustomUserDetails userDetail) {
        User user = findByUserOrNotFoundThrow(userDetail.getUsername());

        String dipsKey = LIKE_KEY + user.getId();
        String dbLikeKey = DB_LIKE_KEY + user.getId();

        // Redis 에서 찜 목록 조회
        Set<Long> allDips = redisService.findOpsForSet(dipsKey).stream()
            .map(data -> ((Integer) data).longValue())
            .collect(Collectors.toSet());

        // 이미 db에 저장된 값
        Set<Long> dbDips = redisService.findOpsForSet(dbLikeKey).stream()
            .map(data -> ((Integer) data).longValue())
            .collect(Collectors.toSet());

        // 기존 DB에 없던 새 찜만 저장
        allDips.removeAll(dbDips);

        // 새 찜 상품 ID 목록으로 한 번에 Product 조회 (IN 쿼리)
        List<Product> productList = productRepository.findAllById(allDips);

        // 일부 상품이 삭제되었을 경우
        if (productList.size() != allDips.size()) {
            throw new CustomException(PRODUCT_NOT_FOUND);
        }

        // Dips 저장
        List<Dips> dipsList = productList.stream()
            .map(product -> Dips.of(user, product))
            .toList();
        dipsRepository.saveAll(dipsList);

        // Redis 데이터 제거
        redisService.removeOpsForSetALL(dipsKey);
        redisService.removeOpsForSetALL(dbLikeKey);
    }

    public List<ProductResponseDto> findDips(UserDetails userDetails) {
        User user = findByUserOrNotFoundThrow(userDetails.getUsername());

        String dipsKey = LIKE_KEY + user.getId();
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
                    redisService.getKeyLongValue(COUNT_KEY + product.getId())
                )
            ).toList();
    }

    private User findByUserOrNotFoundThrow(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() ->
            new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
    }

}
