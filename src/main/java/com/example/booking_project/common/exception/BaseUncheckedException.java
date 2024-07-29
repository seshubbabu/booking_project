package com.example.booking_project.common.exception;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */
public class BaseUncheckedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7861807674120890343L;

	private String errorCode;
	private String errorMessage;

	public BaseUncheckedException(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}