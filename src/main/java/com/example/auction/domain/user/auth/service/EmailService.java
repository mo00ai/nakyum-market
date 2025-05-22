package com.example.auction.domain.user.auth.service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.user.auth.exception.AuthErrorCode;
import com.example.auction.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final RedisService redisService;
	private final UserRepository userRepository;

	private static final int MAX_ATTEMPTS = 3;

	//인증번호 전송
	public void sendCode(String email) {
		String redisKey = "email:code:" + email;

		// 사전 등록된 이메일
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(AuthErrorCode.ALREAD_REGISTERD);
		}

		// TTL 조회
		Long ttl = redisService.getExpire(redisKey);
		if (ttl != null && ttl > 0) {
			long minutes = ttl / 60;
			long seconds = ttl % 60;
			throw new CustomException(AuthErrorCode.AUTH_CODE_ALREADY_SENT,
				String.format("이미 인증번호가 전송되었습니다. %d분 %d초 후 다시 요청해주세요.", minutes, seconds));
		}

		String code = generateCode();
		redisService.setKeyValue(redisKey, code, Duration.ofMinutes(5));    //인증번호 생성 후 redis에 저장(5분)

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(email);
			helper.setSubject("나겸상가 회원가입을 위한 인증번호입니다.");
			helper.setText("인증번호 : " + code, false);
			helper.setFrom(new InternetAddress("msmskk379@gmail.com", "나겸상가"));

			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException ex) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

	}

	public boolean verifyCode(String email, String inputCode) {

		String codeKey = "email:code:" + email;
		String failKey = "email:fail:" + email;

		Object saved = redisService.getKeyValue(codeKey);
		if (saved == null)
			return false;

		// 실패 횟수 조회
		Long failCount = redisService.getKeyLongValue(failKey);
		long safeFailCount = (failCount != null) ? failCount : 0L;

		// 실패 횟수 초과 처리
		if (safeFailCount >= MAX_ATTEMPTS) {
			Long ttl = redisService.getExpire(failKey);
			long minutes = ttl != null && ttl > 0 ? ttl / 60 : 0;
			long seconds = ttl != null && ttl > 0 ? ttl % 60 : 0;

			throw new CustomException(AuthErrorCode.AUTH_CODE_ATTEMPT_EXCEEDED,
				String.format("인증번호를 여러 번 틀렸습니다. %d분 %d초 후 다시 요청해주세요.", minutes, seconds));
		}

		// 인증번호 일치
		if (saved.equals(inputCode)) {
			redisService.deleteRedisTemplateKeyValue(codeKey);
			redisService.deleteRedisTemplateKeyValue(failKey);
			return true;
		}

		// 인증번호 불일치하면, 실패 카운트 증가
		Long fail = redisService.incrementValue(failKey);
		if (fail != null && fail == 1) {
			// 실패 TTL = 인증번호 TTL
			Long codeTtl = redisService.getExpire(codeKey);
			if (codeTtl != null && codeTtl > 0) {
				redisService.setKeyValue(failKey, fail, Duration.ofSeconds(codeTtl));
			}
		}
		return false; //아직 3회 미만 틀렸을때
	}

	private String generateCode() {
		return String.valueOf((int)(Math.random() * 90000) + 100000);
	}

}
