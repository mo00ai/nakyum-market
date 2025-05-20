package com.example.auction.domain.dips.service;


import static com.example.auction.common.exception.ErrorCode.DB_LOCK_CONFLICT;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.dips.dto.response.DipsFindResponseDto;
import com.example.auction.domain.dips.entity.Dips;
import com.example.auction.domain.dips.repository.DipsRepository;
import com.example.auction.domain.dips.repository.ProductRepository;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import java.time.Duration;
import java.util.List;
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

    @Transactional
    public boolean save(UserDetails userDetails, Long productId) {

        User user = finnByUser(userDetails.getUsername());
        // 유저 별 고유 ID
        String lockKey = "lock:" + user.getId();
        String countKey = "count:" + user.getId();
        // DB 락 체크
        if(redisService.getKeyValue(lockKey) != null) throw new CustomException(DB_LOCK_CONFLICT,DB_LOCK_CONFLICT.getMessage());

        // 테스트용 코드
        Product product = productRepository.findById(1L).orElseThrow(() ->
            new RuntimeException("Product not found"));

        boolean isClick = dipsRepository.findByUserIdAndProductId(user.getId(), productId)
            .isPresent();

        // 첫 클릭
        Long count = redisService.getKeyLongValue(countKey);
        if(count == null) {
            redisService.setKeyValue(countKey, 1L, Duration.ofSeconds(20));
        }else{
            count = redisService.incrementValue(countKey); // count + 1
        }

        // 짧은 시간 내 5번 이상 클릭 시 DB 락
        int max = 5;

        if(count != null && count >= max){
            redisService.setKeyValue(lockKey,user.getId(),Duration.ofSeconds(10));
        }

        if(isClick){
            removeDips(user.getId(), productId);
        }else{
            dipsRepository.save(Dips.of(user, product));
        }
        return !isClick;
    }

    @Transactional
    public void removeDips(Long userId, Long productId){
        dipsRepository.delete(dipsRepository.findByUserIdAndProductIdOrElseThrow(userId,productId));
    }


    public List<DipsFindResponseDto> findDips(UserDetails userDetails) {
        User user = finnByUser(userDetails.getUsername());
        return findDipsByUserIdOrElseThrow(user.getId())
            .stream()
            .map(DipsFindResponseDto::toDto)
            .toList()
            ;
    }


    private User finnByUser(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() ->
            new RuntimeException("User not found"));
    }

    private List<Dips> findDipsByUserIdOrElseThrow(Long userId){
        return dipsRepository.findDipsByUserIdOrElseThrow(userId);
    }
}
