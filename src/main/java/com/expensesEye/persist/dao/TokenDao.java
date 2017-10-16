package com.expensesEye.persist.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;

/**
 * A class that extends {@link AbstractDao}, and it defines the persistence
 * operations to be performed on {@link VerificationToken}
 */
@Repository
public class TokenDao extends AbstractDao<VerificationToken, Long> {

	public TokenDao() {
		super(VerificationToken.class);
	}

	/**
	 * This method finds a verification token that is mapped to a given {@link User}
	 * 
	 * @param user
	 *            a {@link User} object
	 * @return the {@link VerificationToken} associated with the given user
	 */
	public VerificationToken findByUser(Long userId) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<VerificationToken> cq = cb.createQuery(VerificationToken.class);
			Root<VerificationToken> from = cq.from(VerificationToken.class);
			cq.select(from);
			Predicate predicate = cb.equal(from.get("user"), userId);
			cq.where(predicate);
			TypedQuery<VerificationToken> query = entityManager.createQuery(cq);
			return query.getSingleResult();
		} catch (NoResultException ex) {
			logger.debug(String.format(ErrorMessages.FAILED_GET_VERIFICATION_TOKEN, userId));
			return null;
		}
	}

	/**
	 * This method finds a verification token that is mapped to a given token
	 * 
	 * @param token
	 *            the user's token
	 * @return the {@link VerificationToken} mapped to the given token, or
	 *         <b>null</b> if token doesn't exist
	 */
	public VerificationToken findByToken(String token) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<VerificationToken> cq = cb.createQuery(VerificationToken.class);
			Root<VerificationToken> from = cq.from(VerificationToken.class);
			cq.select(from);
			Predicate predicate = cb.equal(from.get("token"), token);
			cq.where(predicate);
			TypedQuery<VerificationToken> query = entityManager.createQuery(cq);
			return query.getSingleResult();
		} catch (NoResultException ex) {
			logger.debug(String.format(ErrorMessages.INVALID_VERIFICATION_TOKEN, token));
			return null;
		}
	}

}
