package com.example.booking_project.workspace.dto;

import java.util.List;

import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 18, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class BookingCancellationDetailsRequestDTO {

	@JsonProperty("bookingDate")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String bookingDate;

	@JsonProperty("workspaceCode")
	private List<String> workspaceCodes;
}
