package com.example.auction.domain.user.auth.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		// 로그인 상태가 아님 (세션에 인증 정보 없음)
		if (authentication == null) {
			// 직접 응답 작성
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			try {
				response.getWriter().write("""
					{
					    "isError": true,
					    "status": 401,
					    "code": "A003",
					    "message": "로그인 상태가 아닙니다."
					}
					""");
				response.getWriter().flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return; // 아래 로직 실행 안 하도록 종료
		}

		// ✅ 정상 로그아웃 처리
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
