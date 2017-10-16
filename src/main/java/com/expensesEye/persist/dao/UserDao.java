package com.expensesEye.persist.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.request.UserSearchRequest;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

/**
 * A class that extends {@link AbstractDao}, and it defines the persistence
 * operations to be performed on {@link User}
 */
@Repository
public class UserDao extends AbstractDao<User, Long> {

	public UserDao() {
		super(User.class);
	}

	/**
	 * This method finds a user that is mapped to the given email
	 * 
	 * @param email
	 *            the user's email
	 * @return the user mapped to the given email, or <b>null</b> if email doesn't
	 *         exist
	 */
	public User findByEmail(String email) {
		return find(email, null, null, null);
	}

	/**
	 * This method finds a verified/unverified user that is mapped to the given
	 * email or Id. The method will check the id first if exists then will be used
	 * in the query
	 * 
	 * @param email
	 *            the user's email
	 * @param userId
	 *            the user's id
	 * @param verified
	 *            a boolean to return either verified or unverified user
	 * @return the user mapped to the given id/email, or <b>null</b> if user doesn't
	 *         exist
	 */
	public User find(String email, Long userId, Boolean verified) {
		return find(email, userId, verified, null);
	}

	/**
	 * This method finds a user that is mapped to the given email or Id. The method
	 * will check the id first if exists then will be used in the query, otherwise
	 * it will use the email to retrieve the user
	 * 
	 * @param email
	 *            the user's email
	 * @param userId
	 *            the user's id
	 * @param verified
	 *            a boolean to return either verified or unverified user
	 * @param locked
	 *            a boolean to return either locked or unlocked user
	 * @return the user mapped to the given id/email and matches the given request,
	 *         or <b>null</b> if user doesn't exist
	 */
	public User find(String email, Long userId, Boolean verified, Boolean locked) {
		UserSearchRequest searchRequest = UserSearchRequest.builder().email(email).id(userId).verified(verified)
				.locked(locked).build();
		List<User> result = search(searchRequest);
		if (CollectionUtils.isNotEmpty(result)) {
			if (result.size() > 1) {
				logger.debug(String.format(ErrorMessages.INVALID_NUMBER_USER_RESULT, searchRequest.toString()));
				return null;
			}
			return result.get(0);
		}
		return null;
	}

	/**
	 * This method finds a verified/unverified user that is mapped to the given
	 * email or Id. The method will check the id first if exists then will be used
	 * in the query
	 *
	 * @param UserSearchRequest
	 *            a {@link UserSearchRequest} object
	 * @return list of users if the selection is successful, otherwise it will
	 *         return <b>null</b>
	 */
	public List<User> search(UserSearchRequest searchRequest) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> from = cq.from(User.class);
			cq.select(from);

			List<Predicate> predicates = new ArrayList<>();
			if (searchRequest.getId() != null) {
				predicates.add(cb.equal(from.get("id"), searchRequest.getId()));
			}
			if (searchRequest.isVerified() != null) {
				predicates.add(cb.equal(from.get("verified"), searchRequest.isVerified()));
			}
			if (searchRequest.isLocked() != null) {
				predicates.add(cb.equal(from.get("locked"), searchRequest.isLocked()));
			}
			if (searchRequest.getEmail() != null) {
				predicates.add(cb.equal(cb.lower(from.get("email")), searchRequest.getEmail().toLowerCase()));
			}
			if (searchRequest.getFirstName() != null) {
				predicates.add(cb.equal(cb.lower(from.get("firstName")), searchRequest.getFirstName().toLowerCase()));
			}
			if (searchRequest.getLastName() != null) {
				predicates.add(cb.equal(cb.lower(from.get("lastName")), searchRequest.getLastName().toLowerCase()));
			}
			if (searchRequest.getRoles() != null) {
				predicates.add(cb.isMember(searchRequest.getRoles(), from.get("roles")));
			}
			// If there is no predicates then get all users
			if (!predicates.isEmpty()) {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			}

			TypedQuery<User> query = entityManager.createQuery(cq);
			return query.getResultList();
		} catch (NoResultException ex) {
			logger.debug(String.format(ErrorMessages.INVALID_USER_SEARCH_REQUEST, searchRequest.toString()));
			return null;
		}
	}

	/**
	 * This method selects users that don't have any of the given ids and any of the
	 * given roles
	 * 
	 * @param ids
	 *            a list of ids
	 * @param roles
	 *            a list of {@link UserRole}
	 * @return list of users if the selection is successful, otherwise it will
	 *         return <b>null</b>
	 */
	public List<User> findNotInclude(List<Long> ids, List<UserRole> roles) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> from = cq.from(User.class);
			cq.select(from);

			List<Predicate> predicates = new ArrayList<>();
			if (ids != null) {
				predicates.add(cb.not(from.get("id").in(ids)));
			}
			if (roles != null) {
				predicates.add(cb.isNotMember(roles, from.get("roles")));
			}

			// If there is no predicates then get all users
			if (!predicates.isEmpty()) {
				cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
			}
			TypedQuery<User> query = entityManager.createQuery(cq);
			return query.getResultList();
		} catch (NoResultException ex) {
			logger.debug(ErrorMessages.FAILED_GET_ALL_USERS);
			return null;
		}
	}

}
