package com.example.auction.domain.TopKeyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.TopKeyword.entity.TopKeyword;

public interface TopKeywordRepository extends JpaRepository<TopKeyword, Long> {
}
