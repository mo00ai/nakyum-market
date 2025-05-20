package com.example.auction.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
