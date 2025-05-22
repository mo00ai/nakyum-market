package com.example.auction.domain.user.auth.security;

import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.auction.common.exception.CustomException;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

		return new CustomUserDetails(user);
	}

}
