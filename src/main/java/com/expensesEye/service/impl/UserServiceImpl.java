package com.expensesEye.service.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.request.UserSearchRequest;
import com.expensesEye.model.request.UserUpdateRequest;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.service.TokenService;
import com.expensesEye.service.UserService;

/**
 * Implementation of {@link UserService}
 */
@Service
@Transactional
public class UserServiceImpl implements UserService<Long> {
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenService<Long> tokenService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public boolean createUser(User user) {
		if (!isValid(user))
			return false;

		if (userExists(user.getEmail())) {
			logger.error(String.format(ErrorMessages.ALREADY_EXISTS_EMAIL, user.getEmail()));
			return false;
		}
		// Encode password when creating a user
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userDao.create(user);
	}

	@Override
	public boolean updateUser(User user) {
		if (!isValid(user) || user.getId() == null)
			return false;

		User userOldData = userDao.find(null, user.getId(), true);

		if (userOldData == null) {
			logger.error(String.format(ErrorMessages.INVALID_USER_ID, user.getId()));
			return false;
		}

		// If the user name is being updated!
		if (!userOldData.getEmail().equals(user.getEmail())) {
			if (userExists(user.getEmail())) {
				logger.error(String.format(ErrorMessages.ALREADY_EXISTS_EMAIL, user.getEmail()));
				return false;
			}
		}
		return userDao.update(user);
	}

	@Override
	public User updateUser(String email, UserUpdateRequest updateRequest) {
		User user = this.getUserByEmail(email);
		if (user == null) {
			logger.error(String.format(ErrorMessages.INVALID_USER_EMAIL, email));
			return null;
		}
		if (updateRequest != null) {
			if (updateRequest.getFirstName() != null) {
				user.setFirstName(updateRequest.getFirstName());
			}
			if (updateRequest.getLastName() != null) {
				user.setLastName(updateRequest.getLastName());
			}
			if (updateRequest.getLocked() != null) {
				user.setLocked(updateRequest.getLocked());
			}
			if (updateRequest.getVerified() != null) {
				user.setVerified(updateRequest.getVerified());
			}
			if (updateRequest.getEmail() != null) {
				user.setEmail(updateRequest.getEmail());
			}
			if (updateRequest.getPassword() != null) {
				user.setPassword(updateRequest.getPassword());
			}
			if (updateRequest.getImage() != null) {
				user.setImage(updateRequest.getImage());
			}
		}
		return updateUser(user) ? user : null;
	}

	@Override
	public User deleteUser(Long userId) {
		User user = userDao.find(null, userId, null);

		if (user == null)
			return null;

		// Remove token associated with the user
		tokenService.removeToken(user.getId());
		return userDao.remove(user);
	}

	@Override
	public User getUser(Long userId) {
		return userId == null ? null : userDao.find(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return getUserByEmail(email, null, null);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByEmail(String email, Boolean verified) {
		return getUserByEmail(email, verified, null);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByEmail(String email, Boolean verified, Boolean locked) {
		return email == null ? null : userDao.find(email, null, verified, locked);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getFilteredUsers(UserSearchRequest searchRequest) {
		return searchRequest == null ? null : userDao.search(searchRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersNotIncluding(List<Long> ids, List<UserRole> roles) {
		return userDao.findNotInclude(ids, roles);
	}

	/*
	 * Check if user with the given user unique Id
	 */
	private boolean userExists(String id) {
		return this.getUserByEmail(id) == null ? false : true;
	}

	private boolean isValid(User user) {
		return user != null && isNotEmpty(user.getFirstName()) && isNotEmpty(user.getLastName())
				&& user.getRoles() != null && isNotEmpty(user.getEmail()) && isNotEmpty(user.getPassword());
	}

}
