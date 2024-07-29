package com.example.booking_project.workspace.dto;

import java.math.BigInteger;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 24, 2023
 */
@Data
@ToString(includeFieldNames = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingDetailResponseDTO {

	private BigInteger bookingId;
	private String bookingType;
	private String requestType;
	private String requesterId;
	private String requesterName;
	private String requestedDate;
	private String bookingStartDate;
	private String bookingEndDate;
	private String locationCode;
	private String workspaceType;
	private String floorNo;
	private String city;
	private String country;
	private int totalNoOfSeats;
	private String previousBookedSeatStatus; 
    private int noOfSeatsAvailable;
    private int noOfTwoLotsAvailable;
    private int noOfFourLotsAvailable;
	private Map<String, SelectedDeatilResponseDTO> allocatedDeatils;
	
}
