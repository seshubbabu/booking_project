package com.example.booking_project.workspace.repository;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceLocationFlatResponseDTO {

	private BigInteger locationId;
	private String locationCode;
	private String locationName;
	private String floorNo;
	private String workspaceName;
	private String workspaceType;
	private String workspaceTypeCode;
	private String city;
	private String state;
	private String country;
}
