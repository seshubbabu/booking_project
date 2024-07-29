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
public class WorkspaceStatisticsDetails {

	private int totalSeats;
	private int bookedSeats;
	private int blockedSeats;
	private int availableSeats;

}
