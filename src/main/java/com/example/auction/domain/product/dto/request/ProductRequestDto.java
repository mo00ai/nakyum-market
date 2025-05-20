package com.example.auction.domain.product.dto.request;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

	@NotBlank(message = "상품명은 필수값입니다.")
	@Length(max = 20, message = "상품명은 20자 이내로 작성해주세요. ")
	private String name;

	@NotBlank(message = "상품 설명은 필수값입니다.")
	@Length(max = 200, message = "상품 설명은 200자 이내로 작성해주세요. ")
	private String description;

	@NotNull(message = "시작가는 필수입니다.")
	private Long startPrice;

	@NotNull(message = "단위가는 필수입니다.")
	private Long unitPrice;

	@NotNull(message = "경매 시작일은 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate createdAt;

	@NotNull(message = "경매 종료일은 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endedAt;

}
