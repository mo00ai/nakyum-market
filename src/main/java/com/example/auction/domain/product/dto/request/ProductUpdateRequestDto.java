package com.example.auction.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {

	@NotBlank(message = "상품명은 필수값입니다.")
	@Length(max = 20, message = "상품명은 20자 이내로 작성해주세요. ")
	private String name;

	@NotBlank(message = "상품 설명은 필수값입니다.")
	@Length(max = 200, message = "상품 설명은 200자 이내로 작성해주세요. ")
	private String description;

}
