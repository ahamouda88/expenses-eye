package com.expensesEye.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.expensesEye.model.ErrorResponse;

/**
 * A utility class that creates {@link BaseResponse} objects
 */
public final class ResponseFactoryUtils {

	private ResponseFactoryUtils() {
	}

	/**
	 * A method that creates a {@link ResponseEntity} given two {@link HttpStatus}
	 * one if it succeeded and the other if it failed
	 * 
	 * @param data
	 *            the data that will be returned in the response
	 * @param successStatus
	 *            the {@link HttpStatus} in-case it is a success
	 * @param failedStatus
	 *            the {@link HttpStatus} in-case it is a fail
	 * @param errorMessages
	 *            array of error messages in-case of a fail
	 * @return a {@link ResponseEntity} based on the given parameters
	 */
	public static <T> ResponseEntity<?> createResponse(T data, HttpStatus successStatus, HttpStatus failedStatus,
			String... errorMessages) {
		if (data == null) {
			return createFailResponse(failedStatus, errorMessages);
		} else {
			return createSuccessResponse(data, successStatus);
		}
	}

	/**
	 * A method that creates a success {@link ResponseEntity} given the success
	 * {@link HttpStatus} and the data to be sent
	 * 
	 * @param data
	 *            the data that will be returned in the response
	 * @param successStatus
	 *            the {@link HttpStatus} in-case it is a success
	 * @return a {@link ResponseEntity} based on the given parameters
	 */
	public static <T> ResponseEntity<?> createSuccessResponse(T data, HttpStatus successStatus) {
		return new ResponseEntity<>(data, successStatus);
	}

	/**
	 * A method that creates a fail {@link ResponseEntity} given the fail
	 * {@link HttpStatus} and the error messages
	 * 
	 * @param failedStatus
	 *            the {@link HttpStatus} in-case it is a fail
	 * @param errorMessages
	 *            array of error messages in-case of a fail
	 * @return a {@link ResponseEntity} based on the given parameters
	 */
	public static <T> ResponseEntity<?> createFailResponse(HttpStatus failedStatus, String... errorMessages) {
		return new ResponseEntity<>(new ErrorResponse(failedStatus.value(), errorMessages), failedStatus);
	}
}
