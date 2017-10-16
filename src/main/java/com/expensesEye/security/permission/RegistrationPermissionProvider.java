package com.expensesEye.security.permission;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.rest.controller.RegistrationController;
import com.expensesEye.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This Registration permission provider secures the actions being performed on
 * the {@link RegistrationController}
 */
@Component
public class RegistrationPermissionProvider extends PermissionProvider {

	@Autowired
	private TokenService<Long> tokenService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean isAuthorized(Authentication authentication, Object permission, Object... objectsToTarget) {
		if (authentication == null || !(permission instanceof String))
			return false;

		if (notAValidAccount(authentication))
			return false;

		switch (String.valueOf(permission)) {
		case "register":
			// If adding a new user, then check if user is an anonymous, or has ADMIN or
			// USER_MANAGER Role
			return authentication instanceof AnonymousAuthenticationToken;
		case "update":
			// If updating an existing user, then check if current user is the user being
			// updated or has ADMIN or USER_MANAGER Role
			return objectsToTarget == null || objectsToTarget.length == 0 ? false
					: hasUpdateAuthority(authentication, objectsToTarget[0]);
		case "verify":
			// If verifying a user, then get the token and see if the user with the given
			// email is the same user who has the token or has ADMIN or USER_MANAGER Role
			return objectsToTarget == null || objectsToTarget.length < 2 ? false
					: hasVerifyAuthority(objectsToTarget[0], objectsToTarget[1]);
		default:
			return false;
		}
	}

	private boolean hasUpdateAuthority(Authentication authentication, Object userObj) {
		if (!(authentication.getPrincipal() instanceof CurrentUserDetails))
			return false;

		User user = parseUser(userObj);
		if (user == null)
			return false;

		String currentUserEmail = ((CurrentUserDetails) authentication.getPrincipal()).getUsername();
		return currentUserEmail == null ? false : currentUserEmail.equals(user.getEmail());
	}

	private boolean hasVerifyAuthority(Object emailObj, Object tokenObj) {
		if (!(emailObj instanceof String) || !(tokenObj instanceof String)) {
			return false;
		}

		VerificationToken verificationToken = tokenService.getToken(String.valueOf(tokenObj));
		if (verificationToken == null || verificationToken.getUser() == null)
			return false;

		return String.valueOf(emailObj).equals(verificationToken.getUser().getEmail());
	}

	private User parseUser(Object userObj) {
		if (!(userObj instanceof String))
			return null;

		try {
			return objectMapper.readValue(String.valueOf(userObj), User.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Set<Object> getSupportedPermissions() {
		Set<Object> permissions = new HashSet<>();
		permissions.add("register");
		permissions.add("update");
		permissions.add("verify");
		return permissions;
	}
}
