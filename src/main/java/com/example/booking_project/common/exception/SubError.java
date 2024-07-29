package com.example.booking_project.common.exception;

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
public class SubError {

	@JsonProperty("subErrorCode")
	private String subErrorCode;

	@JsonProperty("subErrorMessage")
	private String subErrorMessage;

	@JsonProperty("subErrorTrace")
	private String subErrorTrace;
}
