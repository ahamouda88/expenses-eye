package com.expensesEye.service.impl;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.dao.ExpenseDao;
import com.expensesEye.persist.dao.UserDao;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.persist.entity.User;
import com.expensesEye.service.ExpenseService;

/**
 * Implementation of {@link ExpenseService}
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService<Long> {
	private static Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);

	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private UserDao userDao;

	@Override
	public boolean createExpense(Expense expense) {
		if (!isValid(expense) || expense.getUser() == null || expense.getUser().getId() == null)
			return false;

		long userId = expense.getUser().getId();
		User user = userDao.find(null, userId, true);
		if (user == null) {
			logger.error(String.format(ErrorMessages.INVALID_USER_ID, userId));
			return false;
		}

		List<Expense> expenses = user.getExpenses();
		if (expenses == null)
			expenses = new ArrayList<>();
		expenses.add(expense);

		user.setExpenses(expenses);
		return true;
	}

	@Override
	public boolean updateExpense(Expense expense) {
		if (!isValid(expense) || expense.getId() == null)
			return false;

		return expenseDao.update(expense);
	}

	@Override
	public Expense deleteExpense(Long expenseId) {
		Expense expense = this.getExpense(expenseId);

		if (expense == null) {
			logger.error(String.format(ErrorMessages.INVALID_EXPENSE_ID, expenseId));
			return null;
		}

		Expense removedExpense = expenseDao.remove(expense);

		if (removedExpense == null)
			return null;
		// Remove expense from the parent association (user)
		removedExpense.getUser().getExpenses().remove(removedExpense);

		return removedExpense;
	}

	@Override
	public Expense getExpense(Long expenseId) {
		return expenseId == null ? null : expenseDao.find(expenseId);
	}

	@Override
	public List<Expense> getAllExpenses() {
		return expenseDao.getAll();
	}

	@Override
	public List<Expense> getFilteredExpenses(ExpenseSearchRequest searchRequest) {
		if (searchRequest == null)
			return null;
		return expenseDao.searchExpenses(searchRequest);
	}

	private boolean isValid(Expense expense) {
		return expense != null && expense.getTime() != null && expense.getAmount() != null && expense.getAmount() > -1
				&& !isEmpty(expense.getDescription());
	}
}
