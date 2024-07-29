package com.example.booking_project.workspace.dto;

import java.math.BigInteger;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
public class BookingCancellationRequestDTO {

	@JsonProperty("bookingId")
	@NotBlank(message = "Booking Id cannot not be Blank")
	private BigInteger bookingId;

	@JsonProperty("bookingDate")
	private List<@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$") String> bookingDate;

}
