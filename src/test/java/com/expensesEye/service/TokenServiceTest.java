package com.expensesEye.service;

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
import com.expensesEye.persist.dao.TokenDao;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.service.impl.TokenServiceImpl;
import com.expensesEye.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, TokenServiceImpl.class, TokenDao.class, UserServiceImpl.class,
		UserDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class TokenServiceTest {

	@Autowired
	private TokenService<Long> tokenService;

	@Autowired
	private UserService<Long> userService;

	private User userOne;
	private User userTwo;
	private User userThree;

	@Before
	public void testCreateToken() {
		// Create first user
		userOne = new User("John", "Snow", "john@hotmail.com", "admin", Arrays.asList(UserRole.ADMIN), true);
		userService.createUser(userOne);
		VerificationToken tokenOne = new VerificationToken("12345", userOne);
		Assert.assertEquals(true, tokenService.createToken(tokenOne));

		// Create second user
		userTwo = new User("Andres", "Iniesta", "iniesta@hotmail.com", "barcelona",
				Arrays.asList(UserRole.USER_MANAGER), true);
		userService.createUser(userTwo);
		VerificationToken tokenTwo = new VerificationToken("101010", userTwo);
		Assert.assertEquals(true, tokenService.createToken(tokenTwo));

		// Create third user
		userThree = new User("Lionel", "Messi", "messi@hotmail.com", "thiago", Arrays.asList(UserRole.REGULAR_USER),
				true);
		userService.createUser(userThree);
		VerificationToken tokenThree = new VerificationToken("89000", userThree);
		Assert.assertEquals(true, tokenService.createToken(tokenThree));
	}

	@Test
	public void testCreateInvalidToken() {
		VerificationToken tokenOne = new VerificationToken("12345", null);
		Assert.assertEquals(false, tokenService.createToken(tokenOne));
	}

	@Test
	public void testUpdateToken() {
		VerificationToken verificationToken = tokenService.getToken(userOne.getId());
		String newToken = "111111";
		verificationToken.setToken(newToken);

		Assert.assertEquals(0, verificationToken.getVersion());
		Assert.assertEquals(true, tokenService.updateToken(verificationToken));

		verificationToken = tokenService.getToken(newToken);
		Assert.assertEquals(1, verificationToken.getVersion());
		Assert.assertEquals(newToken, verificationToken.getToken());
	}

	@Test
	public void testGetUser() {
		User user = tokenService.getUser("89000");
		Assert.assertEquals("messi@hotmail.com", user.getEmail());
		Assert.assertEquals("Messi", user.getLastName());
	}

	@Test
	public void testInvalidGetUser() {
		User user = tokenService.getUser(null);
		Assert.assertNull(user);

		user = tokenService.getUser("-101");
		Assert.assertNull(user);
	}

	@Test
	public void testGetToken() {
		VerificationToken verificationToken = tokenService.getToken("89000");
		Assert.assertEquals("messi@hotmail.com", verificationToken.getUser().getEmail());
		Assert.assertNotNull(verificationToken.getId());
	}

	@Test
	public void testInvalidGetToken() {
		VerificationToken verificationToken = tokenService.getToken("-1");
		Assert.assertNull(verificationToken);
	}

	@Test
	public void testRemoveByToken() {
		VerificationToken verificationToken = tokenService.removeToken("101010");
		Assert.assertNotNull(verificationToken);
		Assert.assertEquals("Andres", verificationToken.getUser().getFirstName());
	}
	
	@Test
	public void testInvalidRemoveByToken() {
		VerificationToken verificationToken = tokenService.removeToken("0");
		Assert.assertNull(verificationToken);
	}

	@Test
	public void testGetAllTokens() {
		Assert.assertEquals(3, tokenService.getAllTokens().size());
		Assert.assertEquals(3, userService.getAllUsers().size());

		User deletedUser = userService.deleteUser(userOne.getId());
		Assert.assertNotNull(deletedUser);

		Assert.assertEquals(2, userService.getAllUsers().size());
		Assert.assertEquals(2, tokenService.getAllTokens().size());
	}

	@Test
	public void testGetTokenByUserId() {
		VerificationToken verificationToken = tokenService.getToken(userTwo.getId());
		Assert.assertEquals("iniesta@hotmail.com", verificationToken.getUser().getEmail());
		Assert.assertNotNull(verificationToken.getId());
	}
}
