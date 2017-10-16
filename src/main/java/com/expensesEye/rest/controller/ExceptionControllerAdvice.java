package com.expensesEye.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.expensesEye.model.ErrorResponse;

/**
 * This controller handles the global exceptions thrown from the application
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> runTimeExceptionHandler(RuntimeException ex) {
		HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
		List<String> errors = new ArrayList<>();
		errors.add(ex.getMessage());

		return new ResponseEntity<>(new ErrorResponse(httpStatus.value(), errors), httpStatus);
	}
}