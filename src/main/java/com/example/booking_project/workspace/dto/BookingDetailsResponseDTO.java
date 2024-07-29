package com.example.booking_project.workspace.dto;

import java.math.BigInteger;

import lombok.*;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 24, 2023
 */
@Data
@ToString(includeFieldNames = true)
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingDetailsResponseDTO {

	private BigInteger bookingId;
	private String bookingType;
	private String bookingDate;
	private String requesterId;
	private String employeeId;
	private String employeeName;
	private String requesterName;
	private String requestedDate;
	private String workspaceType;
	private String locationCode;
	private String floorNo;
	private String city;
	private String country;
	private String workspaceCodes;
	private String twoWheelerSlots;
	private String fourWheelerSlots;
	private int noOfLunch;
	private int noOfDinner;
	private String status;
	private int noOfSeats;
	private String previousBookedSeatStatus; 
    private int noOfSeatsAvailable;
    private int noOfTwoLotsAvailable;
    private int noOfFourLotsAvailable;

}
