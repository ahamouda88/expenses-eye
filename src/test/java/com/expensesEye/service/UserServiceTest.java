package com.expensesEye.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.expensesEye.model.request.UserSearchRequest;
import com.expensesEye.model.request.UserUpdateRequest;
import com.expensesEye.persist.dao.TokenDao;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.service.impl.TokenServiceImpl;
import com.expensesEye.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, UserServiceImpl.class, UserDao.class, TokenServiceImpl.class,
		TokenDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class UserServiceTest {

	@Autowired
	private UserService<Long> userService;

	private User userOne;
	private User userTwo;
	private User userThree;

	@Before
	public void testCreateUser() {
		// Create first user
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ADMIN);
		userOne = new User("John", "Snow", "john@hotmail.com", "admin", roles, true);
		boolean expected = true;
		boolean actual = userService.createUser(userOne);
		Assert.assertEquals(expected, actual);

		// Create second user
		userTwo = new User("Andres", "Iniesta", "iniesta@hotmail.com", "barcelona",
				Arrays.asList(UserRole.REGULAR_USER), true);
		actual = userService.createUser(userTwo);
		Assert.assertEquals(expected, actual);

		// Create third user
		userThree = new User("Lionel", "Messi", "messi@hotmail.com", "thiago", Arrays.asList(UserRole.REGULAR_USER),
				true);
		actual = userService.createUser(userThree);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testInvalidCreateUserNullEmail() {
		// Trying to create user with null email
		User userOne = new User("Invalid", "User", null, "password", Arrays.asList(UserRole.ADMIN));
		boolean expected = false;
		boolean actual = userService.createUser(userOne);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testInvalidCreateUserExistingEmail() {
		// Trying to create user with an existing email
		User userOne = new User("Hazem", "Emam", "messi@hotmail.com", "password", Arrays.asList(UserRole.ADMIN), true);
		boolean expected = false;
		boolean actual = userService.createUser(userOne);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetAll() {
		List<User> userList = userService.getAllUsers();
		int expectedUsers = 3;
		int actualUsers = userList.size();
		Assert.assertEquals(expectedUsers, actualUsers);
	}

	@Test
	public void testUpdateUser() {
		// Retrieved the second user
		User user = userService.getAllUsers().get(1);
		String expectedPassword = "barcelona";
		Assert.assertEquals(expectedPassword, user.getPassword());

		// Update user's password
		user.setPassword("barcelonaNewPassword");
		userService.updateUser(user);

		User updatedUser = userService.getAllUsers().get(1);
		expectedPassword = "barcelonaNewPassword";
		Assert.assertEquals(expectedPassword, updatedUser.getPassword());
	}

	@Test
	public void testUpdateUserUsingUpdateRequest() {
		String newFirstName = "Mr. John";
		UserUpdateRequest updateRequest = UserUpdateRequest.builder().verified(true).firstName(newFirstName).build();

		User user = userService.updateUser("john@hotmail.com", updateRequest);
		Assert.assertEquals(newFirstName, user.getFirstName());
		Assert.assertTrue(user.isVerified());
		Assert.assertFalse(user.isLocked());
	}

	@Test
	public void testInvalidUpdateUser() {
		// Retrieved the first user
		User user = userService.getAllUsers().get(0);
		User newUser = copyUser(user);
		String expectedEmail = "john@hotmail.com";
		Assert.assertEquals(expectedEmail, newUser.getEmail());

		// Update user's email with an existing one!
		newUser.setEmail("messi@hotmail.com");
		userService.updateUser(newUser);
	}

	@Test
	public void testDeleteUser() {
		// Test if user is removed successfully
		String expectedEmail = "john@hotmail.com";
		User removedUser = userService.deleteUser(userOne.getId());
		Assert.assertEquals(expectedEmail, removedUser.getEmail());

		// Test number of remaining users
		List<User> users = userService.getAllUsers();
		int expectedUsers = 2;
		int actualUsers = users.size();
		Assert.assertEquals(expectedUsers, actualUsers);
	}

	@Test
	public void testInvalidRemoveUser() {
		long invalidId = 120;

		// Test removing user with invalid Id
		User removedUser = userService.deleteUser(invalidId);
		Assert.assertNull(removedUser);
	}

	@Test
	public void testGetUserByEmail() {
		User user = userService.getUserByEmail("messi@hotmail.com");
		String expectedLastName = "Messi";
		Assert.assertEquals(expectedLastName, user.getLastName());
	}

	@Test
	public void testGetVerifiedUserByEmail() {
		User user = userService.getUserByEmail("iniesta@hotmail.com", true);
		String expectedLastName = "Iniesta";
		Assert.assertEquals(expectedLastName, user.getLastName());
	}

	@Test
	public void testGetLockedUserByEmail() {
		// No locked users yet
		User user = userService.getUserByEmail("iniesta@hotmail.com", true, true);
		Assert.assertNull(user);
	}

	@Test
	public void testInvaliedFilterUsers() {
		// No locked users yet
		List<User> users = userService.getFilteredUsers(null);
		Assert.assertNull(users);
	}

	@Test
	public void testFilterUsers() {
		UserSearchRequest searchRequest = new UserSearchRequest.Builder().roles(Arrays.asList(UserRole.REGULAR_USER))
				.build();
		List<User> users = userService.getFilteredUsers(searchRequest);
		Assert.assertEquals(2, users.size());

		searchRequest = new UserSearchRequest.Builder().roles(Arrays.asList(UserRole.USER_MANAGER)).build();
		users = userService.getFilteredUsers(searchRequest);
		Assert.assertEquals(0, users.size());

		searchRequest = new UserSearchRequest.Builder().roles(Arrays.asList(UserRole.USER_MANAGER)).build();
		users = userService.getFilteredUsers(searchRequest);
		Assert.assertEquals(0, users.size());

	}

	@Test
	public void testInvalidGetUserByEmail() {
		User user = userService.getUserByEmail("ronaldo@hotmail.com");
		Assert.assertNull(user);
	}

	@Test
	public void testGetUsersNotIncluding() {
		List<User> users = userService.getUsersNotIncluding(Arrays.asList(userOne.getId(), userThree.getId()), null);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("Iniesta", users.get(0).getLastName());

		users = userService.getUsersNotIncluding(Arrays.asList(userOne.getId()), Arrays.asList(UserRole.ADMIN));
		Assert.assertEquals(2, users.size());
	}

	private User copyUser(User user) {
		User newUser = new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(),
				user.getRoles());
		newUser.setId(user.getId());
		newUser.setVersion(user.getVersion());
		return newUser;
	}
}
