/**
 * 
 */
package com.example.booking_project.workspace.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.UserType;

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
public class BookingRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5901128379119276456L;

	@JsonProperty("bookingId")
	private BigInteger bookingId;

	@JsonProperty("bookingType")
	@Enumerated(EnumType.STRING)
	private BookingType bookingType;

	@JsonProperty("requesterId")
	@NotBlank(message = "Booking requester Id cannot not be Blank")
	private String requesterId;

	@JsonProperty("requesterName")
	@NotBlank(message = "Booking requester Name cannot not be Blank")
	private String requesterName;

	@JsonProperty("requestedDate")
	@NotBlank(message = "requestedDate cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String requestedDate;

	@JsonProperty("requestedFor")
	@Enumerated(EnumType.STRING)
	private UserType requestedFor;

	@JsonProperty("bookingStartDate")
	@NotBlank(message = "bookingStartDate cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String bookingStartDate;

	@JsonProperty("bookingEndDate")
	@NotBlank(message = "bookingEndDate cannot not be Blank")
	@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private String bookingEndDate;

	@JsonProperty("workspaceType")
	private String workspaceType;

	@JsonProperty("departmentName")
	private String departmentName;

	@JsonProperty("divisionName")
	private String divisionName;

	@JsonProperty("locationCode")
	@NotBlank(message = "Location Code cannot not be Blank")
	private String locationCode;

	@JsonProperty("floorNo")
	private String floorNo;

	@JsonProperty("city")
	@NotBlank(message = "city cannot not be Blank")
	private String city;

	@JsonProperty("country")
	@NotBlank(message = "country cannot not be Blank")
	private String country;

	@JsonProperty("selectedDeatils")
	private Map<String, SelectedDeatilRequestDTO> selectedDeatils;

}
