package com.example.auction.domain.topKeyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.topKeyword.entity.TopKeyword;

public interface TopKeywordRepository extends JpaRepository<TopKeyword, Long> {
}
