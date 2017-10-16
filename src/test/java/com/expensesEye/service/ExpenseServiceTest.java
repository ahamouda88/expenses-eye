package com.expensesEye.service;

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
import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.dao.ExpenseDao;
import com.expensesEye.persist.dao.TokenDao;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;
import com.expensesEye.service.impl.ExpenseServiceImpl;
import com.expensesEye.service.impl.TokenServiceImpl;
import com.expensesEye.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, ExpenseServiceImpl.class, ExpenseDao.class, UserServiceImpl.class,
		UserDao.class, TokenServiceImpl.class, TokenDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class ExpenseServiceTest {

	@Autowired
	private ExpenseService<Long> expenseService;

	@Autowired
	private UserService<Long> userService;

	@Before
	public void testCreateExpense() {
		// Create users
		User user1 = new User("John", "Snow", "john@hotmail.com", "admin", Arrays.asList(UserRole.ADMIN), true);
		userService.createUser(user1);

		User user2 = new User("Andres", "Iniesta", "iniesta@hotmail.com", "barcelona",
				Arrays.asList(UserRole.USER_MANAGER), true);
		userService.createUser(user2);

		User user3 = new User("Lionel", "Messi", "messi@hotmail.com", "thiago", Arrays.asList(UserRole.REGULAR_USER),
				true);
		userService.createUser(user3);

		// Create expenses
		// Expenses associated with user1
		Expense expense1 = new Expense(1200.0, 1L, "Travel", "Going to UK");
		expense1.setUser(user1);
		boolean expectedCheck = true;
		boolean actualCheck = expenseService.createExpense(expense1);
		Assert.assertEquals(expectedCheck, actualCheck);

		Expense expense2 = new Expense(40.0, 5L, "Meal", "Burger King");
		expense2.setUser(user1);
		actualCheck = expenseService.createExpense(expense2);
		Assert.assertEquals(expectedCheck, actualCheck);

		// Expenses associated with user2
		Expense expense3 = new Expense(500.0, 10L, "Taxes", "NY Tax");
		expense3.setUser(user2);
		actualCheck = expenseService.createExpense(expense3);
		Assert.assertEquals(expectedCheck, actualCheck);

		Expense expense4 = new Expense(10.0, 17L, "Interest", "Interest");
		expense4.setUser(user2);
		actualCheck = expenseService.createExpense(expense4);
		Assert.assertEquals(expectedCheck, actualCheck);

		// Expenses associated with user3
		Expense expense5 = new Expense(1500.0, 25L, "Rent", "Brooklyn apartement");
		expense5.setUser(user3);
		actualCheck = expenseService.createExpense(expense5);
		Assert.assertEquals(expectedCheck, actualCheck);
	}

	@Test
	public void testCreateExpenseWithInvalidDescription() {
		User user = new User("Mickel", "Jordan", "mickel@hotmail.com", "newPassword", Arrays.asList(UserRole.ADMIN),
				true);
		userService.createUser(user);

		Expense expense = new Expense(1500.0, 25L, null, "Brooklyn apartement");
		expense.setUser(user);
		boolean expectedCheck = false;
		boolean actualCheck = expenseService.createExpense(expense);
		Assert.assertEquals(expectedCheck, actualCheck);
	}

	@Test
	public void testGetAllExpenses() {
		List<Expense> expenseList = expenseService.getAllExpenses();
		int expectedExpenses = 5;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);
	}

	@Test
	public void testGetByAmount() {
		List<Expense> expenseList = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().endTime(13L).build());
		int expectedExpenses = 3;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);
	}

	@Test
	public void testUpdateExpense() {
		Expense expense = expenseService.getAllExpenses().get(2);
		Expense updatedExpense = copyExpense(expense);
		updatedExpense.setComment("New Comment");

		boolean expectedCheck = true;
		boolean actualCheck = expenseService.updateExpense(updatedExpense);
		Assert.assertEquals(expectedCheck, actualCheck);
	}

	@Test
	public void testUpdateExpenseWithNullTime() {
		Expense expense = expenseService.getAllExpenses().get(2);
		Expense updatedExpense = copyExpense(expense);
		updatedExpense.setTime(null);

		boolean expectedCheck = false;
		boolean actualCheck = expenseService.updateExpense(updatedExpense);
		Assert.assertEquals(expectedCheck, actualCheck);
	}

	@Test
	public void testDeleteExpense() {
		Long expenseId = expenseService.getAllExpenses().get(2).getId();

		Expense removedExpense = expenseService.deleteExpense(expenseId);
		Assert.assertNotNull(removedExpense);

		List<Expense> expenseList = expenseService.getAllExpenses();
		int expectedExpenses = 4;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);

		// Test invalid delete
		Expense invalidExpense = expenseService.deleteExpense(null);
		Assert.assertNull(invalidExpense);
	}

	@Test
	public void testGetExpense() {
		Long expenseId = expenseService.getAllExpenses().get(2).getId();
		Expense expense = expenseService.getExpense(expenseId);
		String expectedDesc = "Taxes";
		Assert.assertEquals(expectedDesc, expense.getDescription());
	}

	@Test
	public void testGetExpensesByUserAndBetweenDates() {
		Long userId = userService.getAllUsers().get(0).getId();
		List<Expense> expenseList = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().userId(userId).startTime(4L).endTime(8L).build());

		int expectedExpenses = 1;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);

		String expectedDesc = "Meal";
		String actualDest = expenseList.get(0).getDescription();
		Assert.assertEquals(expectedDesc, actualDest);
	}

	@Test
	public void testGetExpensesBetweenDates() {
		List<Expense> expenseList = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().startTime(1L).endTime(18L).build());

		int expectedExpenses = 4;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);
	}

	@Test
	public void testGetExpensesByAmount() {
		List<Expense> expenseList = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().minAmount(1.0).maxAmount(500.0).build());

		int expectedExpenses = 3;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);
	}

	@Test
	public void testGetUserExpenses() {
		Long userId = userService.getAllUsers().get(2).getId();
		List<Expense> expenseList = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().userId(userId).build());

		int expectedExpenses = 1;
		int actualExpenses = expenseList.size();
		Assert.assertEquals(expectedExpenses, actualExpenses);

		// Test invalid userId
		List<Expense> invalidList2 = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().userId(-1L).build());
		Assert.assertTrue(invalidList2.isEmpty());
	}

	private Expense copyExpense(Expense expense) {
		Expense newExpense = new Expense(expense.getAmount(), expense.getTime(), expense.getDescription(),
				expense.getComment());
		newExpense.setId(expense.getId());
		newExpense.setVersion(expense.getVersion());
		return newExpense;
	}
}
