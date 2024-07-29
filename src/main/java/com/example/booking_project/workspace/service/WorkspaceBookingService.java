package com.example.booking_project.workspace.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsResponseDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingCancellationResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingRequestDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.DefaultPreferencesRequestDTO;
import com.example.booking_project.workspace.dto.OfficeWorkspaceResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLocationResponseDTO;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */

@Service
public interface WorkspaceBookingService {

	CommonResponseDTO<?> createBooking(BookingRequestDTO workspaceBookingRequest) throws ParseException;

	CommonResponseDTO<List<OfficeWorkspaceResponseDTO>> fetchWorkspaceDetailsByLocation(String locationCode);

	CommonResponseDTO<WorkspaceLayoutResponseDTO> fetchWorkspaceLayoutDetails(
			WorkspaceLayoutRequestDTO workspaceLayoutRequest);

	CommonResponseDTO<List<WorkspaceLocationResponseDTO>> fetchWorkspaceLocationsDetails();

	CommonResponseDTO<List<BookingCancellationResponseDTO>> cancelBooking(
			List<BookingCancellationRequestDTO> bookingCancellationRequest);

	CommonResponseDTO<List<BookingDetailResponseDTO>> fetchBookingDetails(BookingDetailsRequestDTO bookingDetailsReq);

	CommonResponseDTO<AdminDashboardDetailsResponseDTO> fetchAdminDashboardDetails(
			AdminDashboardDetailsRequestDTO adminDashboardDetailsReq);

	CommonResponseDTO<AdminDashboardStatsResponseDTO> fetchAdminDashboardStats(
			AdminDashboardStatsRequestDTO adminDashboardStatsReq);

	byte[] downloadAdminDashboardDetails(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq,
			HttpServletResponse response) throws IOException;

	CommonResponseDTO<List<Object>> fetchBookingSearchDetails(

			BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO);

	CommonResponseDTO<String> makeAsDefaultPrefrence(DefaultPreferencesRequestDTO defaultPreferencesReq);
}
