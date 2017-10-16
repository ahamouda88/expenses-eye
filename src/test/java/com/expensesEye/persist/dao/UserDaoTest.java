package com.expensesEye.persist.dao;

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
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, UserDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	private User userOne;
	private User userTwo;

	@Before
	public void testCreateUser() {
		// Create the first user
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.ADMIN);
		userOne = new User("John", "Snow", "temp1@hotmail.com", "admin", roles);

		boolean expected = true;
		boolean actualResult = userDao.create(userOne);
		Assert.assertEquals(expected, actualResult);

		// Create the second user
		userTwo = new User("Lionel", "Messi", "temp2@hotmail.com", "password", Arrays.asList(UserRole.REGULAR_USER));

		actualResult = userDao.create(userTwo);
		Assert.assertEquals(expected, actualResult);
	}

	@Test
	public void testInValidCreateUser() {
		// Create Invalid user
		User userOne = new User("Cristiano", "Ronaldo", "temp@hotmail.com", "admin", null);

		boolean expected = false;
		boolean actualResult = userDao.create(userOne);
		Assert.assertEquals(expected, actualResult);
	}

	@Test
	public void testUpdateUser() {
		User user = userDao.find(userOne.getId());

		String expectedPassword = "admin";
		Assert.assertEquals(expectedPassword, user.getPassword());

		// Update user with Id=1
		expectedPassword = "newPassword";
		user.setPassword(expectedPassword);

		// Test if user is updated successfully
		boolean expected = true;
		boolean actualResult = userDao.update(user);
		Assert.assertEquals(expected, actualResult);

		// Test updated password
		User updatedUser = userDao.find(userOne.getId());
		Assert.assertEquals(expectedPassword, updatedUser.getPassword());
	}

	@Test
	public void testRemoveUser() {
		// Get last added user
		long id = userDao.getAll().get(1).getId();
		User userToBeRemoved = userDao.find(id);

		String expectedEmail = "temp2@hotmail.com";
		Assert.assertEquals(expectedEmail, userToBeRemoved.getEmail());

		// Test if user is removed successfully
		User removedUser = userDao.remove(userToBeRemoved);
		Assert.assertEquals(expectedEmail, removedUser.getEmail());

		// Test number of remaining users
		List<User> users = userDao.getAll();
		int expectedUsers = 1;
		int actualUsers = users.size();
		Assert.assertEquals(expectedUsers, actualUsers);
	}

	@Test
	public void testPersistingChild() {
		User user = new User("Ahmed", "Hamouda", "temp@hotmail.com", "nice", Arrays.asList(UserRole.USER_MANAGER));

		Expense expense = new Expense(250.0, 123L, "Soccer Ball");
		expense.setUser(user);

		List<Expense> list = new ArrayList<>();
		list.add(expense);
		user.setExpenses(list);

		userDao.create(user);
		Assert.assertTrue(expense.getId() > 0);
	}

	@Test
	public void testInValidFind() {
		// Find user with invalid Id
		User user = userDao.find(100L);
		Assert.assertNull(user);
	}

	@Test
	public void testGetAll() {
		List<User> users = userDao.getAll();
		int expectedUsers = 2;
		int actualUsers = users.size();

		Assert.assertEquals(expectedUsers, actualUsers);
	}

	@Test
	public void testFindByEmail() {
		User user = userDao.find("temp1@hotmail.com", null, false);
		String expectedEmail = "temp1@hotmail.com";

		Assert.assertEquals(expectedEmail, user.getEmail());
	}

	@Test
	public void testFindVerifiedUser() {
		// There are no verified users yet
		User user = userDao.find("temp1@hotmail.com", null, true);
		Assert.assertNull(user);

		// Verify a user
		User userOne = userDao.find("temp1@hotmail.com", null, false);
		userOne.setVerified(true);

		// Test if user is updated successfully
		boolean expected = true;
		boolean actualResult = userDao.update(userOne);
		Assert.assertEquals(expected, actualResult);

		// Return the verified user
		user = userDao.find("temp1@hotmail.com", null, true);
		Assert.assertNotNull(user);
		Assert.assertTrue(user.isVerified());
		Assert.assertEquals("John", user.getFirstName());
	}

	@Test
	public void testFindLockedAndVerified() {
		// There are no locked and verified users yet
		User user = userDao.find("temp1@hotmail.com", null, true, true);
		Assert.assertNull(user);

		// Verify and lock a user
		User userOne = userDao.find("temp1@hotmail.com", null, false);
		userOne.setVerified(true);
		userOne.setLocked(true);

		// Test if user is updated successfully
		boolean expected = true;
		boolean actualResult = userDao.update(userOne);
		Assert.assertEquals(expected, actualResult);

		// Return the verified user
		user = userDao.find("temp1@hotmail.com", null, true, true);
		Assert.assertNotNull(user);
		Assert.assertTrue(user.isVerified());
		Assert.assertEquals("John", user.getFirstName());
	}

	@Test
	public void testSearchUsers() {
		// Search by locked users
		UserSearchRequest searchRequest = UserSearchRequest.builder().locked(true).build();
		List<User> users = userDao.search(searchRequest);
		Assert.assertEquals(0, users.size());

		// Search by verified and locked user
		searchRequest = UserSearchRequest.builder().verified(false).locked(false).build();
		users = userDao.search(searchRequest);
		Assert.assertEquals(2, users.size());

		// Search by roles
		List<UserRole> roles = new ArrayList<>();
		roles.add(UserRole.REGULAR_USER);
		searchRequest = UserSearchRequest.builder().roles(roles).build();
		users = userDao.search(searchRequest);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("Messi", users.get(0).getLastName());

		// Search by not existing user role
		searchRequest = UserSearchRequest.builder().roles(Arrays.asList(UserRole.USER_MANAGER)).build();
		users = userDao.search(searchRequest);
		Assert.assertEquals(0, users.size());

		// Search by first name
		searchRequest = UserSearchRequest.builder().firstName("john").build();
		users = userDao.search(searchRequest);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("temp1@hotmail.com", users.get(0).getEmail());
	}

	@Test
	public void testNotIncludeFind() {
		// Filter users that are doesn't include user one id
		List<User> users = userDao.findNotInclude(Arrays.asList(userOne.getId()), null);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("Messi", users.get(0).getLastName());

		// Filter users that are doesn't include user's role
		users = userDao.findNotInclude(null, Arrays.asList(UserRole.REGULAR_USER));
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("Snow", users.get(0).getLastName());

		users = userDao.findNotInclude(Arrays.asList(100000L), Arrays.asList(UserRole.USER_MANAGER));
		Assert.assertEquals(2, users.size());
	}
}
