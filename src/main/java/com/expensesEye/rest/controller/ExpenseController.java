package com.expensesEye.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensesEye.constants.PathConstants;
import com.expensesEye.model.ErrorMessages;
import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.service.ExpenseService;
import com.expensesEye.utils.ResponseFactoryUtils;

/**
 * This class is a Rest Controller for handling the expense actions, and
 * handling the expense end-points
 */
@RestController
@RequestMapping(value = PathConstants.EXPENSES_PATH)
public class ExpenseController {

	@Autowired
	private ExpenseService<Long> expenseService;

	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ADMIN', 'REGULAR_USER')")
	public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
		boolean inserted = expenseService.createExpense(expense);

		expense = inserted ? expense : null;
		return ResponseFactoryUtils.createResponse(expense, HttpStatus.CREATED, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_CREATE_EXPENSE);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@PreAuthorize("hasPermission(#expense, 'checkExpense') or hasRole('ADMIN')")
	public ResponseEntity<?> updateExpense(@RequestBody Expense expense) {
		boolean updated = expenseService.updateExpense(expense);
		Long expenseId = expense == null ? -1 : expense.getId();

		expense = updated ? expense : null;
		return ResponseFactoryUtils.createResponse(expense, HttpStatus.OK, HttpStatus.BAD_REQUEST,
				ErrorMessages.FAILED_UPDATE_EXPENSE, String.format(ErrorMessages.INVALID_EXPENSE_USER, expenseId));
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasPermission(#expenseId, 'checkExpenseId') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteExpense(@RequestParam("id") Long expenseId) {
		return ResponseFactoryUtils.createResponse(expenseService.deleteExpense(expenseId), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, String.format(ErrorMessages.INVALID_EXPENSE_ID, expenseId));
	}

	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getAllExpenses() {
		return ResponseFactoryUtils.createResponse(expenseService.getAllExpenses(), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, ErrorMessages.FAILED_GET_ALL_EXPENSES);
	}

	@RequestMapping(value = PathConstants.USER_ID_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#userId, 'checkExpenseByUser') or hasRole('ADMIN')")
	public ResponseEntity<?> getAllExpensesByUser(@PathVariable(name = "userid") Long userId) {
		return this.getFilteredExpenses(userId, null, null, null, null);
	}

	@RequestMapping(value = PathConstants.ID_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#expenseId, 'checkExpenseId') or hasRole('ADMIN')")
	public ResponseEntity<?> getExpenseById(@PathVariable(name = "id") Long expenseId) {
		return ResponseFactoryUtils.createResponse(expenseService.getExpense(expenseId), HttpStatus.OK,
				HttpStatus.BAD_REQUEST, String.format(ErrorMessages.INVALID_EXPENSE_ID, expenseId));

	}

	@RequestMapping(value = PathConstants.SEARCH_PATH, method = RequestMethod.GET)
	@PreAuthorize("hasPermission(#userId, 'checkExpenseByUser') or hasRole('ADMIN')")
	public ResponseEntity<?> getFilteredExpenses(@RequestParam(name = "userId", required = false) Long userId,
			@RequestParam(name = "minAmount", required = false) Double minAmount,
			@RequestParam(name = "maxAmount", required = false) Double maxAmount,
			@RequestParam(name = "startTime", required = false) Long startTime,
			@RequestParam(name = "endTime", required = false) Long endTime) {
		ExpenseSearchRequest searchRequest = ExpenseSearchRequest.builder().maxAmount(maxAmount).minAmount(minAmount)
				.startTime(startTime).endTime(endTime).userId(userId).build();

		return ResponseFactoryUtils.createResponse(expenseService.getFilteredExpenses(searchRequest), HttpStatus.OK,
				HttpStatus.BAD_REQUEST,
				String.format(ErrorMessages.INVALID_EXPENSE_SEARCH_REQUEST, searchRequest.toString()));
	}
}
