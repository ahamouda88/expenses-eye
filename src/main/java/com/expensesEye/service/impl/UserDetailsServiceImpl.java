package com.expensesEye.service.impl;

import static org.springframework.util.StringUtils.isEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.model.ErrorMessages;
import com.expensesEye.persist.entity.User;
import com.expensesEye.service.UserService;

/**
 * Implementation of Spring's {@link UserDetailsService}, that represents the
 * current user's data
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	private UserService<Long> userService;

	@Override
	public UserDetails loadUserByUsername(String email) {
		if (isEmpty(email)) {
			logger.error("Email shouldn't be null or empty!");
			return null;
		}

		User user = userService.getUserByEmail(email, true);
		if (user == null) {
			logger.debug(String.format(ErrorMessages.INVALID_USER_EMAIL, email));
			return null;
		}
		return new CurrentUserDetails(user);
	}
}
