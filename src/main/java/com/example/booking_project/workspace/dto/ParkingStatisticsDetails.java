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
public class ParkingStatisticsDetails {

	private int totalTwoWheelerSlots;
	private int totalFourWheelerSlots;
	private int bookedTwoWheelerSlots;
	private int bookedFourWheelerSlots;
	
}
