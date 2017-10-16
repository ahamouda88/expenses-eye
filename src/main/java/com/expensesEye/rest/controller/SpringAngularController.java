package com.expensesEye.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.expensesEye.constants.PageConstants;

/**
 * This controller class returns the name of the view file corresponding to
 * every angular url
 */
@Controller
public class SpringAngularController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String appPage() {
		return PageConstants.APP_PAGE;
	}

	@RequestMapping(value = "/all/view/home", method = RequestMethod.GET)
	public String homePage() {
		return PageConstants.HOME_PAGE;
	}

	// This method is for the loginPage in the security config
	@RequestMapping(value = { "/all/view/login", "/logout" }, method = RequestMethod.GET)
	public String loginPage() {
		return PageConstants.LOGIN_PAGE;
	}

	@RequestMapping(value = "/all/view/user", method = RequestMethod.GET)
	public String userPage() {
		return PageConstants.USER_PAGE;
	}

	@RequestMapping(value = "/usrmgr/view/users", method = RequestMethod.GET)
	public String allUsersPage() {
		return PageConstants.ALL_USERS_PAGE;
	}

	@RequestMapping(value = "/usr/view/expense", method = RequestMethod.GET)
	public String expensePage() {
		return PageConstants.EXPENSE_PAGE;
	}

	@RequestMapping(value = "/usr/view/expenses", method = RequestMethod.GET)
	public String allExpensesPage() {
		return PageConstants.ALL_EXPENSES_PAGE;
	}

	@RequestMapping(value = "/usr/view/userDetails", method = RequestMethod.GET)
	public String userDetailsPage() {
		return PageConstants.USER_DETAILS_PAGE;
	}

	@RequestMapping(value = "/usr/view/expenses/week-expenses", method = RequestMethod.GET)
	public String thisWeekExpenses() {
		return PageConstants.WEEK_EXPENSES_PAGE;
	}
}
