package com.example.booking_project.workspace.dto;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 7, 2023
 *
 */
@Data
@ToString(includeFieldNames = true)
public class BookingCancellationRespDetails {

	@JsonProperty("bookingDate")
	@NotBlank(message = "Booking Date cannot not be Blank")
	private String bookingDate;

	@JsonProperty("message")
	private String message;
}
