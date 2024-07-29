/**
 * 
 */
package com.example.booking_project.workspace.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking_project.workspace.common.dto.BlockRequestDTO;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingRequestDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.DefaultPreferencesRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutResponseDTO;
import com.example.booking_project.workspace.service.BookingService;
import com.example.booking_project.workspace.service.WorkspaceBookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */
@RestController
@Tag(name = "booking-controller", description = "Booking Controller")
@RequestMapping("/workspace/booking")
public class WorkspaceBookingController {

	private static Logger logger = LoggerFactory.getLogger(WorkspaceBookingController.class);

	@Autowired
	WorkspaceBookingService workspaceBookingService;

	@Autowired
	BookingService bookingServiceImpl;

	@GetMapping(path = "/locations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Workspace locations API", description = "fetch Workspace Details", tags = "booking-controller")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Workspace locations Details Fetched Successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> getlocations() {
		logger.info("WorkspaceBookingController :: getlocations :: ");
		return ResponseEntity.status(HttpStatus.OK).body(workspaceBookingService.fetchWorkspaceLocationsDetails());

	}

	@PostMapping(path = "/submission", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Workspace Booking Submission", description = "Book Workpspace", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Workspace Booked Successfully"),
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> bookWorkspace(
			@Validated @RequestBody(required = true) BookingRequestDTO workspaceBookingRequest) throws ParseException {
		logger.info("WorkspaceBookingController :: bookWorkspace :: workspaceBookingRequest : STARTED ");
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.createBooking(workspaceBookingRequest));
	}

	@PostMapping(path = "/block-submission", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Workspace Booking Submission", description = "Book Workpspace", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Workspace Booked Successfully"),
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> blockSeat(
			@Validated @RequestBody(required = true) BlockRequestDTO blockRequestDTO) throws ParseException {
		logger.info("WorkspaceBookingController :: blockSeat :: adminBookingRequestDTO : STARTED ");
		return ResponseEntity.status(HttpStatus.OK).body(bookingServiceImpl.blockSeat(blockRequestDTO));
	}

	@PostMapping(path = "/cancellation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Workspace Booking Cancellation", description = "Cancel Booking", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Booking Cancelled Successfully"),
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> cancelBooking(
			@Validated @RequestBody(required = true) List<BookingCancellationRequestDTO> bookingCancellationRequest) {
		logger.info("WorkspaceBookingController :: cancelBooking :: bookingCancellationRequest : {} "
				+ bookingCancellationRequest);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.cancelBooking(bookingCancellationRequest));
	}

	@PostMapping(path = "/layout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Workspace Layout API", description = "Book Workpspace", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Workspace Details Fetched Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<WorkspaceLayoutResponseDTO>> getLayout(
			@Validated @RequestBody(required = true) WorkspaceLayoutRequestDTO workspaceLayoutRequest) {
		logger.info("WorkspaceBookingController :: getLayout :: workspaceLayoutRequest : {} " + workspaceLayoutRequest);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.fetchWorkspaceLayoutDetails(workspaceLayoutRequest));
	}

	@PostMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Prev / Future Booking details API", description = "Fetch Previous / future Booking Details", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Workspace Booked Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> getBookingDetails(
			@Validated @RequestBody(required = true) BookingDetailsRequestDTO bookingDetailsReq) {
		logger.info("WorkspaceBookingController :: getBookingDetails :: bookingDetailsReq : {} " + bookingDetailsReq);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.fetchBookingDetails(bookingDetailsReq));
	}

	@PostMapping(path = "/report", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Admin Dashboard Booking details API", description = "Fetch Admin Dashboard Booking details", tags = "booking-controller")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Fetch Admin Dashboard Booking details Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> getAdminDashboardReport(
			@Validated @RequestBody(required = true) AdminDashboardDetailsRequestDTO adminDashboardDetailsReq) {
		logger.info("WorkspaceBookingController :: getAdminDashboardDetails :: adminDashboardDetailsReq : {} "
				+ adminDashboardDetailsReq);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.fetchAdminDashboardDetails(adminDashboardDetailsReq));
	}

	@PostMapping(path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Admin Dashboard Booking Stats API", description = "Fetch Admin Dashboard Booking Stats Info", tags = "booking-controller")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Fetch Admin Dashboard Booking details Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> getAdminDashboardStats(
			@Validated @RequestBody(required = true) AdminDashboardStatsRequestDTO adminDashboardStatsReq) {
		logger.info("WorkspaceBookingController :: getAdminDashboardDetails :: adminDashboardStatsReq : {} "
				+ adminDashboardStatsReq);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.fetchAdminDashboardStats(adminDashboardStatsReq));
	}

	@PostMapping(path = "/dashboard/download-report", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Admin Dashboard Booking details API", description = "Fetch Admin Dashboard Booking details", tags = "booking-controller")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "downloaded Admin Dashboard Booking details Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public byte[] downloadDashboardDetails(HttpServletResponse response,
			@Validated @RequestBody(required = true) AdminDashboardDetailsRequestDTO adminDashboardDetailsReq)
			throws ParseException, IOException {
		logger.info("WorkspaceBookingController :: downloadDashboardDetails :: downlaodDashboardDetailsReq : {} "
				+ adminDashboardDetailsReq);
		response.setContentType("application/x-ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=DashboardDetailsReport.xls");
		return workspaceBookingService.downloadAdminDashboardDetails(adminDashboardDetailsReq, response);

	}

	@PostMapping(path = "/search-seat", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Search Booking Details", description = "Get the Search Booking Details", tags = "booking-controller")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Workspace Search Deatils"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<?>> getBookingSearchDetails(
			@Validated @RequestBody(required = true) BookingSearchDetailsRequestDTO bookingSearchDetailsReq)
			throws ParseException {
		logger.info("WorkspaceBookingController :: getBooking Search Details :: bookingSearchDetailsReq : {} "
				+ bookingSearchDetailsReq);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.fetchBookingSearchDetails(bookingSearchDetailsReq));
	}

	@PostMapping(path = "/default-preference", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Default Preference Booking API", description = "Book Workpspace", tags = "booking-controller")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Booking Preferences made as a dafault Successfully"),
			@ApiResponse(responseCode = "200", description = "Created", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true))) })
	public ResponseEntity<CommonResponseDTO<String>> makeAsDefaultBooking(
			@Validated @RequestBody(required = true) DefaultPreferencesRequestDTO defaultPreferencesReq) {
		logger.info("WorkspaceBookingController :: makeAsDefaultBooking :: defaultPreferencesReq : {} "
				+ defaultPreferencesReq);
		return ResponseEntity.status(HttpStatus.OK)
				.body(workspaceBookingService.makeAsDefaultPrefrence(defaultPreferencesReq));
	}
}