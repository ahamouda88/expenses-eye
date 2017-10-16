package com.expensesEye.model.exception;

public class UserRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserRegistrationException(String message) {
		super(message);
	}
}
