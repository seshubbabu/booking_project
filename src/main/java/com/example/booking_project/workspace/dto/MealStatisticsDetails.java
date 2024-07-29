package com.example.booking_project.workspace.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 17, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class MealStatisticsDetails {

	private int totalNoOfLunch;
	private int totalNoOfDinner;

}
