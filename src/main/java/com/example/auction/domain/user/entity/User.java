package com.example.auction.domain.user.entity;

import java.time.LocalDateTime;

import com.example.auction.common.entity.BaseEntity;
import com.example.auction.domain.image.entity.Image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(length = 255)
	private String password;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false,length = 20)
	private UserRole userRole;

	@Column(length = 50)
	private String address;

	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id", nullable = false)
	private Image image;

	@Builder
	public static User of(String email, String password, String nickname, UserRole userRole, String address,
		Image image) {

		return User.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.userRole(userRole)
			.address(address)
			.image(image)
			.build();

	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

}

