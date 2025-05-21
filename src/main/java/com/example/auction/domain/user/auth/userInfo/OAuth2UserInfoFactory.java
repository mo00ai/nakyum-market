package com.example.auction.domain.user.auth.userInfo;

import java.util.Map;

import com.example.auction.domain.user.auth.exception.AuthErrorCode;

/**
 *provider 이름(구글,카카오,네이버)에 따라 적절한 OAuth2UserInfo 구현체를 생성한다.
 */
public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
		return switch (provider.toLowerCase()) {
			case "google" -> new GoogleUserInfo(attributes);
			case "naver" -> new NaverUserInfo(attributes);
			default -> throw new IllegalArgumentException(AuthErrorCode.NON_PROVIDER.getMessage());
		};
	}

}
