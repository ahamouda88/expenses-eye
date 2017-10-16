package com.expensesEye.service;

import java.io.Serializable;
import java.util.List;

import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;

/**
 * An interface that contains the logic for doing operations on
 * {@link VerificationToken}
 * 
 * @param <T>
 *            the model Id's data type
 */
public interface TokenService<T extends Serializable> {

	/**
	 * This method creates a {@link VerificationToken} model, given the verification
	 * token model
	 * 
	 * @param verificationToken
	 *            the verification token that need to be created
	 * @return true if token is created successfully, or false otherwise
	 */
	public boolean createToken(VerificationToken verificationToken);

	/**
	 * This method updates a {@link VerificationToken} model, given the verification
	 * token model
	 * 
	 * @param verificationToken
	 *            the verification token that need to be created
	 * @return true if token is created successfully, or false otherwise
	 */
	public boolean updateToken(VerificationToken verificationToken);

	/**
	 * This method returns a {@link User} of the given token
	 * 
	 * @param token
	 *            the user's token
	 * @return a {@link User} object or null if user with the given token doesn't
	 *         exist
	 */
	public User getUser(String token);

	/**
	 * This method returns a {@link VerificationToken}, given the user's token
	 * 
	 * @param token
	 *            the user's token
	 * @return a {@link VerificationToken} object or null token doesn't exist
	 */
	public VerificationToken getToken(String token);

	/**
	 * This method returns a {@link VerificationToken}, given the user's id
	 * 
	 * @param userId
	 *            the user's id
	 * @return a {@link VerificationToken} object or null token doesn't exist
	 */
	public VerificationToken getToken(Long userId);

	/**
	 * This method removes a {@link VerificationToken}, given the user's id
	 * 
	 * @param userId
	 *            the user's Id
	 * @return the removed {@link VerificationToken} model if verificationToken
	 *         exists, otherwise it will return <b>null</b>
	 */
	public VerificationToken removeToken(Long userId);

	/**
	 * This method removes a {@link VerificationToken}, given the user's token
	 * 
	 * @param token
	 *            the user's token
	 * @return the removed {@link VerificationToken} model if verificationToken
	 *         exists, otherwise it will return <b>null</b>
	 */
	public VerificationToken removeToken(String token);

	/**
	 * This method returns a all {@link VerificationToken} models
	 * 
	 * @return a list of {@link VerificationToken}
	 */
	public List<VerificationToken> getAllTokens();
}
