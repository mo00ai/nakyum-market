package com.example.auction.domain.test.repository;

import com.example.auction.domain.auctionbid.entity.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAuctionBidRepository extends JpaRepository<AuctionBid, Long> {

}
