package com.example.booking_project.workspace.dto;

import java.math.BigInteger;
import java.util.List;

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
public class BookingCancellationResponseDTO {

	@JsonProperty("bookingId")
	@NotBlank(message = "Booking Id cannot not be Blank")
	private BigInteger bookingId;

	@JsonProperty("bookingDate")
	@NotBlank(message = "Booking Date cannot not be Blank")
	private List<BookingCancellationRespDetails> cancellationDetails;

}
