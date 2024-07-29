package com.example.booking_project.workspace.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 16, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class DayWiseBookingDetail {

	private String employeeId;
	private String employeeName;
	private int noOfLunch;
	private int noOfDinner;
	private String parkingType;
	private List<String> workspaceNos;
	private List<String> twoWheelerSlots;
	private List<String> fourWheelerSlots;
	private String status;

}
