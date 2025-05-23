package com.example.auction.domain.user.auth.service;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.common.exception.CustomException;
import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.service.ImageService;
import com.example.auction.domain.user.auth.dto.request.SigninRequest;
import com.example.auction.domain.user.auth.dto.request.SignupRequest;
import com.example.auction.domain.user.auth.exception.AuthErrorCode;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.entity.UserRole;
import com.example.auction.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final LoginAttemptService loginAttemptService;
	private final ImageService imageService;

	@Value("${app.default-image-id}")
	private Long defaultImageId;

	public User signup(SignupRequest request, String verifiedEmail) {
		if (!request.getEmail().equals(verifiedEmail)) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED);
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomException(AuthErrorCode.ALREAD_REGISTERD);
		}

		Image defaultImage = imageService.getImage(defaultImageId);

		User user = User.of(
			request.getEmail(),
			passwordEncoder.encode(request.getPassword()),
			request.getUserName(),
			UserRole.valueOf(request.getUserRole()),
			null, // 주소 없음
			defaultImage,
			"local"    //소셜과 구분
		);

		return userRepository.save(user);
	}

	public User signin(SigninRequest request) {

		String email = request.getEmail();

		//로그인 차단여부확인
		if (loginAttemptService.isLocked(email)) {
			Duration remaining = loginAttemptService.getRemainingLockTime(email);
			long minutes = remaining.toMinutes();
			long seconds = remaining.minusMinutes(minutes).getSeconds();

			String msg = String.format("로그인 시도 횟수를 초과했습니다. %d분 %d초 후 다시 시도해주세요.", minutes, seconds);
			throw new CustomException(AuthErrorCode.LOGIN_LOCKED, msg);
		}

		//이메일 검증
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> {
				loginAttemptService.loginFailed(email);
				return new CustomException(AuthErrorCode.INVALID_LOGIN);
			});

		//비밀번호 검증
//		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//			loginAttemptService.loginFailed(email);
//			throw new CustomException(AuthErrorCode.INVALID_LOGIN);
//		}
		// provider가 "local"이 아닌 경우: 소셜 로그인 사용자
//		if (!"local".equals(user.getProvider())) {
//			throw new CustomException(AuthErrorCode.SOCIAL_ONLY);
//		}

		loginAttemptService.resetFailCount(email); // 로그인 성공 시 실패 기록 삭제

		return user;
	}
}
