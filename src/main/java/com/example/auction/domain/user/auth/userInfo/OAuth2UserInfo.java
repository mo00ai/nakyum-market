package com.example.auction.domain.user.auth.userInfo;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.user.entity.User;

/**
 * OAuth2 로그인 사용자 정보를 표준화된 방식으로
 * 가져오기 위한 인터페이스
 */
public interface OAuth2UserInfo {
	String getEmail();
	String getName();
	User toEntity(Image defaultImage);
}
