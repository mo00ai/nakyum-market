package com.example.auction.domain.user.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.user.auth.dto.request.SigninRequest;
import com.example.auction.domain.user.auth.dto.request.SignupRequest;
import com.example.auction.domain.user.auth.dto.response.SigninResponse;
import com.example.auction.domain.user.auth.dto.response.SignupResponse;
import com.example.auction.domain.user.auth.exception.AuthErrorCode;
import com.example.auction.domain.user.auth.security.AuthUserWrapper;
import com.example.auction.domain.user.auth.security.CustomUserDetails;
import com.example.auction.domain.user.auth.service.AuthService;
import com.example.auction.domain.user.auth.service.EmailService;
import com.example.auction.domain.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final EmailService emailService;
	private final AuthService authService;

	@PostMapping("/send-code")
	public CommonResponse<Void> sendCode(@RequestParam String email) {
		emailService.sendCode(email);
		return CommonResponse.ok();
	}

	@PostMapping("/verify-code")
	public CommonResponse<Void> verifyCode(@RequestParam String email, @RequestParam String code,
		HttpServletRequest request) {

		if (!emailService.verifyCode(email, code)) {
			return CommonResponse.error(AuthErrorCode.INVALID_EMAIL_VERIFICATION);

		}

		request.getSession(true).setAttribute("verifiedEmail", email);

		return CommonResponse.ok();

	}

	@PostMapping("/signup")
	public CommonResponse<SignupResponse> signup(@RequestBody SignupRequest request, HttpServletRequest httpRequest) {

		String verifiedEmail = (String)httpRequest.getSession().getAttribute("verifiedEmail");

		if (verifiedEmail == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED);
		}

		User user = authService.signup(request, verifiedEmail);
		httpRequest.getSession().removeAttribute("verifiedEmail"); // 인증 완료 후 세션에서 인증 정보 제거
		return CommonResponse.ok(new SignupResponse(user.getNickname()));

	}

	@PostMapping("/signin")
	public CommonResponse<SigninResponse> signin(@RequestBody SigninRequest request, HttpServletRequest httpRequest) {

		//기존 로그인 확인하는 로직 - 로그아웃 상태여야만 로그인이 가능.
		HttpSession session = httpRequest.getSession(false);

		SecurityContext securityContext =
			(session != null) ? (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT") : null;

		if (securityContext != null) {
			Authentication existingAuth = securityContext.getAuthentication();
			if (existingAuth != null && existingAuth.isAuthenticated()) {
				throw new CustomException(AuthErrorCode.ALREADY_LOGGED_IN);
			}
		}

		User user = authService.signin(request);

		CustomUserDetails userDetails = new CustomUserDetails(user);

		// DB나 Redis에서 사용자 정보를 조회해서 인증 객체 생성 가능
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
		httpRequest.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		return CommonResponse.ok(new SigninResponse(user.getNickname()));
	}

	@PostMapping("/logout")
	public CommonResponse<Void> logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate(); //redis에서도 삭제됨
		}

		return CommonResponse.ok();
	}

	@GetMapping("/users/me")
	public CommonResponse<?> me(@AuthenticationPrincipal AuthUserWrapper wrapper) {
		if (wrapper == null) {
			return CommonResponse.error(AuthErrorCode.NOT_AUTHENTICATED);
		}
		return CommonResponse.ok("로그인된 유저 ID: " + wrapper.getUser().getId());
	}

}
