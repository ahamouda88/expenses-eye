package com.expensesEye.service;

import com.expensesEye.model.exception.TokenExpiredException;
import com.expensesEye.model.exception.UserRegistrationException;
import com.expensesEye.model.exception.UserVerificationException;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;

/**
 * An interface that defines the service that is responsible for registering a
 * user
 */
public interface RegistrationService {

	/**
	 * This method registers a user given a {@link User} object. By adding the user
	 * to the database, and send an email verification for the user to verify
	 * 
	 * @param user
	 *            a {@link User} object
	 * @return the VerificationToken if registered successfully, <b>null</b>
	 *         otherwise
	 * @throws UserRegistrationException
	 *             if failed to register the given user
	 */
	public VerificationToken registerUser(User user) throws UserRegistrationException;

	/**
	 * This method verifies a user given a the user's email, and token
	 * 
	 * @param email
	 *            the user's email
	 * @param token
	 *            the user's token
	 * @return the verified user if verified successfully, <b>null</b> otherwise
	 * @throws UserVerificationException
	 *             if failed to verify a user
	 * @throws TokenExpiredException
	 *             if token has expired
	 */
	public User verifyUser(String email, String token) throws UserVerificationException, TokenExpiredException;

	/**
	 * This method generates a new {@link VerificationToken}, given the user's old
	 * token
	 * 
	 * @param token
	 *            the user's old token
	 * @return the VerificationToken if new token is generated successfully,
	 *         <b>null</b> otherwise
	 */
	public VerificationToken generateNewVerificationToken(String token) throws UserVerificationException;

	/**
	 * This method sends a registration request to the given email address
	 * 
	 * @param fromEmail
	 *            the user's email address whom sending the request
	 * @param toEmail
	 *            the user's email address to receive the registration request
	 */
	public void sendRegistrationRequest(String fromEmail, String toEmail);

	/**
	 * This method sends a verification email to the given user, having the given
	 * token
	 * 
	 * @param email
	 *            the user's email
	 * @param token
	 *            the user's token
	 * @return true if email was send successfully, false otherwise
	 */
	public boolean sendVerificationEmail(String email, String token);

	/**
	 * This method either creates a new {@link User} or return an existing user.
	 * This is mainly used when logging in with any social media provider
	 * 
	 * @param user
	 *            the user to be added to retrieved
	 * @return the new created user or an existing user
	 */
	public User providerLogin(User user);

}
