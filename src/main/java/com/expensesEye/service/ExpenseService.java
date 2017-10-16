package com.expensesEye.service;

import java.io.Serializable;
import java.util.List;

import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.dao.ExpenseDao;
import com.expensesEye.persist.entity.Expense;

/**
 * An interface that defines the operations to be performed on an
 * {@link Expense} model, and it should interacts with the Expense's Dao Layer
 * {@link ExpenseDao}
 */
public interface ExpenseService<T extends Serializable> {

	/**
	 * This method creates an {@link Expense} model, given the Expense model
	 * 
	 * @param Expense
	 *            the expense that need to be created
	 * @return true if expense is created successfully, or false otherwise
	 */
	public boolean createExpense(Expense expense);

	/**
	 * This method updates an {@link Expense} model, given the Expense model
	 * 
	 * @param expense
	 *            the expense that need to be updated
	 * @return true if expense is updated successfully, or false otherwise
	 */
	public boolean updateExpense(Expense expense);

	/**
	 * This method removes an {@link Expense} model, based on the given expense's id
	 * 
	 * @param expenseId
	 *            an expense's Id
	 * @param <T>
	 *            the expense's Id data type
	 * @return the removed {@link Expense} model if expense exists, otherwise it
	 *         will return <b>null</b>
	 */
	public Expense deleteExpense(T expenseId);

	/**
	 * This method returns an {@link Expense} model, based on the given expense's id
	 * 
	 * @param expenseId
	 *            an expense's Id
	 * @param <T>
	 *            the expense's Id data type
	 * @return a {@link Expense} model if expense exists, otherwise it will return
	 *         <b>null</b>
	 */
	public Expense getExpense(T expenseId);

	/**
	 * This method returns a list of all expenses
	 * 
	 * @return a list of expenses
	 */
	public List<Expense> getAllExpenses();

	/**
	 * This method filters expenses based on to the given
	 * {@link ExpenseSearchRequest}
	 * 
	 * @param searchRequest
	 *            an {@link ExpenseSearchRequest} object
	 * @return list of expenses if exists, otherwise it will return <b>null</b>
	 */
	public List<Expense> getFilteredExpenses(ExpenseSearchRequest searchRequest);
}
