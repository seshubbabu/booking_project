package com.example.booking_project.workspace.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

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
public class BookingResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8983806353617656566L;

	@JsonProperty("bookingId")
	private BigInteger bookingId;

	@JsonProperty("bookingType")
	private String bookingType;

	@JsonProperty("requesterId")
	@NotBlank(message = "Booking requester Id cannot not be Blank")
	private String requesterId;

	@JsonProperty("requesterName")
	@NotBlank(message = "Booking requester Name cannot not be Blank")
	private String requesterName;

	@JsonProperty("requestedDate")
	@NotBlank(message = "requestedDate cannot not be Blank")
	private String requestedDate;

	@JsonProperty("workspaceType")
	@NotBlank(message = "Workspace Type cannot not be Blank")
	private String workspaceType;

	@JsonProperty("divisionName")
	private String divisionName;

	@JsonProperty("departmentName")
	private String departmentName;

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

	@JsonProperty("allocatedDeatils")
	private Map<String, SelectedDeatilResponseDTO> allocatedDeatils;

}
