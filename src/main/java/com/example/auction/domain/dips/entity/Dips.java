package com.example.auction.domain.dips.entity;

import com.example.auction.common.entity.BaseEntity;
import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "dips")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dips extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static Dips of(User user, Product product) {
        return Dips.builder()
                .user(user)
                .product(product)
                .build();
    }
}
