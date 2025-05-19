package com.example.auction.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.auction.domain.user.auth.service.CustomOAuth2UserService;
import com.example.auction.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
		CustomOAuth2UserService customOAuth2UserService,
		OAuth2SuccessHandler oAuth2SuccessHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customOAuth2UserService = customOAuth2UserService;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CSRF 보호 비활성화 (REST API이므로)
			.csrf(csrf -> csrf.disable())
			// H2 Console 사용을 위해 frame 옵션 비활성화

			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			// 인증/인가 설정
			.authorizeHttpRequests(authorize -> authorize
				// 인증이 필요없는 공개 API
				.requestMatchers("/auth/**").permitAll()
				// 특정 권한이 필요한 API
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// 나머지 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)

			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService))    //소셜 로그인 유저 정보처리
				.successHandler(oAuth2SuccessHandler))        // 로그인 성공 시 jwt발급, 리다이렉트

			// 폼 로그인 비활성화 (REST API 이므로)
			.formLogin(formLogin -> formLogin.disable())

			// HTTP Basic 인증 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())

			// 세션 관리 설정 (JWT를 사용하므로 상태 비저장)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 예외 처리 설정
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					response.sendError(401, "Unauthorized: " + authException.getMessage());
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.sendError(403, "Access Denied: " + accessDeniedException.getMessage());
				})
			);
		// JWT 필터 등록 - UsernamePasswordAuthenticationFilter 이전에 실행;
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
