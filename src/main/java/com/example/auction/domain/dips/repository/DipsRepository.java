package com.example.auction.domain.dips.repository;



import static com.example.auction.domain.dips.exception.DipsErrorCode.NOT_FOUND_DIPS;

import com.example.auction.common.exception.CustomException;
import com.example.auction.domain.dips.entity.Dips;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DipsRepository extends JpaRepository<Dips, Long> {

    List<Dips> findDipsByUserId(Long id);

    Optional<Dips> findByUserIdAndProductId(Long userId, Long productId);

    default Dips findByUserIdAndProductIdOrElseThrow(Long userId, Long productId) {
        return findByUserIdAndProductId(userId,productId).orElseThrow(
            () -> new CustomException(NOT_FOUND_DIPS, NOT_FOUND_DIPS.getMessage())
        );
    }

    default List<Dips> findDipsByUserIdOrElseThrow(Long id) {
        List<Dips> list = findDipsByUserId(id);
        if (list.isEmpty()) {
            throw new CustomException(NOT_FOUND_DIPS, NOT_FOUND_DIPS.getMessage());
        }
        return list;
    }
}
