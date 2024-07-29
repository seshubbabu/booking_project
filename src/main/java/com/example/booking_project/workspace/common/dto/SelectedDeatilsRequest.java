package com.example.booking_project.workspace.common.dto;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
public class SelectedDeatilsRequest {

	@JsonProperty("employeeId")
	@NotBlank(message = "Booking Employee Id cannot not be Blank")
	private String employeeId;

	@JsonProperty("employeeName")
	@NotBlank(message = "Booking Employee Name cannot not be Blank")
	private String employeeName;

	@JsonProperty("workspaceCode")
	private String workspaceCode;

	@JsonProperty("bookingDate")
	@NotBlank(message = "Booking Date cannot not be Blank")
	private String bookingDate;

	@JsonProperty("noOfLunch")
	private int noOfLunch;

	@JsonProperty("noOfDinner")
	private int noOfDinner;

	@JsonProperty("noOfTwoWheeler")
	private int noOfTwoWheeler;

	@JsonProperty("noOfFourWheeler")
	private int noOfFourWheeler;

	@JsonProperty("twoWheelerLot")
	private String twoWheelerLot;

	@JsonProperty("fourWheelerLot")
	private String fourWheelerLot;
}
