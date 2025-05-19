package com.example.auction.domain.user.auth.userInfo;

import java.util.Map;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.entity.UserRole;

/**
 * 카카오 OAuth2 로그인으로부터 받은 사용자 정보를 파싱하는 클래스.
 */
public class KakaoUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		return (String)kakaoAccount.get("email");
	}

	/**
	 *
	 * @return 카카오는 실명제가 아니라 nickname 을 제공함(프로필 이름)
	 */
	@Override
	public String getName() {
		Map<String, Object> profile = (Map<String, Object>)((Map<String, Object>)attributes.get("kakao_account")).get(
			"profile");
		return (String)profile.get("nickname");
	}

	@Override
	public User toEntity(Image defaultImage) {
		return User.of(
			getEmail(),
			null,
			getName(),
			UserRole.ROLE_USER,
			null,
			defaultImage,
			"KAKAO"
		);
	}

}
