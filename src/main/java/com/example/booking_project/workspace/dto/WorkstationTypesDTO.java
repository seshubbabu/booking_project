package com.example.booking_project.workspace.dto;

import java.io.Serializable;
import java.util.List;

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
public class WorkstationTypesDTO implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -2247492987129340155L;

	private List<String> floors;

}
