package com.example.booking_project.workspace.dto;

import jakarta.validation.constraints.NotBlank;

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
public class WorkspaceLayoutDetails {

	@JsonProperty("workspaceNo")
	@NotBlank(message = "workspaceNo cannot not be Blank")
	private String workspaceNo;

	@JsonProperty("seatNo")
	@NotBlank(message = "seatNo cannot not be Blank")
	private String seatNo;

	@JsonProperty("status")
	@NotBlank(message = "status cannot not be Blank")
	private String status;

	@JsonProperty("employeeId")
	@NotBlank(message = "employeeId cannot not be Blank")
	private String employeeId;

	@JsonProperty("employeeName")
	@NotBlank(message = "employeeName cannot not be Blank")
	private String employeeName;

	@JsonProperty("department")
	private String department;

	@JsonProperty("division")
	private String division;

	@JsonProperty("image")
	private Object image;
}
