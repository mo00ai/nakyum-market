package com.example.auction.domain.user.auth.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.service.ImageService;
import com.example.auction.domain.user.auth.userInfo.OAuth2UserInfo;
import com.example.auction.domain.user.auth.userInfo.OAuth2UserInfoFactory;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;

/**
 * 스프링 시큐리티에서 사용자 처리함
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final ImageService imageService;

	@Value("${app.default-image-id}")
	private Long defaultImageId;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		Image defaultImage = imageService.getImage(defaultImageId);

		String provider = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

		/**
		 * 이메일로 정보찾고 있으면 로그인
		 * 없으면 회원가입
		 */
		User user = userRepository.findByEmail(userInfo.getEmail())
			.orElseGet(() -> userRepository.save(userInfo.toEntity(defaultImage)));

		return new CustomOAuth2User(user, oAuth2User.getAttributes(), "email");
	}

}
