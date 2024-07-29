package com.example.booking_project.workspace.common.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Sivasankar.Thalavai
 *
 *         Apr 3, 2023
 */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "transdata", schema = "dbo")
//@ToString(includeFieldNames = true)
public class CheckInDetails {

	private String employeeId;
	
	private Timestamp checkInTime;
	
	
}
