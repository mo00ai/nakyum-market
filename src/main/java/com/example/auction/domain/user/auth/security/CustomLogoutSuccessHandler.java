package com.example.auction.domain.user.auth.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.auction.common.exception.SuccessCode;
import com.example.auction.common.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		if (response.isCommitted()) {
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");

		CommonResponse<Void> body = CommonResponse.success(SuccessCode.LOGOUT_SUCCESS);

		String json = objectMapper.writeValueAsString(body);
		response.getWriter().write(json);
	}
}
