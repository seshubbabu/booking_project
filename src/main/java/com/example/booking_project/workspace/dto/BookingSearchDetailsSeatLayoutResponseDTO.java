package com.example.booking_project.workspace.dto;

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
public class BookingSearchDetailsSeatLayoutResponseDTO {
	
	private String locationCode;
	private WorkspaceDetailsSeatDTO workspaceDetails;
	
	
}
