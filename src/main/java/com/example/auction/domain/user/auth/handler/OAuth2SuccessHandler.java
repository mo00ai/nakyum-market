package com.example.auction.domain.user.auth.handler;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.auction.domain.user.auth.security.CustomOAuth2User;

/**
 * 성공 후 프론트에 전달 (쿼리파라미터)
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication)
		throws IOException {
		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 세션에 SecurityContext 등록 (명시적으로!)
		SecurityContextHolder.getContext().setAuthentication(authentication);
		HttpSession session = request.getSession();
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		//토큰 전달 없이 리다이렉트만
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("소셜 로그인 성공 세션 저장 완료");
		response.getWriter().flush();
	}

}
