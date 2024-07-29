package com.example.booking_project.common.exception;

import java.util.Map;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */

public class SeatAlreadyBookedException extends BaseUncheckedException {

    /**
     *
     */
    private static final long serialVersionUID = -3302917992612860700L;

    public SeatAlreadyBookedException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public SeatAlreadyBookedException(String errorCode, String errorMessage, Map<String, String> businessErrorList) {
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

