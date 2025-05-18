package com.example.auction.domain.product.entity;

import com.example.auction.common.entity.BaseEntity;
import com.example.auction.domain.image.entity.Image;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate startedAt;

    @Column(nullable = false)
    private LocalDate endedAt;

    private int count;

    private LocalDate deletedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    public static Product of(String name, String description, Long startPrice, Long unitPrice, Long finalPrice, LocalDate startedAt, LocalDate endedAt) {
        return Product.builder()
                .name(name)
                .description(description)
                .startPrice(startPrice)
                .unitPrice(unitPrice)
                .finalPrice(finalPrice)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }
}
