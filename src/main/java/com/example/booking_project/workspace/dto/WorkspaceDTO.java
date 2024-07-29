package com.example.booking_project.workspace.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@Data
public class WorkspaceDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -2247492987129340155L;

	@JsonProperty("workspaceCodes")
	private List<String> workspaceCodes;

	@JsonProperty("floorNo")
	private String floorNo;

	@JsonProperty("type")
	private String type;
}
