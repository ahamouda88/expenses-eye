package com.expensesEye.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import com.expensesEye.persist.entity.User;

/**
 * A Model class that represents that data of the current user, and it
 * implements Spring's {@link UserDetails}
 */
public class CurrentUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	private final static String ROLE_PREFIX = "ROLE_";
	private User user;
	private List<GrantedAuthority> authorities;

	public CurrentUserDetails(User user) {
		this.user = user;
		this.authorities = new ArrayList<>();

		if (user != null && !CollectionUtils.isEmpty(user.getRoles())) {
			user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
		}
	}

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user == null ? null : user.getPassword();
	}

	@Override
	public String getUsername() {
		return user == null ? null : user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return user == null ? false : !user.isLocked();
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
