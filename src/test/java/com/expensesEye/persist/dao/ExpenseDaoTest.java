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
import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.persist.entity.User;
import com.expensesEye.persist.entity.UserRole;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = { SpringBootConfig.class, ExpenseDao.class, UserDao.class })
@EntityScan(basePackages = "com.expensesEye.persist.entity")
public class ExpenseDaoTest {

	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private UserDao userDao;

	@Before
	public void testCreateExpense() {

		User user = new User("Quynh", "To Tuan", "quynh@hotmail.com", "nice", Arrays.asList(UserRole.ADMIN));
		userDao.create(user);
		boolean expectedCheck = true;

		// Create Expense 1
		Expense expense = new Expense(100.10, 123L, "Chinese Food", "Awesome food");
		expense.setUser(user);

		boolean actualCheck = expenseDao.create(expense);
		Assert.assertEquals(expectedCheck, actualCheck);

		// Create Expense 2
		Expense expense2 = new Expense(500.0, 169L, "AirBnb", "Not a bad place");
		expense2.setUser(user);

		actualCheck = expenseDao.create(expense2);
		Assert.assertEquals(expectedCheck, actualCheck);

		// Create Expense 3
		Expense expense3 = new Expense(1200.0, 900L, "Rent", "Great Location");
		expense3.setUser(user);

		actualCheck = expenseDao.create(expense3);
		Assert.assertEquals(expectedCheck, actualCheck);

		List<Expense> expenses = new ArrayList<>();
		expenses.add(expense);
		expenses.add(expense2);
		expenses.add(expense3);
		user.setExpenses(expenses);
	}

	@Test
	public void testUpdateExpense() {
		List<Expense> allExpenses = expenseDao.getAll();
		long expenseId = allExpenses.get(1).getId();
		Expense expense = expenseDao.find(expenseId);
		expense.setComment("My new comment");

		boolean expected = true;
		boolean actualResult = expenseDao.update(expense);
		Assert.assertEquals(expected, actualResult);

		Expense updatedExpense = expenseDao.find(expenseId);
		String expectedComment = "My new comment";
		Assert.assertEquals(expectedComment, updatedExpense.getComment());
	}

	@Test
	public void testRemoveExpense() {
		List<Expense> allExpenses = expenseDao.getAll();
		long expenseId = allExpenses.get(1).getId();
		Expense expense = expenseDao.find(expenseId);

		Expense removedExpense = expenseDao.remove(expense);
		// Should remove expense from parent's collection
		removedExpense.getUser().getExpenses().remove(removedExpense);

		Assert.assertNotNull(removedExpense);

		// Test size of the list after removing a expense
		List<Expense> newExpenseList = expenseDao.getAll();

		int expectedSize = 2;
		Assert.assertEquals(expectedSize, newExpenseList.size());
	}

	@Test
	public void testFindExpense() {
		// Test total number of expenses
		List<Expense> allExpenses = expenseDao.getAll();
		int expectedSize = 3;
		Assert.assertEquals(expectedSize, allExpenses.size());

		// Test the find method
		Expense expense = expenseDao.find(allExpenses.get(0).getId());
		String expectedComment = "Awesome food";
		Assert.assertEquals(expectedComment, expense.getComment());
	}

	@Test
	public void testGetUserExpenses() {
		// Create a user
		User user = new User("Ahmed", "Hamouda", "ahmed@hotmail.com", "password", Arrays.asList(UserRole.ADMIN));

		// Create an expense
		Expense expense = new Expense(430.0, 1235L, "Rent", "No comment");
		expense.setUser(user);

		List<Expense> expenses = new ArrayList<>();
		expenses.add(expense);
		user.setExpenses(expenses);
		userDao.create(user);

		List<Expense> userExpenses = expenseDao
				.searchExpenses(ExpenseSearchRequest.builder().userId(user.getId()).build());
		int expectedSize = 1;
		Assert.assertEquals(expectedSize, userExpenses.size());

		Double expectedAmount = 430.0;
		Assert.assertEquals(expectedAmount, userExpenses.get(0).getAmount());
	}

	@Test
	public void testFindByAmount() {
		List<Expense> expenses = expenseDao.searchExpenses(ExpenseSearchRequest.builder().minAmount(120.0).build());
		User user = expenses.get(0).getUser();

		// Test the number of returned expenses
		int expectedSize = 2;
		Assert.assertEquals(expectedSize, expenses.size());

		// Test the user associated with the returned expense
		String expectedEmail = "quynh@hotmail.com";
		Assert.assertEquals(expectedEmail, user.getEmail());
	}

	@Test
	public void testFindByUser() {
		User user = userDao.find("quynh@hotmail.com", null, false);

		List<Expense> expenses = expenseDao.searchExpenses(ExpenseSearchRequest.builder().userId(user.getId()).build());

		// Test the number of returned expenses
		int expectedSize = 3;
		Assert.assertEquals(expectedSize, expenses.size());
	}

	@Test
	public void testFindBetweenDates() {
		List<Expense> expenses = expenseDao
				.searchExpenses(ExpenseSearchRequest.builder().startTime(125L).endTime(800L).build());

		// Test the number of returned expenses
		int expectedSize = 1;
		Assert.assertEquals(expectedSize, expenses.size());
	}

	@Test
	public void testFindByAmountUserAndBetweenDates() {
		// Test valid destination, user and date range
		User user = userDao.find("quynh@hotmail.com", null, false);
		ExpenseSearchRequest searchRequest = ExpenseSearchRequest.builder().minAmount(100.0).maxAmount(600.0)
				.startTime(150L).userId(user.getId()).build();
		List<Expense> expenses = expenseDao.searchExpenses(searchRequest);

		int expectedSize = 1;
		Assert.assertEquals(expectedSize, expenses.size());

		// Test invalid date range
		ExpenseSearchRequest invalidSearchRequest = ExpenseSearchRequest.builder().minAmount(10000.0).maxAmount(20000.0)
				.startTime(150L).userId(user.getId()).build();
		List<Expense> invalidExpenses = expenseDao.searchExpenses(invalidSearchRequest);

		expectedSize = 0;
		Assert.assertEquals(expectedSize, invalidExpenses.size());
	}

	@Test
	public void testInvalidFindBetweenDates() {
		ExpenseSearchRequest searchRequest = ExpenseSearchRequest.builder().startTime(901L).endTime(1000L).build();
		List<Expense> expenses = expenseDao.searchExpenses(searchRequest);

		// Test the number of returned expenses
		int expectedSize = 0;
		Assert.assertEquals(expectedSize, expenses.size());
	}
}
