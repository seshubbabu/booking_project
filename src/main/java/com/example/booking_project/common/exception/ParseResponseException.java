package com.example.booking_project.common.exception;

import lombok.Getter;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */

@Getter
public class ParseResponseException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2131213149509455903L;

	private APIError apiError;

	public ParseResponseException(String errorCode, String errorMessage, APIError apiError) {
		super(errorCode, errorMessage);
		this.apiError = apiError;

	}

}
