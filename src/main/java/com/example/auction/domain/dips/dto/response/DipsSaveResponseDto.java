package com.example.auction.domain.dips.dto.response;

import com.example.auction.domain.dips.entity.Dips;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DipsSaveResponseDto {

    private final Long userId;

    private final Long productId;

    public static DipsSaveResponseDto toDto(Dips dips){
        return new DipsSaveResponseDto(dips.getUser().getId(),dips.getProduct().getId());
    }

}
