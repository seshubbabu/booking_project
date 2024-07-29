package com.example.booking_project.workspace.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 09, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class BookingAndMealDetailDTO {

	@JsonProperty("employeeId")
	@NotBlank(message = "Booking Employee Id cannot not be Blank")
	private String employeeId;

	@JsonProperty("employeeName")
	@NotBlank(message = "Booking Employee Name cannot not be Blank")
	private String employeeName;

	@JsonProperty("workspaceCodes")
	private List<String> workspaceCodes;

	@JsonProperty("noOfLunch")
	private int noOfLunch;

	@JsonProperty("noOfDinner")
	private int noOfDinner;
}
