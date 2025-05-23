package com.example.auction.domain.dips.controller;


import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.dips.dto.response.DipsFindResponseDto;
import com.example.auction.domain.dips.dto.response.DipsSaveResponseDto;
import com.example.auction.domain.dips.service.DipsService;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/products/dibs")
@RestController
@RequiredArgsConstructor
public class DipsController {

    private final DipsService dipsService;

    @PostMapping("/{productId}")
    public CommonResponse<Boolean> save(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long productId)
         {
        return CommonResponse.ok(dipsService.save(userDetails, productId));
    }

    @GetMapping
    public CommonResponse<List<ProductResponseDto>> findDips(
        @AuthenticationPrincipal UserDetails userDetails
    ){
        return CommonResponse.ok(dipsService.findDips(userDetails));
    }

}

