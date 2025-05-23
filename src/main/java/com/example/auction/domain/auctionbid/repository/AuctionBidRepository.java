package com.example.auction.domain.auctionbid.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.auctionbid.entity.AuctionBid;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {

    public Optional<AuctionBid> findByProductIdAndBidPrice(Long productId,Long finalPrice);
}
