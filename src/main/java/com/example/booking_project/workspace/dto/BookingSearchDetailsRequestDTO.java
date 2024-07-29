package com.example.booking_project.workspace.dto;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *  * @author Parandhamaiah.Naidu
 *
 */
@Data
@ToString(includeFieldNames = true)
public class BookingSearchDetailsRequestDTO {

	@JsonProperty("employeeName")
	@NotBlank(message = "Employee Name cannot not be Blank")
	private String employeeName;

	@JsonProperty("requestDate")
	@NotBlank(message = "Request Date cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String requestDate;
	
	@JsonProperty("locationCode")
	@NotBlank(message = "Location Code cannot not be Blank")
	private String locationCode;
	
	@JsonProperty("action")
	//@NotBlank(message = "action cannot not be Blank")
	private String action;
}
