package com.example.auction.domain.auctionbid.entity;


import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "action_bid")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuctionBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bidPrice;

    @Column(nullable = false)
    private boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static AuctionBid of(Long bidPrice, boolean isSuccess, Product product, User user) {
        return AuctionBid.builder()
                .bidPrice(bidPrice)
                .isSuccess(isSuccess)
                .product(product)
                .user(user)
                .build();
    }
}
