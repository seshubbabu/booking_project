package com.example.booking_project.common.exception;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */
public class BaseCheckedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3065586889453104421L;

	public BaseCheckedException() {
		super();
	}

	public BaseCheckedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BaseCheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseCheckedException(String message) {
		super(message);
	}

	public BaseCheckedException(Throwable cause) {
		super(cause);
	}

}
