package com.example.auction.domain.user.auth.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.user.auth.exception.AuthErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		// 로그인 상태가 아님 (세션에 인증 정보 없음)
		if (authentication == null) {
			// 직접 응답 작성
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");

			CommonResponse<Void> commonErrorResponse = CommonResponse.error(AuthErrorCode.NOT_AUTHENTICATED);
			try {
				response.getWriter().write(objectMapper.writeValueAsString(commonErrorResponse));
				response.getWriter().flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return;
		}
		// 정상 로그아웃 처리
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
