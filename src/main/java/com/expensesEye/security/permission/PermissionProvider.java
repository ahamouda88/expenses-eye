package com.expensesEye.security.permission;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.persist.entity.User;
import com.expensesEye.service.UserService;

/**
 * An interface that contains the main method for providing a permission to a
 * user on an object
 */
@Component
public abstract class PermissionProvider {

	@Autowired
	protected UserService<Long> userService;

	/**
	 * This method checks if the given {@link Authentication} user is authorized to
	 * access the given target objects
	 * 
	 * @param authentication
	 *            an {@link Authentication} object
	 * @param permission
	 *            the permission given
	 * @param objectsToTarget
	 *            objects to check access on
	 * @return true if user is authorized to access the given object(s), false
	 *         otherwise
	 */
	protected abstract boolean isAuthorized(Authentication authentication, Object permission,
			Object... objectsToTarget);

	/**
	 * This method returns a set of supported permissions for this provider
	 * 
	 * @return a set of permissions
	 */
	protected abstract Set<Object> getSupportedPermissions();

	/**
	 * This method checks if a given {@link Authentication} user has authority or
	 * not
	 * 
	 * @param authentication
	 *            an {@link Authentication} object
	 * @return true if user has authority, false otherwise
	 */
	protected boolean hasAuthority(Authentication authentication) {
		if (authentication == null || authentication.getAuthorities() == null)
			return false;

		return authentication.getAuthorities().stream().anyMatch(authority -> getAuthorities().contains(authority));
	}

	/**
	 * A set of all valid authorities, that always have permission
	 * 
	 * @return a set of {@link GrantedAuthority}
	 */
	protected Set<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		authorities.add(new SimpleGrantedAuthority("ROLE_USER_MANAGER"));
		return authorities;

	}

	/**
	 * This methods checks if the given user is same as current authorized user
	 * 
	 * @param authentication
	 *            authorized user
	 * @param userId
	 *            a user's id
	 * @return true if user is the same, false otherwise
	 */
	protected boolean isSameUser(Authentication authentication, long userId) {
		if (authentication == null || authentication.getPrincipal() == null
				|| !(authentication.getPrincipal() instanceof CurrentUserDetails))
			return false;
		CurrentUserDetails currentUser = (CurrentUserDetails) authentication.getPrincipal();
		return currentUser.getUser().getId() == userId;
	}

	/**
	 * This methods checks if the current user is locked or not verified
	 * 
	 * @param authentication
	 *            authorized user
	 * @return true if user is locked or not verified, false otherwise
	 */
	protected boolean notAValidAccount(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null
				|| !(authentication.getPrincipal() instanceof CurrentUserDetails))
			return false;

		CurrentUserDetails currentUser = (CurrentUserDetails) authentication.getPrincipal();
		if (currentUser.getUsername() == null)
			return false;

		User user = userService.getUserByEmail(currentUser.getUsername());
		return user != null && (user.isLocked() || !user.isVerified());
	}

}
