package com.example.auction.domain.dips.dto.response;

import com.example.auction.domain.dips.entity.Dips;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DipsFindResponseDto {

    private final Long productId;

    private final String productName;

    private final Long price;

    public static DipsFindResponseDto toDto(Dips dips){
        return new DipsFindResponseDto(
            dips.getProduct().getId(),
            dips.getProduct().getName(),
            dips.getProduct().getFinalPrice()
        );
    }

}
