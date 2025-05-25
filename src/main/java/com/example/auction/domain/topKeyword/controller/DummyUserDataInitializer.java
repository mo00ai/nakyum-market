package com.example.auction.domain.topKeyword.controller;

import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.repository.ImageRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.entity.UserRole;
import com.example.auction.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// @Configuration
@RequiredArgsConstructor
public class DummyUserDataInitializer {

	private final UserRepository userRepository;
	private final ImageRepository imageRepository;
	private final PasswordEncoder passwordEncoder;

	// @Bean
	public CommandLineRunner initDummyUsers() {
		return args -> {
			Image defaultImage = imageRepository.findById(1L)
				.orElseThrow(() -> new IllegalArgumentException("기본 이미지가 존재하지 않습니다."));

			IntStream.rangeClosed(1, 1000).forEach(i -> {
				User user = User.of(
					"user" + i + "@example.com",
					passwordEncoder.encode("password"),  // "password"를 암호화
					"유저" + i,
					UserRole.USER,
					"서울시 강남구",
					defaultImage,
					"local"
				);

				userRepository.save(user);
			});
		};
	}
}
