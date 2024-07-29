package com.example.booking_project.workspace.dto;

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
public class WorkspaceMasterDetails {

	@JsonProperty("workspaceCode")
	private String workspaceCode;

	@JsonProperty("workspaceType")
	private String workspaceType;

	@JsonProperty("workspaceDesc")
	private String workspaceDesc;

	@JsonProperty("localtionCode")
	private String localtionCode;

	@JsonProperty("floorNo")
	private String floorNo;

	@JsonProperty("city")
	private String city;

	@JsonProperty("country")
	private String country;

	@JsonProperty("status")
	private Boolean status;

}
