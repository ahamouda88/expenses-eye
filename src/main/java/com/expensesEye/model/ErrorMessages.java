package com.expensesEye.model;

import org.springframework.stereotype.Component;

@Component
// @PropertySource("classpath:error-messages.properties")
public class ErrorMessages {

	// Dynamic Messages
	public static final String INVALID_USER_ID = "User with id [%d], doesn't exist";
	public static final String INVALID_USER_EMAIL = "User with email [%s], doesn't exist";
	public static final String INVALID_EXPENSE_ID = "Expense with id [%d], doesn't exist";
	public static final String INVALID_EXPENSE_USER = "User for expense id [%d] is invalid";
	public static final String ALREADY_EXISTS_EMAIL = "User with the following email [%s] already exists";
	public static final String INVALID_EXPENSE_SEARCH_REQUEST = "Expense Search Request [%s] is invalid";
	public static final String INVALID_USER_SEARCH_REQUEST = "User Search Request [%s] is invalid";
	public static final String INVALID_NUMBER_USER_RESULT = "More than one user matchs the following Search Request [%s]";
	public static final String FAILED_USER_LOGIN = "Failed to login user with email [%s]";
	public static final String FAILED_USER_BLOCKED_ACCOUNT = "User's account is blocked because of failing to login [%d] times";

	// Static Messages
	public static final String FAILED_CREATE_USER = "Failed to create user";
	public static final String FAILED_UPDATE_USER = "Failed to update user";
	public static final String FAILED_CREATE_EXPENSE = "Failed to create expense";
	public static final String FAILED_UPDATE_EXPENSE = "Failed to update expense";
	public static final String FAILED_GET_ALL_USERS = "Failed to retrieve users";
	public static final String FAILED_GET_ALL_EXPENSES = "Failed to retrieve expenses";
	public static final String INVALID_REQUEST = "Invalid request";
	public static final String INVALID_PARAM_EMAIL = "Email param [%s] is invalid";
	public static final String INVALID_VERIFICATION_TOKEN = "Invalid verification token: [%s]";
	public static final String FAILED_SEND_EMAIL = "Failed to send email";
	public static final String FAILED_VERIFY_USER = "Failed to verify user";
	public static final String FILE_SIZE_EXCEEDED_LIMIT = "File size exceeded the limit [%d]";

	public static final String FAILED_CREATE_VERIFICATION_TOKEN = "Failed to create verification token";
	public static final String FAILED_GET_VERIFICATION_TOKEN = "Failed to retrieve verification token for user Id [%d]";
	public static final String FAILED_EXPIRED_VERIFICATION_TOKEN = "Failed to verify user: Expired verification token";

	public static final String FAILDED_PARSE_RESPONSE = "Failed to parse response to a map object";

	/* Social Media Provider */
	public static final String FAILED_PROVIDER_LOGIN = "Failed login using social media provider";
	public static final String FAILED_PROVIDER_GET_IMAGE = "Failed to retrieve image given the url";
	public static final String FAILED_PROVIDER_GET_USER = "Failed to retrieve user using his social media account";
	public static final String FAILED_PROVIDER_GET_TOKEN = "Failed to retrieve access token when logging using social media provider";
}
