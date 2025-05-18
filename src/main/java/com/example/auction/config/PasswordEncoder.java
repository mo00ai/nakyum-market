package com.example.auction.config;

import org.springframework.stereotype.Component;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Component
public class PasswordEncoder {

	/**
	 * 비밀번호를 Bcrypt로 인코딩합니다.
	 *
	 * @param rawPassword 원문 비밀번호
	 * @return 암호화된 문자열
	 */
	public String encode(String rawPassword) {
		return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
	}

	/**
	 * 원문과 인코딩된 비밀번호가 일치하는지 확인합니다.
	 *
	 * @param rawPassword 원문 비밀번호
	 * @param encodedPassword 암호화된 비밀번호
	 * @return 일치 여부
	 */
	public boolean matches(String rawPassword, String encodedPassword) {
		return BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword).verified;
	}
}