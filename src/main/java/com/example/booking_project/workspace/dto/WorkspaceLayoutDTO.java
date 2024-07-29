package com.example.booking_project.workspace.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 12, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class WorkspaceLayoutDTO {

	@JsonIgnore
	private String locationCode;

	@JsonProperty("workspaceNo")
	private String workspaceCode;

	@JsonProperty("seatNo")
	private String seatNo;

	@JsonIgnore
	private String workspaceType;

	@JsonIgnore
	private String floorNo;

	@JsonProperty("status")
	private String workspaceStatus;

	private String employeeId;

	private String employeeName;

	private String departmentName;

	private String divisionName;
}
