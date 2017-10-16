package com.expensesEye.rest.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.expensesEye.constants.PathConstants;
import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.exception.TokenExpiredException;
import com.expensesEye.model.exception.UserVerificationException;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.service.RegistrationService;
import com.expensesEye.service.TokenService;
import com.expensesEye.service.UserService;
import com.expensesEye.utils.ResponseFactoryUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This controller class contains the end-points responsible for managing the
 * user registration and verification process
 */
@RestController
@RequestMapping(value = PathConstants.USERS_PATH)
public class RegistrationController {

	@Autowired
	private TokenService<Long> tokenService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private UserService<Long> userService;

	@Value("${expenseseye.image.size.limit}")
	private long fileSizeLimit;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@RequestMapping(value = PathConstants.REGISTER_PATH, method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	@PreAuthorize("hasPermission(authentication,'register') or hasAnyRole('ADMIN','USER_MANAGER')")
	public ResponseEntity<?> registerNewUser(@RequestPart(value = "user") String userString,
			@RequestParam(value = "image", required = false) MultipartFile image) {

		if (image != null && image.getSize() > fileSizeLimit) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST,
					String.format(ErrorMessages.FILE_SIZE_EXCEEDED_LIMIT, fileSizeLimit));
		}

		User user = parseUser(userString, image);
		if (user != null) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (securityContext.getAuthentication() == null
					|| securityContext.getAuthentication().getPrincipal() == null
					|| securityContext.getAuthentication().getPrincipal().equals("anonymousUser")) {
				// Don't allow anonymous users to update following fields
				user.setVerified(false);
				user.setLocked(false);
				user.setNumOfAttempts(0);
				user.setRoles(Arrays.asList(UserRole.REGULAR_USER));
			}
		}

		VerificationToken verificationToken = registrationService.registerUser(user);
		return ResponseFactoryUtils.createResponse(verificationToken == null ? null : verificationToken,
				HttpStatus.CREATED, HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_CREATE_USER);
	}

	@RequestMapping(value = PathConstants.REGISTER_PATH, method = RequestMethod.PUT, consumes = {
			"multipart/form-data" })
	@PreAuthorize("hasPermission(#userString,'update') or hasAnyRole('ADMIN','USER_MANAGER')")
	public ResponseEntity<?> updateExistingUser(@RequestPart("user") String userString,
			@RequestPart(value = "image", required = false) MultipartFile image) {

		if (image != null && image.getSize() > fileSizeLimit) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST,
					String.format(ErrorMessages.FILE_SIZE_EXCEEDED_LIMIT, fileSizeLimit));
		}

		// Check if user is allowed to update sensitive fields
		User user = parseUser(userString, image);
		if (user != null && user.getId() != null) {
			User oldUser = userService.getUser(user.getId());
			if (oldUser != null) {
				SecurityContext securityContext = SecurityContextHolder.getContext();
				if (securityContext != null && securityContext.getAuthentication() != null
						|| securityContext.getAuthentication().getAuthorities() != null) {
					GrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");

					if (!securityContext.getAuthentication().getAuthorities().stream()
							.anyMatch(authority -> admin.equals(authority))) {
						// Don't allow same user to update following fields
						user.setVerified(oldUser.isVerified());
						user.setLocked(oldUser.isLocked());
						user.setNumOfAttempts(oldUser.getNumOfAttempts());
						user.setPassword(oldUser.getPassword());
						user.setEmail(oldUser.getEmail());
					}
				}
			}
		}

		boolean updated = userService.updateUser(user);
		return ResponseFactoryUtils.createResponse(updated ? user : null, HttpStatus.OK, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_UPDATE_USER);
	}

	@RequestMapping(value = PathConstants.EMAIL_VERIFY_PATH, method = RequestMethod.GET)
	public String verifyUser(HttpServletResponse httpResponse, @PathVariable(name = "email") String email,
			@RequestParam(name = "token") String token,
			@RequestParam(name = "newtoken", required = false) boolean newToken) {

		// Check if the user with the given email is the same user who has the token
		VerificationToken existingToken = tokenService.getToken(token);
		if (existingToken == null || existingToken.getUser() == null) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			return "User is not authorized to verify this account";
		}
		if (!existingToken.getUser().getEmail().equals(email)) {
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			return "User is not authorized to verify this account";
		}
		// Generating new token and send an email to the user
		if (newToken) {
			String successMessage = String
					.format("A new token was generated and an email was sent to the given email %s", email);
			String failMessage = String.format("Failed to generate a new token for the given email %s", email);
			try {
				VerificationToken verificationToken = registrationService.generateNewVerificationToken(token);
				return verificationToken == null ? failMessage : successMessage;
			} catch (UserVerificationException e) {
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				return failMessage;
			}
		}
		// Verify the user if not generating a new token
		StringBuilder sb = new StringBuilder();
		sb.append("User with email ").append(email);
		try {
			User user = registrationService.verifyUser(email, token);
			if (user != null) {
				sb.append(
						" is successfully verified! Please click the following link to login: <a href=\"http://localhost:8080/\">Click Here!</a>");
			} else {
				httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
				sb.append(" is failed to be verified!");
			}
		} catch (TokenExpiredException tokenEx) {
			sb.append(String.format(
					" is failed to be verified. Token is expired, to generate and send a new token please click here: <a href=\"http://localhost:8080/api/users/verify/%s?token=%s&newtoken=true\">Click Here!</a>",
					email, token));
		} catch (UserVerificationException userEx) {
			httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			sb.append(" is failed to be verified!");
		}
		return sb.toString();
	}

	@RequestMapping(value = PathConstants.SEND_REQUEST_REGISTER_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> sendRegisterRequest(@RequestParam(name = "from") String fromEmail,
			@RequestParam(name = "to") String toEmail) {
		try {
			registrationService.sendRegistrationRequest(fromEmail, toEmail);
			return ResponseFactoryUtils.createSuccessResponse("Email sent successfully", HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseFactoryUtils.createFailResponse(HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_SEND_EMAIL);
		}
	}

	private User parseUser(String userString, MultipartFile image) {
		if (StringUtils.isEmpty(userString))
			return null;

		try {
			User user = objectMapper.readValue(userString, User.class);
			if (user != null && image != null) {
				user.setImage(image.getBytes());
			}
			return user;
		} catch (Exception e) {
			return null;
		}
	}

}
