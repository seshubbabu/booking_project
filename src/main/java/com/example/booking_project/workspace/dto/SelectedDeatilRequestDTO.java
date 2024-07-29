package com.example.booking_project.workspace.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
public class SelectedDeatilRequestDTO {

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

	@JsonProperty("noOfTwoWheeler")
	private int noOfTwoWheeler;

	@JsonProperty("noOfFourWheeler")
	private int noOfFourWheeler;

	@JsonProperty("twoWheelerLots")
	private List<String> twoWheelerLots;

	@JsonProperty("fourWheelerLots")
	private List<String> fourWheelerLots;

}
