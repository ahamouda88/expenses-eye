package com.expensesEye.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import com.expensesEye.persist.entity.User;
import com.expensesEye.service.UserService;

public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	private UserService<Long> userService;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String email = (String) event.getAuthentication().getPrincipal();
		User user = userService.getUserByEmail(email, true);
		
		if (!user.isLocked()) {
			user.setNumOfAttempts(0);
			userService.updateUser(user);
		}
	}

}
