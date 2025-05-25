package com.example.auction.domain.user.auth.handler;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.auction.common.exception.SuccessCode;
import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.dips.service.DipsService;
import com.example.auction.domain.user.auth.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private final DipsService dipsService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		if (response.isCommitted()) {
			return;
		}
		CustomUserDetails userDetail = (CustomUserDetails)authentication.getPrincipal();
		dipsService.removeRedis(userDetail);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");

		// 공통 응답 포맷 생성
		CommonResponse<Void> logoutResponse = CommonResponse.success(SuccessCode.LOGOUT_SUCCESS);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		response.getWriter().write(objectMapper.writeValueAsString(logoutResponse));
	}
}
