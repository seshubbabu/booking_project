package com.example.booking_project.workspace.util;

import java.util.Map;

import com.example.booking_project.common.exception.BaseUncheckedException;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 26, 2023
 */
public class NoDefaultPrefrenceException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	private Map<String, String> businessErrorList;

	public NoDefaultPrefrenceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoDefaultPrefrenceException(String errorCode, String errorMessage, Map<String, String> businessErrorList) {
		super(errorCode, errorMessage);
		this.businessErrorList = businessErrorList;
	}

	public Map<String, String> getBusinessErrorList() {
		return businessErrorList;
	}

	public void setBusinessErrorList(Map<String, String> businessErrorList) {
		this.businessErrorList = businessErrorList;
	}
}
