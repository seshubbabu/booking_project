package com.example.booking_project.workspace.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 17, 2023
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdminDashboardDetailsResponse {

	private String bookingId;
	private String bookingType;
	private String bookingDate;
	private String userType;
	private String workspaceCodes;
	private String locationCode;
	private String floorNo;
	private String twoWheelerLots;
	private String fourWheelerLots;
	private String workspaceType;
	private String employeeId;
	private String employeeName;
	private String city;
	private String country;
	private int noOfLunch;
	private int noOfDinner;
	@JsonIgnore
	private int totalNoOfRecords;

}
