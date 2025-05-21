package com.example.auction.domain.user.auth.security;

import java.util.Collection;
import java.util.Collections;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.auction.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, AuthUserWrapper {

	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(() -> "ROLE_" + user.getUserRole().name());
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
