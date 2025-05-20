package com.example.auction.domain.product.entity;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.auction.common.entity.BaseEntity;
import com.example.auction.domain.image.entity.Image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "product")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Long startPrice;

	@Column(nullable = false)
	private Long unitPrice;

	private Long finalPrice;

	@Column(nullable = false)
	private LocalDate endedAt;

	private int count;

	private LocalDate deletedAt;

	@OnDelete(action = OnDeleteAction.CASCADE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id", nullable = false)
	private Image image;

	public static Product of(String name, String description, Long startPrice, Long unitPrice, LocalDate endedAt) {
		return Product.builder()
			.name(name)
			.description(description)
			.startPrice(startPrice)
			.unitPrice(unitPrice)
			.endedAt(endedAt)
			.count(0)
			.build();
	}

	public void addCount() {
		this.count++;
	}

	public void updateProduct(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public void deleteProduct() {
		this.deletedAt = LocalDate.now();
	}

	public void updateFinalPrice(Long finalPrice) {
		this.finalPrice = finalPrice;
	}

}
