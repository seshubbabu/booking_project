package com.example.booking_project.workspace.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@Data
public class CommonExceptionResponseDTO {

	@JsonProperty("errorCode")
	private String errorCode;

	@JsonProperty("errorMessage")
	private String errorMessage;

	@JsonProperty("stackTrace")
	private Object stackTrace;
}
