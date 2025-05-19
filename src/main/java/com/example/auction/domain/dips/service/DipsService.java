package com.example.auction.domain.dips.service;


import com.example.auction.common.service.RedisService;
import com.example.auction.domain.dips.dto.response.DipsFindResponseDto;
import com.example.auction.domain.dips.dto.response.DipsSaveResponseDto;
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


    private final int LOCK_TIME = 10;
    private final int s = 3;

    @Transactional
    public String save(UserDetails userDetails, Long productId) {
        User user = finnByUser(userDetails.getUsername());


        // 테스트용 코드
        Product product = productRepository.findById(1L).orElseThrow(() ->
            new RuntimeException("Product not found"));

        dipsRepository.save(Dips.of(user, product));

        return "성공";
    }

    public List<DipsFindResponseDto> findDips(UserDetails userDetails) {
        User user = finnByUser(userDetails.getUsername());
        return findDipsByUserIdOrElseThrow(user.getId())
            .stream()
            .map(DipsFindResponseDto::toDto)
            .toList()
            ;
    }

    public void removeDips(Long userId, Long productId){
        dipsRepository.delete(dipsRepository.findByUserIdAndProductIdOrElseThrow(userId,productId));
    }

    private User finnByUser(String userName) {
        return userRepository.findByEmail(userName).orElseThrow(() ->
            new RuntimeException("User not found"));
    }

    private List<Dips> findDipsByUserIdOrElseThrow(Long userId){
        return dipsRepository.findDipsByUserIdOrElseThrow(userId);
    }
}
