package com.example.booking_project.common.exception;

import java.util.Map;


public class NoSufficientParkingLotsException extends BaseUncheckedException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Map<String, String> businessErrorList;

    public NoSufficientParkingLotsException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public NoSufficientParkingLotsException(String errorCode, String errorMessage,
                                            Map<String, String> businessErrorList) {
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