package com.example.booking_project.workspace.dto;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.example.booking_project.workspace.enums.WorkspaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
public class WorkspaceLayoutRequestDTO {

	@JsonProperty("account")
	private String account;

	@JsonProperty("locationCode")
	@NotBlank(message = "LocationCode cannot not be Blank")
	private String locationCode;

	@JsonProperty("workspaceType")
	@Enumerated(EnumType.STRING)
	private WorkspaceType workspaceType;

	@JsonProperty("requestedDate")
	private List<@Pattern(regexp = "^([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-([0-9][0-9])?[0-9][0-9]$") String> requestedDate;

	@JsonProperty("floorNo")
	@NotBlank(message = "floorNo cannot not be Blank")
	private String floorNo;

}
