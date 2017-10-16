package com.expensesEye.constants;

/**
 * An interface that contains url path constants
 */
public interface PathConstants {

	public final static String SEARCH_PATH = "/search";
	public final static String ID_PATH = "/{id}";
	public final static String USER_ID_PATH = "/user/{userid}";

	// User Constants
	public final static String USERS_PATH = "/api/users";
	public final static String REGISTER_PATH = "/register";
	public final static String EMAIL_VERIFY_PATH = "/verify/{email:.+}";
	public final static String EMAIL_PATH = "/email/{email:.+}";
	public final static String CURRENT_USER = "/currentuser";
	public final static String NOT_ANY_PATH = "/notany";
	public final static String SEND_REQUEST_REGISTER_PATH = "/requestregister";

	// Expenses Constants
	public final static String EXPENSES_PATH = "/api/expenses";

	// Provider Authorization Constants
	public final static String AUTH_PATH = "/auth";
	public final static String FACEBOOK_PATH = "/facebook";
	public final static String GOOGLE_PATH = "/google";

}
