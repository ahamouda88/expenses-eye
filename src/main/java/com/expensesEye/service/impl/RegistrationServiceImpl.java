package com.expensesEye.service.impl;

import java.util.Calendar;
import java.util.UUID;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.exception.TokenExpiredException;
import com.expensesEye.model.exception.UserRegistrationException;
import com.expensesEye.model.exception.UserVerificationException;
import com.expensesEye.model.request.UserUpdateRequest;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.service.EmailService;
import com.expensesEye.service.RegistrationService;
import com.expensesEye.service.TokenService;
import com.expensesEye.service.UserService;

/**
 * Implementation of {@link RegistrationService}
 */
@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private UserService<Long> userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TokenService<Long> tokenService;

	private EmailValidator emailValidator = EmailValidator.getInstance();

	@Value("${expenseseye.register.mainemail}")
	private String mainEmail;

	@Override
	public VerificationToken registerUser(User user) throws UserRegistrationException {
		boolean inserted = userService.createUser(user);

		if (!inserted)
			return null;

		// Create verification token
		VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), user);
		boolean tokenCheck = tokenService.createToken(verificationToken);

		if (!tokenCheck) {
			throw new UserRegistrationException(ErrorMessages.FAILED_CREATE_VERIFICATION_TOKEN);
		}

		// Only send verification email for users that are not marked as verified!
		if (!user.isVerified()) {
			boolean emailCheck = sendVerificationEmail(user.getEmail(), verificationToken.getToken());
			if (!emailCheck) {
				throw new UserRegistrationException(ErrorMessages.FAILED_SEND_EMAIL);
			}
		}
		return verificationToken;
	}

	@Override
	public User verifyUser(String email, String token) throws UserVerificationException, TokenExpiredException {
		// Validate email first before calling the user service
		if (!emailValidator.isValid(email)) {
			throw new UserVerificationException(String.format(ErrorMessages.INVALID_PARAM_EMAIL, email));
		}

		// Verify if given parameters are correct
		User user = userService.getUserByEmail(email);
		if (user != null && user.isVerified())
			return user;

		VerificationToken verificationToken = tokenService.getToken(token);
		if (verificationToken == null || verificationToken.getUser() == null || user == null
				|| user.getId() != verificationToken.getUser().getId()) {
			throw new UserVerificationException(ErrorMessages.FAILED_VERIFY_USER);
		}

		// Check if token is not expired yet
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			throw new TokenExpiredException(ErrorMessages.FAILED_EXPIRED_VERIFICATION_TOKEN);
		}

		return userService.updateUser(email, UserUpdateRequest.builder().verified(true).build());
	}

	@Override
	public VerificationToken generateNewVerificationToken(String token) throws UserVerificationException {
		VerificationToken verificationToken = tokenService.getToken(token);
		if (verificationToken == null) {
			throw new UserVerificationException(ErrorMessages.INVALID_VERIFICATION_TOKEN);
		}
		verificationToken.updateToken(UUID.randomUUID().toString());

		boolean updateCheck = tokenService.updateToken(verificationToken);
		if (!updateCheck || verificationToken.getUser() == null)
			return null;

		boolean emailCheck = sendVerificationEmail(verificationToken.getUser().getEmail(),
				verificationToken.getToken());
		return emailCheck ? verificationToken : null;
	}

	@Override
	public User providerLogin(User user) {
		if (user == null || user.getEmail() == null)
			return null;

		User existingUser = userService.getUserByEmail(user.getEmail());

		if (existingUser == null) {
			// Create user if doesn't exist
			boolean userCheck = userService.createUser(user);
			return userCheck ? userService.getUserByEmail(user.getEmail()) : null;
		}
		return existingUser;
	}

	@Override
	public void sendRegistrationRequest(String fromEmail, String toEmail) {
		if (!emailValidator.isValid(fromEmail)) {
			throw new UserRegistrationException(String.format(ErrorMessages.INVALID_PARAM_EMAIL, fromEmail));
		}
		if (!emailValidator.isValid(toEmail)) {
			throw new UserRegistrationException(String.format(ErrorMessages.INVALID_PARAM_EMAIL, toEmail));
		}

		String message = "User " + fromEmail
				+ " is inviting you to register for the Expenses Eye application. Please <a href=\"http://localhost:8080\">click here</a> to start registration";
		boolean emailCheck = emailService.sendEmail(fromEmail, toEmail, "Expenses Eye Registration Request", message,
				"text/html");
		if (!emailCheck) {
			throw new UserRegistrationException(ErrorMessages.FAILED_SEND_EMAIL);
		}
	}

	@Override
	public boolean sendVerificationEmail(String email, String token) {
		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(token))
			return false;

		// Send verification email with the token data
		String verificationMessage = String.format(
				"Please complete your verifcation: <a href=\"http://localhost:8080/api/users/verify/%s?token=%s\">click here</a>",
				email, token);
		boolean emailCheck = emailService.sendEmail(mainEmail, email, "Expenses Eye Email Verification",
				verificationMessage, "text/html");
		if (!emailCheck) {
			tokenService.removeToken(token);
		}
		return emailCheck;
	}

}
