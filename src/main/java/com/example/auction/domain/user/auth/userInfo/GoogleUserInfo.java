package com.example.auction.domain.user.auth.userInfo;

import java.util.Map;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.entity.UserRole;

/**
 * 구글 OAuth2 로그인으로부터 받은 사용자 정보를 파싱하는 클래스.
 */
public class GoogleUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	public GoogleUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;

	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getName() {
		return (String)attributes.get("name");
	}

	@Override
	public User toEntity(Image defaultImage) {
		return User.of(
			getEmail(),
			null,
			getName(),
			UserRole.USER,
			null,
			defaultImage,
			"GOOGLE"
		);
	}

}
