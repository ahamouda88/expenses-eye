package com.expensesEye.model.exception;

import com.expensesEye.model.ErrorMessages;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException(String email) {
		super(String.format(ErrorMessages.ALREADY_EXISTS_EMAIL, email));
	}

}
