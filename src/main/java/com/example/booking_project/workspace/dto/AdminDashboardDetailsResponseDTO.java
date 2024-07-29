package com.example.booking_project.workspace.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 17, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class AdminDashboardDetailsResponseDTO {

	private int totalNoOfRecords;
	private List<AdminDashboardDetailsResponse> bookingDetails;

}
