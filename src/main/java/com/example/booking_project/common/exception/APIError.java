package com.example.booking_project.common.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Builder
@Data
public class APIError {

	@JsonProperty("apiSubErrors")
	List<SubError> apiSubErrors;
}
