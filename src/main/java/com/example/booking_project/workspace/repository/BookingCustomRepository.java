package com.example.booking_project.workspace.repository;

import java.math.BigInteger;
/**
 * 
 * @author Parandhamaiah.Naidu
 *
 */
import java.sql.Date;
import java.util.List;

import com.example.booking_project.workspace.dto.AdminDashboardStatsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsResponseDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutDTO;


public interface BookingCustomRepository {

	List<WorkspaceLocationFlatResponseDTO> getlocationDetails();

	List<BookingDetailsResponseDTO> getBookingDetails(BigInteger bookingId);

	List<WorkspaceLayoutDTO> getWorkspaceLayoutDetails(Date date, String workspaceType, String locationCode,
			String floorNo, BigInteger locationMasterId);

	void cancelBooking(BookingCancellationRequestDTO bookingCancellationRequest);

	List<BookingDetailsResponseDTO> getPreviousAndFutureBookingDetails(BookingDetailsRequestDTO bookingDetailsReq);

	AdminDashboardDetailsResponseDTO getAdminDashboardDetails(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq);

	AdminDashboardStatsResponseDTO getAdminDashboardStatsDetails(
			AdminDashboardStatsRequestDTO adminDashboardDetailsReq);

	List<Object> getBookingSearchDetails(BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO);

	List<Object> getBookingSeatDetails(BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO);

	int getTotalSeatCount(BookingDetailsRequestDTO bookingDetailsReq);
}