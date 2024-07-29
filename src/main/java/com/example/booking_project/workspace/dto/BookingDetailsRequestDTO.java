package com.example.booking_project.workspace.dto;

import com.example.booking_project.workspace.enums.BookingRequestType;
import com.example.booking_project.workspace.enums.BookingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 16, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class BookingDetailsRequestDTO {

	@JsonProperty("requestType")
	private BookingRequestType requestType;

	@JsonProperty("employeeId")
	@NotBlank(message = "Employee Id cannot not be Blank")
	private String employeeId;

	@JsonProperty("bookingType")
	private BookingType bookingType;

	@JsonProperty("requestDate")
	@NotBlank(message = "Request Date cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	private String requestDate;
	
	private String locationCode;

}
