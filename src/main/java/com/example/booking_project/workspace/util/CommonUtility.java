package com.example.booking_project.workspace.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Component
public class CommonUtility {

	private static Logger logger = LoggerFactory.getLogger(CommonUtility.class);

	@Autowired
	private ObjectMapper objectMapper;

	public String responseToString(Object response) {
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			logger.error("CommonUtility :: responseToString : " + e.getMessage());
		}
		return null;
	}

	public static Date stringToSQLDate(String dateStr) {
		SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
		java.util.Date targetDate = null;
		try {
			targetDate = sourceFormat.parse(dateStr);
		} catch (ParseException e) {
			logger.error("CommonUtility :: responseToString : " + e.getMessage());
		}
		return new Date(targetDate.getTime());
	}

	public static String sQLDateToString(Date sqlDate) {
		LocalDate date = sqlDate.toLocalDate();
		DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return dtformat.format(date);
	}
}
