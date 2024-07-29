package com.example.booking_project.workspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@Data
@ToString(includeFieldNames = true)
public class MealDTO {

	@JsonProperty("noOfLunch")
	private int noOfLunch;
	
	@JsonProperty("noOfDinner")
	private int noOfDinner;
}
