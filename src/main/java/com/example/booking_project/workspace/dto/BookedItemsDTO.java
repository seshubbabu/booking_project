package com.example.booking_project.workspace.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 24, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class BookedItemsDTO {

	@Valid
	@JsonProperty("selectedWorkspaceNos")
	@NotNull(message = "workspaceNos cannot be null")
	private List<WorkspaceDTO> selectedWorkspaceNos;

	@Valid
	@JsonProperty("mealDetails")
	private MealDTO mealDetails;

	@Valid
	@JsonProperty("parkingDetails")
	private ParkingLotDTO parkingDetails;
}
