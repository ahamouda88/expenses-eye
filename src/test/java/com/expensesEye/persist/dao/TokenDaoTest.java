package com.expensesEye.persist.dao;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.persist.entity.VerificationToken;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, TokenDao.class, UserDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class TokenDaoTest {

	@Autowired
	private TokenDao tokenDao;

	@Autowired
	private UserDao userDao;

	private User userOne;
	private User userTwo;

	@Before
	public void testCreateToken() {
		// Create the first user
		userOne = new User("John", "Snow", "temp1@hotmail.com", "admin", Arrays.asList(UserRole.ADMIN));
		userDao.create(userOne);
		VerificationToken tokenOne = new VerificationToken("12345", userOne);
		Assert.assertEquals(true, tokenDao.create(tokenOne));

		// Create the second user
		userTwo = new User("Lionel", "Messi", "temp2@hotmail.com", "password", Arrays.asList(UserRole.REGULAR_USER));
		userDao.create(userTwo);
		VerificationToken tokenTwo = new VerificationToken("67890", userTwo);
		Assert.assertEquals(true, tokenDao.create(tokenTwo));
	}

	@Test
	public void testInValidCreateToken() {
		VerificationToken token = new VerificationToken("12345", null);
		Assert.assertEquals(false, tokenDao.create(token));
	}

	@Test
	public void testFindByToken() {
		VerificationToken token = tokenDao.findByToken("12345");

		Assert.assertEquals("John", token.getUser().getFirstName());
		Assert.assertNotNull(token.getId());
	}

	@Test
	public void testInvalidFindByToken() {
		VerificationToken token = tokenDao.findByToken("invalidToken");
		Assert.assertNull(token);
	}

	@Test
	public void testFindByUserId() {
		VerificationToken token = tokenDao.findByUser(userTwo.getId());

		Assert.assertEquals("Lionel", token.getUser().getFirstName());
		Assert.assertNotNull(token.getId());
	}

	@Test
	public void testInvalidFindByUserId() {
		VerificationToken token = tokenDao.findByUser(-1l);
		Assert.assertNull(token);
	}
}
