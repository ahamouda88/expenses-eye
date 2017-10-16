package com.expensesEye.service;

import java.io.Serializable;
import java.util.List;

import com.expensesEye.model.request.UserSearchRequest;
import com.expensesEye.model.request.UserUpdateRequest;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

/**
 * An interface that defines the operations to be performed on {@link User}
 * model, and it should interacts with the User's Dao Layer {@link UserDao}
 * 
 * @param <T>
 *            the model Id's data type
 */
public interface UserService<T extends Serializable> {

	/**
	 * This method creates a {@link User} model, given the user model
	 * 
	 * @param user
	 *            the user that need to be created
	 * @return true if user is created successfully, or false otherwise
	 */
	public boolean createUser(User user);

	/**
	 * This method updates a {@link User} model, given the user model
	 * 
	 * @param user
	 *            the user that need to be updated
	 * @return true if user is updated successfully, or false otherwise
	 */
	public boolean updateUser(User user);

	/**
	 * This method updates a {@link User} model, given the user's email, and a
	 * {@link UserUpdateRequest} object
	 * 
	 * @param email
	 *            the email of the user need to be updated
	 * @param updateRequest
	 *            a {@link UserUpdateRequest} object
	 * @return the updated user if user is updated successfully, or <b>null</b>
	 *         otherwise
	 */
	public User updateUser(String email, UserUpdateRequest updateRequest);

	/**
	 * This method removes a {@link User} model, based on the given user's id
	 * 
	 * @param userId
	 *            a user's Id
	 * @param <T>
	 *            the user's Id data type
	 * @return the removed {@link User} model if user exists, otherwise it will
	 *         return <b>null</b>
	 */
	public User deleteUser(T userId);

	/**
	 * This method returns a {@link User} model, based on the given user's id
	 * 
	 * @param userId
	 *            a user's Id
	 * @param <T>
	 *            the user's Id data type
	 * @return a {@link User} model if user exists, otherwise it will return
	 *         <b>null</b>
	 */
	public User getUser(T userId);

	/**
	 * This method returns a {@link User} model, based on the given an email
	 * 
	 * @param email
	 *            a user's email
	 * @return a {@link User} model if user exists, otherwise it will return
	 *         <b>null</b>
	 */
	public User getUserByEmail(String email);

	/**
	 * This method returns a {@link User} model, based on the given an email
	 * 
	 * @param email
	 *            a user's email
	 * @param verified
	 *            boolean to identify if a verified user is needed or not
	 * @return a {@link User} model if user exists, otherwise it will return
	 *         <b>null</b>
	 */
	public User getUserByEmail(String email, Boolean verified);

	/**
	 * This method returns a {@link User} model, based on the given an email
	 * 
	 * @param email
	 *            a user's email
	 * @param verified
	 *            boolean to identify if a verified user is needed or not
	 * @param locked
	 *            boolean to identify if a locked user is needed or not
	 * @return a {@link User} model if user exists, otherwise it will return
	 *         <b>null</b>
	 */
	public User getUserByEmail(String email, Boolean verified, Boolean locked);

	/**
	 * This method filters users based on to the given {@link UserSearchRequest}
	 * 
	 * @param searchRequest
	 *            an {@link UserSearchRequest} object
	 * @return list of users if exists, otherwise it will return <b>null</b>
	 */
	public List<User> getFilteredUsers(UserSearchRequest searchRequest);

	/**
	 * This method returns users that don't have any of the given ids and any of the
	 * given roles
	 * 
	 * @param ids
	 *            a list of ids
	 * @param roles
	 *            a list of {@link UserRole}
	 * @return list of users if exists otherwise it will return <b>null</b>
	 */
	public List<User> getUsersNotIncluding(List<Long> ids, List<UserRole> roles);

	/**
	 * This method returns a list of all users
	 * 
	 * @return a list of users
	 */
	public List<User> getAllUsers();

}
