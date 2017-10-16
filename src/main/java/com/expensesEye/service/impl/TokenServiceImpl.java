package com.expensesEye.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.expensesEye.persist.dao.TokenDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.service.TokenService;

/**
 * Implementation of {@link TokenService}
 */
@Service
@Transactional
public class TokenServiceImpl implements TokenService<Long> {

	@Autowired
	private TokenDao tokenDao;

	@Override
	public boolean createToken(VerificationToken verificationToken) {
		if (!isValid(verificationToken))
			return false;

		return tokenDao.create(verificationToken);
	}

	@Override
	public boolean updateToken(VerificationToken verificationToken) {
		if (!isValid(verificationToken))
			return false;

		return tokenDao.update(verificationToken);
	}

	@Override
	public User getUser(String token) {
		VerificationToken verificationToken = getToken(token);
		return verificationToken == null ? null : verificationToken.getUser();
	}

	@Override
	public VerificationToken getToken(String token) {
		if (StringUtils.isEmpty(token))
			return null;

		return tokenDao.findByToken(token);
	}

	@Override
	public VerificationToken getToken(Long userId) {
		if (userId == null)
			return null;

		return tokenDao.findByUser(userId);
	}

	@Override
	public VerificationToken removeToken(Long userId) {
		VerificationToken verificationToken = getToken(userId);
		return verificationToken == null ? null : tokenDao.remove(verificationToken);
	}

	@Override
	public VerificationToken removeToken(String token) {
		if (token == null)
			return null;

		VerificationToken verificationToken = tokenDao.findByToken(token);
		return verificationToken == null ? null : tokenDao.remove(verificationToken);
	}

	@Override
	public List<VerificationToken> getAllTokens() {
		return tokenDao.getAll();
	}

	private boolean isValid(VerificationToken token) {
		return token != null && token.getToken() != null && token.getUser() != null && token.getExpiryDate() != null;
	}

}
