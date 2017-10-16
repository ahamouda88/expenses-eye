package com.expensesEye.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.expensesEye.config.SpringBootConfig;
import com.expensesEye.model.exception.TokenExpiredException;
import com.expensesEye.model.exception.UserRegistrationException;
import com.expensesEye.model.exception.UserVerificationException;
import com.expensesEye.persist.dao.TokenDao;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.persist.entity.VerificationToken;
import com.expensesEye.service.impl.EmailServiceImpl;
import com.expensesEye.service.impl.RegistrationServiceImpl;
import com.expensesEye.service.impl.TokenServiceImpl;
import com.expensesEye.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, UserServiceImpl.class, UserDao.class, TokenServiceImpl.class,
		EmailServiceImpl.class, RegistrationServiceImpl.class, TokenDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class RegistrationServiceTest {

	@Autowired
	private RegistrationService registrationService;

	private VerificationToken token1;
	private VerificationToken token2;
	private VerificationToken token3;

	@Before
	public void testRegisterUser() {
		// Create first user
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ADMIN);
		User userOne = new User("John", "Snow", "john@hotmail.com", "admin", roles);
		token1 = registrationService.registerUser(userOne);
		Assert.assertNotNull(token1);

		// Create second user
		User userTwo = new User("Andres", "Iniesta", "iniesta@hotmail.com", "barcelona",
				Arrays.asList(UserRole.USER_MANAGER));
		token2 = registrationService.registerUser(userTwo);
		Assert.assertNotNull(token2);

		// Create third user
		User userThree = new User("Lionel", "Messi", "messi@hotmail.com", "thiago",
				Arrays.asList(UserRole.REGULAR_USER));
		token3 = registrationService.registerUser(userThree);
		Assert.assertNotNull(token3);
	}

	@Test
	public void testInvalidRegister() {
		VerificationToken token = registrationService.registerUser(null);
		Assert.assertNull(token);
	}

	@Test
	public void testVerifyUser() throws UserVerificationException, TokenExpiredException {
		Assert.assertFalse(token1.getUser().isVerified());
		User user = registrationService.verifyUser(token1.getUser().getEmail(), token1.getToken());
		Assert.assertTrue(user.isVerified());
	}

	@Test(expected = UserVerificationException.class)
	public void testInvalidVerifyUserOne() throws UserVerificationException, TokenExpiredException {
		registrationService.verifyUser(token1.getUser().getEmail(), "invalidToken");
	}

	@Test(expected = UserVerificationException.class)
	public void testInvalidVerifyUserTwo() throws UserVerificationException, TokenExpiredException {
		registrationService.verifyUser(token1.getUser().getEmail(), token2.getToken());
	}

	@Test(expected = UserVerificationException.class)
	public void testInvalidVerifyUserThree() throws UserVerificationException, TokenExpiredException {
		registrationService.verifyUser(null, token2.getToken());
	}

	@Test
	public void testValidSendRegistrationRequest() throws MessagingException {
		registrationService.sendRegistrationRequest("ahmed@gmail.com", "quynh@gmail.com");
		List<Message> inbox = Mailbox.get("quynh@gmail.com");

		Assert.assertEquals(1, inbox.size());
		Assert.assertEquals("Expenses Eye Registration Request", inbox.get(0).getSubject());
	}

	@Test(expected = UserRegistrationException.class)
	public void testInvalidSendRegistrationRequest() {
		registrationService.sendRegistrationRequest("ahmed@gmail.com", "ahmed.com");
	}

	@Test
	public void testGenerateNewToken() throws UserVerificationException {
		String oldToken = token3.getToken();
		VerificationToken verificationToken = registrationService.generateNewVerificationToken(oldToken);
		Assert.assertNotEquals(oldToken, verificationToken.getToken());
	}

	@Test(expected = UserVerificationException.class)
	public void testInvalidGenerateNewToken() throws UserVerificationException {
		registrationService.generateNewVerificationToken("invalid");
	}

}
