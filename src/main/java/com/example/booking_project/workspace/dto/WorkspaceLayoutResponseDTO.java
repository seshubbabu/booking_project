package com.example.booking_project.workspace.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 21, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class WorkspaceLayoutResponseDTO {

	@JsonProperty("account")
	private String account;

	@JsonProperty("locationCode")
	@NotBlank(message = "LocationCode cannot not be Blank")
	private String locationCode;

	@JsonProperty("workspaceType")
	@NotBlank(message = "WorkspaceType cannot not be Blank")
	private String workspaceType;

	@JsonProperty("floorNo")
	@NotBlank(message = "floorNo cannot not be Blank")
	private String floorNo;

	@JsonProperty("workspaceDetails")
	@NotBlank(message = "workspaceDetails cannot not be Blank")
	Map<String, List<WorkspaceLayoutDTO>> workspaceDetails;

}
