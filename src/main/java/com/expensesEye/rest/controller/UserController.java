package com.expensesEye.rest.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensesEye.config.SecurityConfig;
import com.expensesEye.constants.PathConstants;
import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.model.ErrorMessages;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.security.permission.UserPermissionProvider;
import com.expensesEye.service.UserService;
import com.expensesEye.utils.ResponseFactoryUtils;

/**
 * This class is a Rest Controller for handling the user actions, and handling
 * the user end-points. Securing this controller is handled in the
 * {@link SecurityConfig} file and in {@link UserPermissionProvider} class
 */
@RestController
@RequestMapping(value = PathConstants.USERS_PATH)
public class UserController {

	@Autowired
	private UserService<Long> userService;

	private EmailValidator emailValidator = EmailValidator.getInstance();

	@RequestMapping(value = PathConstants.CURRENT_USER, method = RequestMethod.GET)
	public User user(Principal user) {
		try {
			CurrentUserDetails currentUser = (CurrentUserDetails) ((Authentication) user).getPrincipal();
			return currentUser.getUser();
		} catch (Exception ex) {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user) {
		boolean inserted = userService.createUser(user);

		user = inserted ? user : null;
		return ResponseFactoryUtils.createResponse(user, HttpStatus.CREATED, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_CREATE_USER);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@RequestBody User user) {
		boolean updated = userService.updateUser(user);

		user = updated ? user : null;
		return ResponseFactoryUtils.createResponse(user, HttpStatus.OK, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_UPDATE_USER);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasPermission(#userId,'checkUserById') or hasAnyRole('ADMIN','USER_MANAGER')")
	public ResponseEntity<?> deleteUser(@RequestParam("id") Long userId) {

		return ResponseFactoryUtils.createResponse(userService.deleteUser(userId), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, String.format(ErrorMessages.INVALID_USER_ID, userId));
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext != null && securityContext.getAuthentication() != null
				&& securityContext.getAuthentication().getPrincipal() instanceof CurrentUserDetails) {
			// Don't include current authorized user info
			CurrentUserDetails currentUser = (CurrentUserDetails) securityContext.getAuthentication().getPrincipal();
			if (currentUser.getUser() != null) {
				boolean isAdmin = currentUser.getUser().getRoles() != null
						? currentUser.getUser().getRoles().contains(UserRole.ADMIN)
						: false;
				List<UserRole> notIncludedRoles = isAdmin ? null : Arrays.asList(UserRole.ADMIN);

				return ResponseFactoryUtils.createResponse(
						userService.getUsersNotIncluding(Arrays.asList(currentUser.getUser().getId()),
								notIncludedRoles),
						HttpStatus.OK, HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_GET_ALL_USERS);
			}
		}

		return ResponseFactoryUtils.createResponse(userService.getAllUsers(), HttpStatus.OK, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_GET_ALL_USERS);
	}

	@RequestMapping(value = PathConstants.ID_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#userId,'checkUserById') or hasAnyRole('ADMIN','USER_MANAGER')")
	public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {

		return ResponseFactoryUtils.createResponse(userService.getUser(userId), HttpStatus.OK, HttpStatus.BAD_REQUEST,
				String.format(ErrorMessages.INVALID_USER_ID, userId));
	}

	@RequestMapping(value = PathConstants.EMAIL_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#email,'checkUserByEmail') or hasAnyRole('ADMIN','USER_MANAGER')")
	public ResponseEntity<?> getUserByEmail(@PathVariable(name = "email") String email) {

		// Validate email first before calling the user service
		if (!emailValidator.isValid(email)) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST,
					String.format(ErrorMessages.INVALID_PARAM_EMAIL, email));
		}
		return ResponseFactoryUtils.createResponse(userService.getUserByEmail(email), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, String.format(ErrorMessages.INVALID_USER_EMAIL, email));
	}

	@Deprecated // Not used yet!
	@RequestMapping(value = PathConstants.NOT_ANY_PATH, method = RequestMethod.GET)
	public ResponseEntity<?> getUserNotInclude(@RequestParam(name = "ids", required = false) String ids,
			@RequestParam(name = "roles", required = false) String roles) {

		List<Long> idList = ids != null
				? Arrays.asList(ids.split(",")).stream().map(s -> Long.valueOf(s)).collect(Collectors.toList())
				: null;
		List<UserRole> roleList = roles != null
				? Arrays.asList(roles.split(",")).stream().map(s -> UserRole.valueOf(s)).collect(Collectors.toList())
				: null;

		return ResponseFactoryUtils.createResponse(userService.getUsersNotIncluding(idList, roleList), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_GET_ALL_USERS);
	}
}
