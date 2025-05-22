package com.example.auction.config.security;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.auction.common.dto.FilterErrorDto;
import com.example.auction.common.exception.BaseCode;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.domain.user.auth.handler.CustomLogoutHandler;
import com.example.auction.domain.user.auth.handler.CustomLogoutSuccessHandler;
import com.example.auction.domain.user.auth.handler.OAuth2SuccessHandler;
import com.example.auction.domain.user.auth.security.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomLogoutHandler customLogoutHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.logout(logout -> logout
				.logoutUrl("/auth/logout")
				.addLogoutHandler(customLogoutHandler)
				.logoutSuccessHandler(customLogoutSuccessHandler)
			)
			// CSRF 보호 비활성화 (REST API이므로)
			.csrf(csrf -> csrf.disable())
			// H2 Console 사용을 위해 frame 옵션 비활성화

			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			// 인증/인가 설정
			.authorizeHttpRequests(authorize -> authorize
				// 인증이 필요없는 공개 API
				.requestMatchers("/", "/auth/**", "/login/**", "/oauth2/**").permitAll()
				// 특정 권한이 필요한 API
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 나머지 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)

			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService))    //소셜 로그인 유저 정보처리
				.successHandler(oAuth2SuccessHandler))        // 로그인 성공 시 실행

			// 폼 로그인 비활성화 (REST API 이므로)
			.formLogin(formLogin -> formLogin.disable())

			// HTTP Basic 인증 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())

			// 세션 관리 설정 (세션 사용으로 필요시 생성)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			)

			// 예외 처리 설정
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					setErrorResponse(request, response, ErrorCode.INVALID_SIGNATURE);
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					setErrorResponse(request, response, ErrorCode.UNAUTHOIZED);
				})
			);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private void setErrorResponse(HttpServletRequest request, HttpServletResponse response,
		BaseCode baseCode) throws IOException {
		response.setStatus(baseCode.getHttpStatus().value());
		response.setContentType("application/json;charset=UTF-8");

		FilterErrorDto error = new FilterErrorDto(
			baseCode.getHttpStatus().value(),
			baseCode.getMessage(),
			LocalDateTime.now(),
			request.getRequestURI()
		);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		String json = objectMapper.writeValueAsString(error);
		response.getWriter().write(json);
	}

}


