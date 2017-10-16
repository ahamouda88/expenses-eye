package com.expensesEye.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.expensesEye.persist.entity.User;
import com.expensesEye.service.UserService;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	@Autowired
	private UserService<Long> userService;

	@Value("${expenseseye.login.attempts.limit}")
	private int loginLimit;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		String email = (String) event.getAuthentication().getPrincipal();
		User user = userService.getUserByEmail(email, true);

		if (user != null) {
			user.setNumOfAttempts(user.getNumOfAttempts() + 1);
			if (user.getNumOfAttempts() >= loginLimit) {
				user.setLocked(true);
			}
			userService.updateUser(user);
		}
	}

}
