package com.example.booking_project.common.exception;

import java.util.Map;

/**
 * 
 * @author Parandhamaiah.Naidu
 *
 */

public class LocationDetailsNotFoundExcpetion extends BaseUncheckedException {

	/**
	* 
	*/
	private static final long serialVersionUID = -3302917992612860700L;

	public LocationDetailsNotFoundExcpetion(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public LocationDetailsNotFoundExcpetion(String errorCode, String errorMessage, Map<String, String> businessErrorList) {
		super(errorCode, errorMessage);
		this.businessErrorList = businessErrorList;
	}

	private Map<String, String> businessErrorList;

	public Map<String, String> getBusinessErrorList() {
		return businessErrorList;
	}

	public void setBusinessErrorList(Map<String, String> businessErrorList) {
		this.businessErrorList = businessErrorList;
	}

}
