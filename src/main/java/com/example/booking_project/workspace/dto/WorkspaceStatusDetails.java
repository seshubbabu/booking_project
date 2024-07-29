package com.example.booking_project.workspace.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 21, 2023
 */
@Data
@ToString(includeFieldNames = true)
public class WorkspaceStatusDetails {

	private String workspaceNo;
	private String seatNo;
	private String odcType;
	private String status;
	private String employeeId;
	private String employeeName;
	private String department;
	private String division;
	private Object image;

}
