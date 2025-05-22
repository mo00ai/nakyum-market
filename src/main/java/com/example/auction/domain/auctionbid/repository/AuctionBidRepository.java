package com.example.auction.domain.auctionbid.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.auctionbid.entity.AuctionBid;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {

}
