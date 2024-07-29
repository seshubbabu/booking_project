package com.example.booking_project.workspace.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.booking_project.common.exception.ErrorDetails;

import lombok.Builder;
import lombok.Data;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@Data
@Builder
public class CommonResponseDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1247398160901982720L;

	@JsonProperty("status")
	private int status;

	@JsonProperty("message")
	private String message;

	@JsonProperty("data")
	private Object data;

	@JsonProperty("timeStamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timeStamp;

	@JsonProperty("errors")
	private ErrorDetails errors;

	public CommonResponseDTO() {
		super();
		this.timeStamp = LocalDateTime.now();
	}

	@Builder(builderMethodName = "commonResponseBuilder")
	public CommonResponseDTO(int status, String message, Object data, LocalDateTime timeStamp, ErrorDetails errors) {
		this.status = status;
		this.message = message;
		this.data = data;
		this.timeStamp = timeStamp;
		this.errors = errors;
	}

}
