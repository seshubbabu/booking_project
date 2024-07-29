package com.example.booking_project.common.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.util.NoDefaultPrefrenceException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final Pattern BOOKING_TYPE_ENUM_MSG_PATTERN = Pattern.compile("values accepted for Enum class: ");

	@ExceptionHandler({ SeatAlreadyBookedException.class })
	public ResponseEntity<CommonResponseDTO<?>> AlreadyBookedException(SeatAlreadyBookedException ex,
			HttpServletRequest request) {
		logger.error("Already Booked Exception : " + ex.getLocalizedMessage() + " for " + request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(
				CommonResponseDTO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.timeStamp(LocalDateTime.now())
						.errors(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage())
								.errorCode(ex.getErrorCode()).errorMessage(ex.getErrorMessage()).customMessage("")
								.subErrors(null != ex.getBusinessErrorList()
										? setBusinessErrorList(ex.getBusinessErrorList())
										: null)
								.build())
						.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ NoSufficientParkingLotsException.class })
	public ResponseEntity<CommonResponseDTO<?>> NoSufficientParkingLotsException(NoSufficientParkingLotsException ex,
			HttpServletRequest request) {
		logger.error("No Sufficient Parking Lots Exception : " + ex.getLocalizedMessage() + " for "
				+ request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(CommonResponseDTO.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).timeStamp(LocalDateTime.now())
				.errors(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage()).errorCode(ex.getErrorCode())
						.errorMessage(ex.getErrorMessage())
						.customMessage(
								"Expected Two Wheeler / Four Wheeler parking lot is not available. So please correct the parking details as per the below details")
						.subErrors(null != ex.getBusinessErrorList() ? setBusinessErrorList(ex.getBusinessErrorList())
								: null)
						.build())
				.build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ NoBookingFoundException.class })
	public ResponseEntity<CommonResponseDTO<?>> NoBookingFoundException(NoBookingFoundException ex,
			HttpServletRequest request) {
		logger.error("No BookingFound Exception : " + ex.getLocalizedMessage() + " for " + request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(CommonResponseDTO.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).timeStamp(LocalDateTime.now())
				.errors(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage()).errorCode(ex.getErrorCode())
						.errorMessage(ex.getErrorMessage()).customMessage("There is no bookings.")
						.subErrors(null != ex.getBusinessErrorList() ? setBusinessErrorList(ex.getBusinessErrorList())
								: null)
						.build())
				.build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ NoDefaultPrefrenceException.class })
	public ResponseEntity<CommonResponseDTO<?>> NoDefaultPrefrenceException(NoDefaultPrefrenceException ex,
			HttpServletRequest request) {
		logger.error("No DefaultPrefrenceException : " + ex.getLocalizedMessage() + " for " + request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(
				CommonResponseDTO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.timeStamp(LocalDateTime.now())
						.errors(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage())
								.errorCode(ex.getErrorCode()).errorMessage(ex.getErrorMessage()).customMessage("")
								.subErrors(null != ex.getBusinessErrorList()
										? setBusinessErrorList(ex.getBusinessErrorList())
										: null)
								.build())
						.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ ValidationException.class })
	public ResponseEntity<ErrorDetails> validationException(ValidationException ex, HttpServletRequest request) {
		logger.error("validation Exception : " + ex.getLocalizedMessage() + " for " + request.getRequestURI());
		return new ResponseEntity<>(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage()).errorCode(null)
				.customMessage("Request is not valid").build(), HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<CommonResponseDTO<?>> MethodArgumentNotValidException(Exception exception,
			HttpServletRequest request) {
		logger.error("MethodArgumentNotValidException : " + exception.getLocalizedMessage() + " for "
				+ request.getRequestURI());
		if (exception.getCause() != null && exception.getCause() instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException methArgexception = (MethodArgumentNotValidException) exception;
			return new ResponseEntity<CommonResponseDTO<?>>(
					CommonResponseDTO.builder().status(HttpStatus.BAD_REQUEST.value()).timeStamp(LocalDateTime.now())
							.errors(ErrorDetails.builder()
									.errorMessage(methArgexception.getFieldError().getField() + " - "
											+ methArgexception.getFieldError().getDefaultMessage())
									.errorCode(null).customMessage("Please check the request payload data").build())
							.build(),
					HttpStatus.BAD_REQUEST);
		} else if (exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
			HttpMessageNotReadableException invalidFormatException = (HttpMessageNotReadableException) exception;
			System.out.println("message: " + exception.getCause().getMessage());
			// Matcher match =
			// BOOKING_TYPE_ENUM_MSG_PATTERN.matcher(exception.getCause().getMessage());
			// if (match.matches()) {
			return new ResponseEntity<CommonResponseDTO<?>>(
					CommonResponseDTO.builder().status(HttpStatus.BAD_REQUEST.value()).timeStamp(LocalDateTime.now())
							.errors(ErrorDetails.builder()
									.errorMessage(invalidFormatException.getMessage().stripLeading()).errorCode(null)
									.customMessage("Invalid Request Payload Data").build())
							.build(),
					HttpStatus.BAD_REQUEST);
			// }
		}
		return new ResponseEntity<CommonResponseDTO<?>>(
				CommonResponseDTO.builder().status(HttpStatus.BAD_REQUEST.value()).timeStamp(LocalDateTime.now())
						.errors(ErrorDetails.builder().errorMessage(exception.getMessage()).errorCode(null)
								.customMessage("Please check the request payload data").build())
						.build(),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<CommonResponseDTO<?>> genericException(Exception ex, HttpServletRequest request) {
		logger.error("Exception : " + ex.getLocalizedMessage() + " for " + request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(
				CommonResponseDTO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.timeStamp(LocalDateTime.now())
						.errors(ErrorDetails.builder().errorMessage(ex.getMessage()).errorCode(null)
								.customMessage("Could not process request").build())
						.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ LocationDetailsNotFoundExcpetion.class })
	public ResponseEntity<CommonResponseDTO<?>> LocationDetailsException(LocationDetailsNotFoundExcpetion ex,
			HttpServletRequest request) {
		logger.error("Location Details Not Found Exception : " + ex.getLocalizedMessage() + " for "
				+ request.getRequestURI());
		return new ResponseEntity<CommonResponseDTO<?>>(CommonResponseDTO.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).timeStamp(LocalDateTime.now())
				.errors(ErrorDetails.builder().errorMessage(ex.getLocalizedMessage()).errorCode(ex.getErrorCode())
						.errorMessage(ex.getErrorMessage()).customMessage("Requested Location Details Are Not Found.")
						.subErrors(setBusinessErrorList(ex.getBusinessErrorList())).build())
				.build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private List<SubError> setBusinessErrorList(Map<String, String> businessErrorList) {
		List<SubError> subErrors = new ArrayList<>();
		businessErrorList.entrySet().stream().forEach(businessError -> {
			SubError subError = new SubError();
			subError.setSubErrorCode(businessError.getKey());
			subError.setSubErrorMessage(businessError.getValue());
			subErrors.add(subError);
		});
		return subErrors;
	}

}