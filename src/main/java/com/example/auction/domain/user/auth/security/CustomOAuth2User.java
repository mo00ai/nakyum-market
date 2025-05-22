package com.example.auction.domain.user.auth.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.auction.domain.user.entity.User;

/**
 * 시큐리티 인증 후(소셜로그인 성공 후)에 사용자 정보 저장용
 */
@Getter
public class CustomOAuth2User implements OAuth2User, AuthUserWrapper {
	private final User user;
	private final Map<String, Object> attributes;
	private final String nameAttributeKey; //식별키 이메일이나 아이디

	public CustomOAuth2User(User user, Map<String, Object> attributes, String nameAttributeKey) {
		this.user = user;
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * 사용자의 권한 반환
	 *
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getUserRole().name()));
	}

	/**
	 * OAuth2에서 식별자로 사용하는 값 (ex: email 또는 provider id)
	 */
	@Override
	public String getName() {
		return String.valueOf(attributes.get(nameAttributeKey));
	}

}
