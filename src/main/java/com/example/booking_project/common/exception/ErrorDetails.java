package com.example.booking_project.common.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("detail")
    private String customMessage;

    @JsonProperty("subErrors")
    List<SubError> subErrors;

}
