package com.example.booking_project.workspace.dto;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Parandhamaiah.Naidu
 *
 */
@Data
@ToString(includeFieldNames = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingSearchDetailsResponseDTO {

	private String employeeId;
	private String employeeName;
	
}
