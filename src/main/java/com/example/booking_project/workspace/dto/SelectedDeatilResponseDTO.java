package com.example.booking_project.workspace.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(includeFieldNames = true)
@AllArgsConstructor
@NoArgsConstructor
public class SelectedDeatilResponseDTO {

	@JsonProperty("employeeId")
	private String employeeId;

	@JsonProperty("employeeName")
	private String employeeName;

	@JsonProperty("workspaceCodes")
	private List<String> workspaceCodes;

	@JsonProperty("noOfLunch")
	private int noOfLunch;

	@JsonProperty("noOfDinner")
	private int noOfDinner;

	@JsonProperty("parkingType")
	private String parkingType;

	@JsonProperty("twoWheelerSlots")
	private List<String> twoWheelerSlots;

	@JsonProperty("fourWheelerSlots")
	private List<String> fourWheelerSlots;

	@JsonProperty("status")
	private String status;

	private int noOfSeats;
	private int noOfTwoWheelerSlots;
	private int noOfFourWheelerSlots;
}
