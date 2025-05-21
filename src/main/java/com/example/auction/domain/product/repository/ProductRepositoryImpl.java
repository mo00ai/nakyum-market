package com.example.auction.domain.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.auction.domain.image.entity.QImage;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import com.example.auction.domain.product.dto.response.QProductResponseDto;
import com.example.auction.domain.product.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QProduct product = QProduct.product;
	QImage image = QImage.image;

	@Override
	public Page<ProductResponseDto> findProducts(String keyword, Pageable pageable, String IMAGE_DIR) {

		List<ProductResponseDto> productList = queryFactory
			.select(new QProductResponseDto(
				product.id,
				Expressions.stringTemplate(
					"concat({0}, {1})", IMAGE_DIR, image.uploadFileName
				),
				product.name,
				product.description,
				product.startPrice,
				product.unitPrice,
				product.finalPrice,
				Expressions.dateTemplate(java.time.LocalDate.class, "DATE_FORMAT({0}, {1})", product.createAt, "%Y-%m-%d"),
				product.endedAt,
				product.count
			))
			.from(product)
			.join(product.image, image)
			.where(
				nameLike(keyword)
			)
			.orderBy(product.createAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(product.count())
			.from(product)
			.where(
				nameLike(keyword)
			)
			.fetchOne();

		if (total == null) {
			total = 0L;
		}

		return new PageImpl<>(productList, pageable, total);
	}

	private BooleanExpression nameLike(String name) {
		return name != null ? product.name.like("%" + name + "%") : null;
	}

}