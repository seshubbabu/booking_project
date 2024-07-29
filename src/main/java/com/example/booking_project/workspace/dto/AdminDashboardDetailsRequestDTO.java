package com.example.booking_project.workspace.dto;

import java.util.List;

import com.example.booking_project.workspace.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 17, 2023
 */
@Data
@ToString(includeFieldNames = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class AdminDashboardDetailsRequestDTO {

	@JsonProperty("city")
	private String city;

	@JsonProperty("locationCode")
	private String locationCode;

	@JsonProperty("floorNo")
	private String floorNo;

	@JsonProperty("userType")
	private UserType userType;

	@JsonProperty("isAllEmployees")
	private boolean isAllEmployees;
	
	@JsonProperty("departmentId")
	private String departmentId;

	@JsonProperty("employeeIds")
	private List<String> employeeIds;

	@JsonProperty("fromDate")
	@NotBlank(message = "From Date cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String fromDate;

	@JsonProperty("toDate")
	@NotBlank(message = "To Date cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String toDate;

	@JsonProperty("pageNo")
	private int pageNo;

	@JsonProperty("records")
	private int records;

}
