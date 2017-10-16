package com.expensesEye.security.permission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.expensesEye.model.CurrentUserDetails;
import com.expensesEye.model.request.ExpenseSearchRequest;
import com.expensesEye.persist.entity.Expense;
import com.expensesEye.rest.controller.ExpenseController;
import com.expensesEye.service.ExpenseService;

/**
 * This Expense permission provider secures the actions being performed on the
 * {@link ExpenseController}
 */
@Component
public class ExpensePermissionProvider extends PermissionProvider {

	@Autowired
	private ExpenseService<Long> expenseService;

	@Override
	public boolean isAuthorized(Authentication authentication, Object permission, Object... objectsToTarget) {
		if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUserDetails)
				|| !(permission instanceof String))
			return false;

		if (notAValidAccount(authentication))
			return false;

		// Return false since we are expecting an expense object or an expense id
		if (objectsToTarget == null || objectsToTarget.length == 0)
			return false;

		switch (String.valueOf(permission)) {
		case "checkExpenseByUser":
			// If returning an expense by giving a user id, check if the current user id is
			// equal to the given user id
			return (objectsToTarget[0] instanceof Long) ? isSameUser(authentication, (long) objectsToTarget[0]) : false;
		case "checkExpense":
			// If updating an existing expense, then check if the expense being updated is
			// one of the current user's expenses
			return hasExpenseAccess(authentication, objectsToTarget[0]);
		case "checkExpenseId":
			// If updating/deleting an existing expense using the expense Id, then check if
			// the expense being updated is one of the current user's expenses
			return (objectsToTarget[0] instanceof Long)
					? hasExpenseAccessById(authentication, (long) objectsToTarget[0])
					: false;
		default:
			return false;
		}
	}

	private boolean hasExpenseAccess(Authentication authentication, Object expenseObj) {
		if (!(expenseObj instanceof Expense))
			return false;

		Expense expense = (Expense) expenseObj;
		return hasExpenseAccessById(authentication, expense.getId());
	}

	private boolean hasExpenseAccessById(Authentication authentication, long expenseId) {
		CurrentUserDetails currentUser = (CurrentUserDetails) authentication.getPrincipal();
		List<Expense> userExpenses = expenseService
				.getFilteredExpenses(ExpenseSearchRequest.builder().userId(currentUser.getUser().getId()).build());

		if (userExpenses == null)
			return false;

		return userExpenses.stream().anyMatch(expense -> expense.getId() == expenseId);
	}

	@Override
	public Set<Object> getSupportedPermissions() {
		Set<Object> permissions = new HashSet<>();
		permissions.add("checkExpenseByUser");
		permissions.add("checkExpense");
		permissions.add("checkExpenseId");
		return permissions;
	}

}
