package com.example.booking_project.workspace.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * 
 * @author Parandhamaiah.Naidu
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceLocationResponseDTO {

	private String locationCode;
	private String locationName;
	private String city;
	private String state;
	private String country;
	private Map<String, WorkstationTypesDTO> workstationTypes;
	private Map<String, ODCSDTO> odcs;

}
