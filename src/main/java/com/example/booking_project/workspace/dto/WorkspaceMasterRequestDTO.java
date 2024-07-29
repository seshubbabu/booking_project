package com.example.booking_project.workspace.dto;

import java.util.List;

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
public class WorkspaceMasterRequestDTO {

	@JsonProperty("workspaceDetails")
	private List<WorkspaceMasterDetails> workspaceMasterRequestDetails;

}
