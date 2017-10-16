package com.expensesEye.security.permission;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.rest.controller.UserController;

/**
 * This User permission provider secures the actions being performed on the
 * {@link UserController}
 */
@Component
public class UserPermissionProvider extends PermissionProvider {

	@Override
	public boolean isAuthorized(Authentication authentication, Object permission, Object... objectsToTarget) {
		if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUserDetails)
				|| !(permission instanceof String))
			return false;

		if (notAValidAccount(authentication))
			return false;

		// Return false since we are expecting an expense object or an expense id
		if (objectsToTarget == null || objectsToTarget.length == 0)
			return false;

		switch (String.valueOf(permission)) {
		case "checkUserById":
			// Check if the current user id is equal to the given user id
			return (objectsToTarget[0] instanceof Long) ? isSameUser(authentication, (long) objectsToTarget[0]) : false;
		case "checkUserByEmail":
			return (objectsToTarget[0] instanceof String) ? isSameUser(authentication, (String) objectsToTarget[0])
					: false;
		default:
			return false;
		}
	}

	private boolean isSameUser(Authentication authentication, String email) {
		CurrentUserDetails currentUser = (CurrentUserDetails) authentication.getPrincipal();
		return email.equals(currentUser.getUsername());
	}

	@Override
	public Set<Object> getSupportedPermissions() {
		Set<Object> permissions = new HashSet<>();
		permissions.add("checkUserById");
		permissions.add("checkUserByEmail");
		return permissions;
	}

}
