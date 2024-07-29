package com.example.booking_project.workspace.repository;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.example.booking_project.workspace.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponse;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsResponseDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsResponseDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsResponseDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsSeatLayoutResponseDTO;
import com.example.booking_project.workspace.dto.DashboardStatistics;
import com.example.booking_project.workspace.dto.MealStatisticsDetails;
import com.example.booking_project.workspace.dto.ParkingStatisticsDetails;
import com.example.booking_project.workspace.dto.WorkspaceDetailsSeatDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutDTO;
import com.example.booking_project.workspace.dto.WorkspaceStatisticsDetails;
import com.example.booking_project.workspace.enums.BookingRequestType;
import com.example.booking_project.workspace.enums.ParkingLotType;
import com.example.booking_project.workspace.enums.UserType;
import com.example.booking_project.workspace.util.CommonUtility;

/**
 * 
 * @author Parandhamaiah.Naidu
 *
 */
@Component
public class CustomBookingRepositoryImpl implements BookingCustomRepository {

	private static Logger logger = LoggerFactory.getLogger(CustomBookingRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ParkingLotMasterRepository parkingItemsRepository;

	@Autowired
	LocationMasterRepository locationMasterRepository;

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkspaceLocationFlatResponseDTO> getlocationDetails() {

		String query = "select lm.location_master_id, lm.location_code ,lm.location_name, lm.floor_no, wtm.workspace_name, wtm.workspace_type_code, lm.city, "
				+ " lm.state, lm.country  from (select *  from [booking].[workspace].[location_master] where status = 1)"
				+ " lm LEFT JOIN (select * from [booking].[workspace].[workspace_type_master]  where status = 1 )  wtm "
				+ " on lm.location_master_id = wtm.location_master_id ORDER BY location_code, floor_no, workspace_name ";

		List<Object[]> locationListObjs = entityManager.createNativeQuery(query).getResultList();

		logger.info(" BookingServiceHelper :: getlocationDetails : query." + query);

		List<WorkspaceLocationFlatResponseDTO> locationList = new ArrayList<>();

		for (Object[] loc : locationListObjs) {

			WorkspaceLocationFlatResponseDTO workspaceLocationFlatResp = new WorkspaceLocationFlatResponseDTO();

			workspaceLocationFlatResp.setLocationId((BigInteger) loc[0]);
			workspaceLocationFlatResp.setLocationCode((String) loc[1]);
			workspaceLocationFlatResp.setLocationName((String) loc[2]);
			workspaceLocationFlatResp.setFloorNo((String) loc[3]);
			workspaceLocationFlatResp.setWorkspaceName((String) loc[4]);
			workspaceLocationFlatResp.setWorkspaceTypeCode((String) loc[5]);
			workspaceLocationFlatResp.setCity((String) loc[6]);
			workspaceLocationFlatResp.setState((String) loc[7]);
			workspaceLocationFlatResp.setCountry((String) loc[8]);

			locationList.add(workspaceLocationFlatResp);
		}

		return locationList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BookingDetailsResponseDTO> getBookingDetails(BigInteger bookingId) {

		List<BookingDetailsResponseDTO> workspaceAndMealDetailsList = new ArrayList<>();

		String query = "SELECT distinct * from (SELECT bookingDetails.booking_id, workspaceItems.booking_date, workspaceItems.booking_type, workspaceItems.workspace_type,  "
				+ "workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no,  "
				+ "workspaceItems.city, workspaceItems.country, "
				+ "parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots, mealItems.no_of_lunch,  "
				+ "mealItems.no_of_dinner,  bookingDetails.requester_id, bookingDetails.requester_name,  "
				+ "bookingDetails.requested_date, workspaceItems.employee_id, workspaceItems.employee_name FROM  "
				+ "(SELECT * FROM [booking].[workspace].[booking_details] WHERE booking_id=:bookingId) AS bookingDetails JOIN "
				+ "(SELECT booking_id, booking_type, booking_date, STRING_AGG (workspace_code, ',') as workspace_codes, workspace_type,  "
				+ "location_code, floor_no, city, country, employee_id, employee_name "
				+ "FROM [booking].[workspace].[workspace_items] WHERE booking_id=:bookingId GROUP BY booking_date, booking_id, booking_type,  "
				+ "workspace_type, location_code, floor_no, city, country, employee_id, employee_name) AS  workspaceItems "
				+ "ON bookingDetails.booking_id = workspaceItems.booking_id JOIN "
				+ "(SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots,  "
				+ "STRING_AGG (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots "
				+ "FROM [booking].[workspace].[parking_items] WHERE booking_id=:bookingId AND status='BOOKED' GROUP BY booking_date) AS parkingItems "
				+ "ON workspaceItems.booking_date = parkingItems.booking_date JOIN "
				+ "(SELECT booking_id, booking_date, no_of_lunch, no_of_dinner "
				+ "FROM [booking].[workspace].[meal_items] WHERE booking_id=:bookingId AND status='BOOKED') AS mealItems "
				+ "ON parkingItems.booking_date = mealItems.booking_date) as tmp";

		logger.info(" BookingServiceHelper :: getBookingDetails : query." + query);

		List<Object[]> bookingDetailObjs = entityManager.createNativeQuery(query).setParameter("bookingId", bookingId)
				.getResultList();

		for (Object[] bookingDetailObj : bookingDetailObjs) {
			BookingDetailsResponseDTO workspaceAndMealDetailsDTO = new BookingDetailsResponseDTO();
			workspaceAndMealDetailsDTO.setBookingId((BigInteger) bookingDetailObj[0]);
			workspaceAndMealDetailsDTO
					.setBookingDate(CommonUtility.sQLDateToString(((Date) bookingDetailObj[1])));
			workspaceAndMealDetailsDTO.setBookingType((String) bookingDetailObj[2]);
			workspaceAndMealDetailsDTO.setWorkspaceType((String) bookingDetailObj[3]);
			workspaceAndMealDetailsDTO.setWorkspaceCodes((String) bookingDetailObj[4]);
			workspaceAndMealDetailsDTO.setLocationCode((String) bookingDetailObj[5]);
			workspaceAndMealDetailsDTO.setFloorNo((String) bookingDetailObj[6]);
			workspaceAndMealDetailsDTO.setCity((String) bookingDetailObj[7]);
			workspaceAndMealDetailsDTO.setCountry((String) bookingDetailObj[8]);
			workspaceAndMealDetailsDTO.setTwoWheelerSlots((String) bookingDetailObj[9]);
			workspaceAndMealDetailsDTO.setFourWheelerSlots((String) bookingDetailObj[10]);
			workspaceAndMealDetailsDTO.setNoOfLunch(((Integer) bookingDetailObj[11]).intValue());
			workspaceAndMealDetailsDTO.setNoOfDinner(((Integer) bookingDetailObj[12]).intValue());
			workspaceAndMealDetailsDTO.setRequesterId((String) bookingDetailObj[13]);
			workspaceAndMealDetailsDTO.setRequesterName((String) bookingDetailObj[14]);
			workspaceAndMealDetailsDTO
					.setRequestedDate(CommonUtility.sQLDateToString(((Date) bookingDetailObj[15])));
			workspaceAndMealDetailsDTO.setEmployeeId((String) bookingDetailObj[16]);
			workspaceAndMealDetailsDTO.setEmployeeName((String) bookingDetailObj[17]);

			workspaceAndMealDetailsList.add(workspaceAndMealDetailsDTO);
		}
		return workspaceAndMealDetailsList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkspaceLayoutDTO> getWorkspaceLayoutDetails(Date requtedDate, String workspaceType,
															  String locationCode, String floorNo, BigInteger locationMasterId) {
		List<WorkspaceLayoutDTO> workspaceLayoutList = new ArrayList<>();
		String query = "select ISNULL(wi.location_code,(:locationCode)) as location_code, wm.workspace_code, wm.workspace_no, wm.workspace_type, ISNULL(wi.floor_no,(:floorNo)) as floorNo, ISNULL(wi.status,'AVAILABLE')  AS workspace_status, wi.employee_id, wi.employee_name, wi.department, wi.division from "
				+ " (select *  from [booking].[workspace].[workspace_master] where location_master_id = :locationMasterId AND status ='Active' AND workspace_type =:workspaceType ) wm"
				+ " LEFT JOIN (select * from [booking].[workspace].[workspace_items] where booking_date = :bookingDate AND "
				+ " location_code =:locationCode AND floor_no = :floorNo AND workspace_type =:workspaceType AND status='BOOKED')  wi"
				+ " on wm.workspace_code = wi.workspace_code";

		logger.info(" BookingServiceHelper :: getWorkspaceLayoutDetails : query." + query);

		List<Object[]> workspaceLayoutDetails = entityManager.createNativeQuery(query)
				.setParameter("locationCode", locationCode).setParameter("bookingDate", requtedDate)
				.setParameter("floorNo", floorNo).setParameter("locationMasterId", locationMasterId)
				.setParameter("workspaceType", workspaceType).getResultList();
		for (Object[] workspaceLayoutDetail : workspaceLayoutDetails) {
			WorkspaceLayoutDTO workspaceLayoutDTO = new WorkspaceLayoutDTO();
			workspaceLayoutDTO.setLocationCode((String) workspaceLayoutDetail[0]);
			workspaceLayoutDTO.setWorkspaceCode((String) workspaceLayoutDetail[2]);
			workspaceLayoutDTO.setSeatNo((String) workspaceLayoutDetail[1]);
			workspaceLayoutDTO.setWorkspaceType((String) workspaceLayoutDetail[3]);
			workspaceLayoutDTO.setFloorNo((String) workspaceLayoutDetail[4]);
			workspaceLayoutDTO.setWorkspaceStatus((String) workspaceLayoutDetail[5]);
			workspaceLayoutDTO.setEmployeeId((String) workspaceLayoutDetail[6]);
			workspaceLayoutDTO.setEmployeeName((String) workspaceLayoutDetail[7]);
			workspaceLayoutDTO.setDepartmentName((String) workspaceLayoutDetail[8]);
			workspaceLayoutDTO.setDivisionName((String) workspaceLayoutDetail[9]);
			workspaceLayoutList.add(workspaceLayoutDTO);
		}
		return workspaceLayoutList;
	}

	@Override
	public void cancelBooking(BookingCancellationRequestDTO bookingCancellationRequest) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BookingDetailsResponseDTO> getPreviousAndFutureBookingDetails(
			BookingDetailsRequestDTO bookingDetailsReq) {

		List<BookingDetailsResponseDTO> bookingDetails = new ArrayList<>();
		String query = null;
		String totalSeatsQuery = null;
		int totalSeats = 0;
		List<Object[]> bookingDetailObjs = new ArrayList<>();

		if (BookingRequestType.UPCOMING.toString().equalsIgnoreCase(bookingDetailsReq.getRequestType().toString())) {
			query = "SELECT workspaceItems.booking_id, workspaceItems.booking_date, workspaceItems.booking_type, workspaceItems.workspace_type, "
					+ " workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no, "
					+ " workspaceItems.city, workspaceItems.country, "
					+ " parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots, mealItems.no_of_lunch, "
					+ " mealItems.no_of_dinner,  workspaceItems.employee_id, workspaceItems.employee_name, workspaceItems.status FROM "
					+ " (SELECT booking_id, booking_type, booking_date, STRING_AGG (workspace_code, ',') as workspace_codes, workspace_type, "
					+ " location_code, floor_no, city, country, employee_id, employee_name, status"
					+ " FROM [booking].[workspace].[workspace_items] WHERE booking_date>=CAST( GETDATE() AS Date ) AND status='BOOKED' AND employee_id=:employeeId AND booking_type =:bookingType GROUP BY booking_date, booking_id, booking_type, "
					+ " workspace_type, location_code, floor_no, city, country, employee_id, employee_name, status) AS  workspaceItems"
					+ " JOIN" + " (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner"
					+ " FROM [booking].[workspace].[meal_items] WHERE booking_date>=CAST( GETDATE() AS Date )  AND status='BOOKED' AND employee_id=:employeeId ) AS mealItems"
					+ " ON workspaceItems.booking_date = mealItems.booking_date  JOIN"
					+ " (SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots, "
					+ " STRING_AGG (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots"
					+ " FROM [booking].[workspace].[parking_items] WHERE booking_date>=CAST( GETDATE() AS Date )  AND status='BOOKED' AND employee_id=:employeeId GROUP BY booking_date) AS parkingItems"
					+ " ON mealItems.booking_date = parkingItems.booking_date;";
			logger.info(" BookingServiceHelper :: getPreviousAndFutureBookingDetails : Upcoming : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("employeeId", bookingDetailsReq.getEmployeeId())
					.setParameter("bookingType", bookingDetailsReq.getBookingType().name()).getResultList();
		} else if (BookingRequestType.HISTORY.toString()
				.equalsIgnoreCase(bookingDetailsReq.getRequestType().toString())) {
			query = "SELECT workspaceItems.booking_id, workspaceItems.booking_date, workspaceItems.booking_type, workspaceItems.workspace_type,  "
					+ "workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no,  "
					+ "workspaceItems.city, workspaceItems.country, "
					+ "parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots, mealItems.no_of_lunch,  "
					+ "mealItems.no_of_dinner, workspaceItems.employee_id, workspaceItems.employee_name, workspaceItems.status FROM  "
					+ "(SELECT booking_id, booking_type, booking_date, STRING_AGG (workspace_code, ',') as workspace_codes, workspace_type,  "
					+ "location_code, floor_no, city, country, employee_id, employee_name, status "
					+ "FROM [booking].[workspace].[workspace_items] WHERE ( booking_date<CAST( GETDATE() AS Date ) OR status='CANCELLED') AND employee_id=:employeeId AND booking_type =:bookingType GROUP BY booking_date, booking_id, booking_type,  "
					+ "workspace_type, location_code, floor_no, city, country, employee_id, employee_name, status) AS  workspaceItems "
					+ "JOIN (SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots,  "
					+ "STRING_AGG (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots "
					+ "FROM [booking].[workspace].[parking_items] WHERE ( booking_date<CAST( GETDATE() AS Date ) OR status='CANCELLED') AND employee_id=:employeeId GROUP BY booking_date) AS parkingItems "
					+ "ON workspaceItems.booking_date = parkingItems.booking_date JOIN "
					+ "(SELECT booking_id, booking_date, no_of_lunch, no_of_dinner "
					+ "FROM [booking].[workspace].[meal_items] WHERE ( booking_date<CAST( GETDATE() AS Date ) OR status='CANCELLED') AND employee_id=:employeeId) AS mealItems "
					+ "ON parkingItems.booking_date = mealItems.booking_date";
			logger.info(" BookingServiceHelper :: getPreviousAndFutureBookingDetails : History : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("employeeId", bookingDetailsReq.getEmployeeId())
					.setParameter("bookingType", bookingDetailsReq.getBookingType().name()).getResultList();
		} else if (BookingRequestType.PREVIOUS.toString()
				.equalsIgnoreCase(bookingDetailsReq.getRequestType().toString())) {

			String bookingIdQuery = "select TOP 1 * from [booking].[workspace].[booking_details] WHERE requester_id=:requesterId "
					+ " AND booking_start_date < CAST( GETDATE() AS Date ) "
					+ " AND status= 'BOOKED' AND is_default_booking_preference='1' ORDER BY booking_start_date DESC ";
			logger.info(" BookingServiceHelper :: getPreviousAndFutureBookingDetails : Previous : query. "
					+ bookingIdQuery);
			List<Object[]> defaultBookingDetails = entityManager.createNativeQuery(bookingIdQuery)
					.setParameter("requesterId", bookingDetailsReq.getEmployeeId()).getResultList();

			if (defaultBookingDetails.size() > 0) {
				bookingDetailObjs = fetchBookingDetails(bookingDetailsReq, defaultBookingDetails);
			} else {
				logger.info(" BookingServiceHelper :: getPreviousAndFutureBookingDetails : NO Default Bookings.");
				String fetchBDatequery = "select TOP 1 booking_id, booking_date, booking_type, employee_id from [booking].[workspace].[workspace_items] WHERE employee_id=:employeeId AND booking_date <= CAST(GETDATE() AS date) ORDER BY booking_date DESC ";
				List<Object[]> lastBookingDetails = entityManager.createNativeQuery(fetchBDatequery)
						.setParameter("employeeId", bookingDetailsReq.getEmployeeId()).getResultList();
				bookingDetailObjs = fetchBookingDetails(bookingDetailsReq, lastBookingDetails);
			}
		}
		for (Object[] bookingDetailObj : bookingDetailObjs) {
			BookingDetailsResponseDTO workspaceAndMealDetailsDTO = new BookingDetailsResponseDTO();
			workspaceAndMealDetailsDTO.setBookingId((BigInteger) bookingDetailObj[0]);
			workspaceAndMealDetailsDTO
					.setBookingDate(CommonUtility.sQLDateToString(((Date) bookingDetailObj[1])));
			workspaceAndMealDetailsDTO.setBookingType((String) bookingDetailObj[2]);
			workspaceAndMealDetailsDTO.setWorkspaceType((String) bookingDetailObj[3]);
			workspaceAndMealDetailsDTO.setWorkspaceCodes((String) bookingDetailObj[4]);
			workspaceAndMealDetailsDTO.setLocationCode((String) bookingDetailObj[5]);
			workspaceAndMealDetailsDTO.setFloorNo((String) bookingDetailObj[6]);
			workspaceAndMealDetailsDTO.setCity((String) bookingDetailObj[7]);
			workspaceAndMealDetailsDTO.setCountry((String) bookingDetailObj[8]);
			workspaceAndMealDetailsDTO.setTwoWheelerSlots((String) bookingDetailObj[9]);
			workspaceAndMealDetailsDTO.setFourWheelerSlots((String) bookingDetailObj[10]);
			workspaceAndMealDetailsDTO.setNoOfLunch(((Integer) bookingDetailObj[11]).intValue());
			workspaceAndMealDetailsDTO.setNoOfDinner(((Integer) bookingDetailObj[12]).intValue());
			workspaceAndMealDetailsDTO.setEmployeeId((String) bookingDetailObj[13]);
			workspaceAndMealDetailsDTO.setEmployeeName((String) bookingDetailObj[14]);
			workspaceAndMealDetailsDTO.setStatus((String) bookingDetailObj[15]);
			int noOfTwoLotsAvailable = getTwoWhellerParkinglot(bookingDetailsReq.getLocationCode(),
					ParkingLotType.TWO_WHEELER.toString());
			int noOfFourLotsAvailable = getFourWhellerParkinglot(bookingDetailsReq.getLocationCode(),
					ParkingLotType.FOUR_WHEELER.toString());
			int avaialableSeats = getAvaialbleSeats(bookingDetailsReq.getLocationCode());
			String bookedSeatStatus = getBookedSeatStatus(bookingDetailsReq.getLocationCode(),
					(String) bookingDetailObj[4]);
			workspaceAndMealDetailsDTO.setNoOfTwoLotsAvailable(noOfTwoLotsAvailable);
			workspaceAndMealDetailsDTO.setNoOfFourLotsAvailable(noOfFourLotsAvailable);
			workspaceAndMealDetailsDTO.setNoOfSeatsAvailable(avaialableSeats);
			workspaceAndMealDetailsDTO.setPreviousBookedSeatStatus(bookedSeatStatus);

			bookingDetails.add(workspaceAndMealDetailsDTO);
		}

		return bookingDetails;
	}

	private int getTwoWhellerParkinglot(String locationCode, String parkingType) {

		int twowheeller = parkingItemsRepository.getTwoFourWheelerCount(locationCode,
				ParkingLotType.TWO_WHEELER.toString());
		System.out.println("twowheeller" + twowheeller);
		System.out.println(ParkingLotType.TWO_WHEELER.toString());
		return twowheeller;
	}

	private int getFourWhellerParkinglot(String locationCode, String parkingType) {

		int fourwheeller = parkingItemsRepository.getTwoFourWheelerCount(locationCode,
				ParkingLotType.FOUR_WHEELER.toString());
		System.out.println("fourwheeller" + fourwheeller);
		return fourwheeller;
	}

	private int getAvaialbleSeats(String locationCode) {

		int availableSeats = parkingItemsRepository.getAvaialbleSeats(locationCode);
		System.out.println("availableSeats" + availableSeats);
		return availableSeats;
	}

	private String getBookedSeatStatus(String locationCode, String workspaceCode) {

		String bookedSeatStatus = parkingItemsRepository.getBookedSeatStatus(locationCode, workspaceCode);
		System.out.println("bookedSeatStatus" + bookedSeatStatus);
		return (null != bookedSeatStatus && !bookedSeatStatus.isBlank()) ? bookedSeatStatus : "AVAILABLE";
	}

	private List<Object[]> fetchBookingDetails(BookingDetailsRequestDTO bookingDetailsReq,
			List<Object[]> previousBookingDetails) {

		List<Object[]> bookingDetailObjs = new ArrayList<>();
		BigInteger bookingId = null;

		for (Object[] bookingDetailObj : previousBookingDetails) {
			bookingId = (BigInteger) bookingDetailObj[0];
		}
		String query = "SELECT workspaceItems.booking_id, workspaceItems.booking_date, workspaceItems.booking_type, workspaceItems.workspace_type,  "
				+ "workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no,  "
				+ "workspaceItems.city, workspaceItems.country, "
				+ "parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots, mealItems.no_of_lunch,  "
				+ "mealItems.no_of_dinner, workspaceItems.employee_id, workspaceItems.employee_name, workspaceItems.status FROM  "
				+ "(SELECT booking_id, booking_type, booking_date, STRING_AGG (workspace_code, ',') as workspace_codes, workspace_type,  "
				+ "location_code, floor_no, city, country, employee_id, employee_name, status "
				+ "FROM [booking].[workspace].[workspace_items] WHERE booking_id=(:bookingId) AND status='BOOKED' AND employee_id=:employeeId AND booking_type =:bookingType GROUP BY booking_date, booking_id, booking_type,  "
				+ "workspace_type, location_code, floor_no, city, country, employee_id, employee_name, status) AS  workspaceItems "
				+ "JOIN (SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots,  "
				+ "STRING_AGG (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots "
				+ "FROM [booking].[workspace].[parking_items] WHERE booking_id=(:bookingId) AND status='BOOKED' AND employee_id=:employeeId GROUP BY booking_date) AS parkingItems "
				+ "ON workspaceItems.booking_date = parkingItems.booking_date JOIN "
				+ "(SELECT booking_id, booking_date, no_of_lunch, no_of_dinner "
				+ "FROM [booking].[workspace].[meal_items] WHERE booking_id=(:bookingId) AND status='BOOKED' AND employee_id=:employeeId ) AS mealItems "
				+ "ON parkingItems.booking_date = mealItems.booking_date";

		logger.info(" BookingServiceHelper :: getPreviousAndFutureBookingDetails : Previous : query." + query);
		bookingDetailObjs = entityManager.createNativeQuery(query).setParameter("bookingId", bookingId)
				.setParameter("employeeId", bookingDetailsReq.getEmployeeId())
				.setParameter("bookingType", bookingDetailsReq.getBookingType().name()).getResultList();
		return bookingDetailObjs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AdminDashboardDetailsResponseDTO getAdminDashboardDetails(
			AdminDashboardDetailsRequestDTO adminDashboardDetailsReq) {

		AdminDashboardDetailsResponseDTO adminDashboardDetailsResponse = new AdminDashboardDetailsResponseDTO();

		List<AdminDashboardDetailsResponse> bookingDetails = new ArrayList<>();
		List<Object[]> bookingDetailObjs = new ArrayList<>();

		int offsetNo = adminDashboardDetailsReq.getPageNo() - 1;

		int i = offsetNo;
		if (i > 0) {
			offsetNo = offsetNo * adminDashboardDetailsReq.getRecords();
		}

		logger.info(" BookingServiceHelper :: getAdminDashboardDetails : offsetNo : " + offsetNo + " records : "
				+ adminDashboardDetailsReq.getRecords());
		String query = null;

		if (adminDashboardDetailsReq.getPageNo() == 0 && adminDashboardDetailsReq.getRecords() == 0) {
			bookingDetailObjs = downloadReportDetails(adminDashboardDetailsReq);
		} else if (adminDashboardDetailsReq.getLocationCode().equalsIgnoreCase("all")
				&& adminDashboardDetailsReq.getUserType() != null
				&& adminDashboardDetailsReq.getUserType().toString().equals(UserType.EMPLOYEE.toString())) {
			query = "SELECT wi_mi.booking_id, wi_mi.booking_date, booking_type, wi_mi.workspace_type, wi_mi.workspace_codes, "
					+ "	location_code, floor_no, city, country, employee_id, employee_name, wi_mi.no_of_lunch, wi_mi.no_of_dinner, overall_count, requested_for, "
					+ "	wi_mi_pi.two_wheeler_parking_lot,  wi_mi_pi.four_wheeler_parking_lot FROM (SELECT workspace_items.booking_id, workspace_items.booking_date, workspace_items.booking_type,  employee_id, employee_name, "
					+ "	workspace_codes, workspace_type, location_code, floor_no, city, country, meal_items.no_of_lunch, meal_items.no_of_dinner, overall_count = COUNT(*) OVER() "
					+ "	FROM "
					+ "(SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, workspace_type, location_code, floor_no, city, country,  "
					+ "employee_id, employee_name FROM [booking].[workspace].[workspace_items] WHERE booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) AS workspace_items  "
					+ "INNER JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner  "
					+ "FROM [booking].[workspace].[meal_items] WHERE booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) as meal_items ON workspace_items.booking_id = meal_items.booking_id  "
					+ "AND workspace_items.booking_date = meal_items.booking_date) as wi_mi "
					+ "INNER JOIN (SELECT  two_wheeler_parking_lot, four_wheeler_parking_lot, booking_id, booking_date, requested_for  FROM  [booking].[workspace].[parking_items]  "
					+ "WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)  "
					+ "AND booking_type like (:bookingType)  AND status= 'BOOKED' AND  "
					+ "requested_for = :userType) AS wi_mi_pi ON wi_mi.booking_id = wi_mi_pi.booking_id AND wi_mi.booking_date = wi_mi_pi.booking_date "
					+ "ORDER BY wi_mi_pi.booking_id, wi_mi_pi.booking_date OFFSET (:pageNo) ROWS FETCH NEXT (:records) ROWS ONLY";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
					.setParameter("bookingType", "ADMIN%")
					.setParameter("userType", adminDashboardDetailsReq.getUserType().name())
					.setParameter("pageNo", offsetNo).setParameter("records", adminDashboardDetailsReq.getRecords())
					.getResultList();
		} else if (adminDashboardDetailsReq.getUserType() != null
				&& adminDashboardDetailsReq.getUserType().toString().equals(UserType.EMPLOYEE.toString())) {
			query = "SELECT wi_mi.booking_id, wi_mi.booking_date, booking_type, wi_mi.workspace_type, wi_mi.workspace_codes, "
					+ "location_code, floor_no, city, country, employee_id, employee_name, wi_mi.no_of_lunch, wi_mi.no_of_dinner, overall_count, requested_for, "
					+ "wi_mi_pi.two_wheeler_parking_lot,  wi_mi_pi.four_wheeler_parking_lot FROM (SELECT workspace_items.booking_id, workspace_items.booking_date, workspace_items.booking_type,  employee_id, employee_name, "
					+ "workspace_codes, workspace_type, location_code, floor_no, city, country, meal_items.no_of_lunch, meal_items.no_of_dinner, overall_count = COUNT(*) OVER() "
					+ " FROM (SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, workspace_type, location_code, floor_no, city, country,  "
					+ "employee_id, employee_name FROM [booking].[workspace].[workspace_items] WHERE employee_id IN (:employeeIds) AND (location_code=(:locationCode) OR floor_no=(:floorNo)) AND booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = 'EMPLOYEE') AS workspace_items  "
					+ " JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner  "
					+ "FROM [booking].[workspace].[meal_items] WHERE employee_id IN (:employeeIds) AND location_code=(:locationCode) AND booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) as meal_items ON workspace_items.booking_id = meal_items.booking_id  "
					+ "AND workspace_items.booking_date = meal_items.booking_date) as wi_mi "
					+ " JOIN (SELECT  two_wheeler_parking_lot, four_wheeler_parking_lot, booking_id, booking_date, requested_for  FROM  [booking].[workspace].[parking_items]  "
					+ "WHERE employee_id IN (:employeeIds) AND location_code=(:locationCode) AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)  "
					+ "AND booking_type like (:bookingType)  AND status= 'BOOKED' AND  "
					+ "requested_for = :userType) AS wi_mi_pi ON wi_mi.booking_id = wi_mi_pi.booking_id AND wi_mi.booking_date = wi_mi_pi.booking_date "
					+ "ORDER BY wi_mi_pi.booking_id, wi_mi_pi.booking_date OFFSET (:pageNo) ROWS FETCH NEXT (:records) ROWS ONLY";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = fetchEmployeeReport(adminDashboardDetailsReq, query, offsetNo, "location");
		} else {
			query = "SELECT  * FROM ( SELECT workspaceItems.booking_id, workspaceItems.booking_type, workspaceItems.booking_date, "
					+ "workspaceItems.workspace_type,  workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no,  "
					+ "workspaceItems.city, workspaceItems.country,  "
					+ "workspaceItems.employee_id, workspaceItems.employee_name,  mealItems.no_of_lunch, mealItems.no_of_dinner, overall_count = COUNT(*) OVER(), parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots"
					+ " FROM  (SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, "
					+ "workspace_type, location_code, floor_no, city, country, employee_id, employee_name FROM [booking].[workspace]."
					+ "[workspace_items] WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)"
					+ " AND booking_type like (:bookingType) AND location_code=(:locationCode) OR floor_no=(:floorNo) AND status= 'BOOKED' "
					+ " AND requested_for = :userType) AS  workspaceItems JOIN "
					+ " (SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots,  STRING_AGG "
					+ " (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots FROM [booking].[workspace].[parking_items] WHERE "
					+ " booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ " AND location_code=(:locationCode) AND status= 'BOOKED' "
					+ " AND requested_for = :userType GROUP BY booking_date) AS parkingItems ON "
					+ "workspaceItems.booking_date = parkingItems.booking_date "
					+ "JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner FROM [booking].[workspace].[meal_items] "
					+ "WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) "
					+ "AND booking_type like (:bookingType) AND location_code=(:locationCode) AND status= 'BOOKED' AND requested_for = :userType) AS mealItems ON "
					+ "parkingItems.booking_date = mealItems.booking_date ) TMP ORDER BY booking_id, booking_date OFFSET (:pageNo) "
					+ "ROWS FETCH NEXT (:records) ROWS ONLY";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("locationCode", adminDashboardDetailsReq.getLocationCode())
					.setParameter("floorNo", adminDashboardDetailsReq.getFloorNo())
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
					.setParameter("bookingType", "ADMIN%").setParameter("pageNo", offsetNo)
					.setParameter("userType", adminDashboardDetailsReq.getUserType().name())
					.setParameter("records", adminDashboardDetailsReq.getRecords()).getResultList();
		}

		for (Object[] bookingDetailObj : bookingDetailObjs) {
			AdminDashboardDetailsResponse adminDashboardDetailsResp = new AdminDashboardDetailsResponse();
			adminDashboardDetailsResp.setBookingId((String) bookingDetailObj[0].toString());
			adminDashboardDetailsResp
					.setBookingDate(CommonUtility.sQLDateToString(((Date) bookingDetailObj[1])));
			adminDashboardDetailsResp.setBookingType((String) bookingDetailObj[2]);
			adminDashboardDetailsResp.setWorkspaceType((String) bookingDetailObj[3]);
			adminDashboardDetailsResp.setWorkspaceCodes((String) bookingDetailObj[4]);
			adminDashboardDetailsResp.setLocationCode((String) bookingDetailObj[5]);
			adminDashboardDetailsResp.setFloorNo((String) bookingDetailObj[6]);
			adminDashboardDetailsResp.setCity((String) bookingDetailObj[7]);
			adminDashboardDetailsResp.setCountry((String) bookingDetailObj[8]);
			adminDashboardDetailsResp.setEmployeeId(bookingDetailObj[9].toString());
			adminDashboardDetailsResp.setEmployeeName((String) bookingDetailObj[10]);

			// adminDashboardDetailsResp.setTwoWheelerLots((String) bookingDetailObj[11]);
			// adminDashboardDetailsResp.setFourWheelerLots((String) bookingDetailObj[12]);
			adminDashboardDetailsResp.setNoOfLunch((int) bookingDetailObj[11]);
			adminDashboardDetailsResp.setNoOfDinner((int) bookingDetailObj[12]);

			adminDashboardDetailsResp.setTotalNoOfRecords((int) bookingDetailObj[13]);
			adminDashboardDetailsResp.setUserType((String) bookingDetailObj[14]);

			bookingDetails.add(adminDashboardDetailsResp);
		}
		adminDashboardDetailsResponse.setBookingDetails(bookingDetails);
		adminDashboardDetailsResponse
				.setTotalNoOfRecords(bookingDetails.size() > 0 ? bookingDetails.get(0).getTotalNoOfRecords() : 0);
		return adminDashboardDetailsResponse;
	}

	private List<Object[]> downloadReportDetails(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq) {
		List<Object[]> bookingDetailObjs;
		String query;
		if (adminDashboardDetailsReq.getLocationCode().equalsIgnoreCase("all")
				&& adminDashboardDetailsReq.getUserType() != null
				&& adminDashboardDetailsReq.getUserType().toString().equals(UserType.EMPLOYEE.toString())
				&& adminDashboardDetailsReq.getPageNo() == 0 && adminDashboardDetailsReq.getRecords() == 0) {
			query = "SELECT wi_mi.booking_id, wi_mi.booking_date, booking_type, wi_mi.workspace_type, wi_mi.workspace_codes, "
					+ "	location_code, floor_no, city, country, employee_id, employee_name, wi_mi.no_of_lunch, wi_mi.no_of_dinner, overall_count, requested_for, "
					+ "	wi_mi_pi.two_wheeler_parking_lot,  wi_mi_pi.four_wheeler_parking_lot FROM (SELECT workspace_items.booking_id, workspace_items.booking_date, workspace_items.booking_type,  employee_id, employee_name, "
					+ "	workspace_codes, workspace_type, location_code, floor_no, city, country, meal_items.no_of_lunch, meal_items.no_of_dinner, overall_count = COUNT(*) OVER() "
					+ "	FROM "
					+ "(SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, workspace_type, location_code, floor_no, city, country,  "
					+ "employee_id, employee_name FROM [booking].[workspace].[workspace_items] WHERE booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) AS workspace_items  "
					+ "INNER JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner  "
					+ "FROM [booking].[workspace].[meal_items] WHERE booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) as meal_items ON workspace_items.booking_id = meal_items.booking_id  "
					+ "AND workspace_items.booking_date = meal_items.booking_date) as wi_mi "
					+ "INNER JOIN (SELECT  two_wheeler_parking_lot, four_wheeler_parking_lot, booking_id, booking_date, requested_for  FROM  [booking].[workspace].[parking_items]  "
					+ "WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)  "
					+ "AND booking_type like (:bookingType)  AND status= 'BOOKED' AND  "
					+ "requested_for = :userType) AS wi_mi_pi ON wi_mi.booking_id = wi_mi_pi.booking_id AND wi_mi.booking_date = wi_mi_pi.booking_date ";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
					.setParameter("bookingType", "ADMIN%")
					.setParameter("userType", adminDashboardDetailsReq.getUserType().name()).getResultList();
		} else if (adminDashboardDetailsReq.getUserType() != null
				&& adminDashboardDetailsReq.getUserType().toString().equals(UserType.EMPLOYEE.toString())) {
			query = "SELECT wi_mi.booking_id, wi_mi.booking_date, booking_type, wi_mi.workspace_type, wi_mi.workspace_codes, "
					+ "location_code, floor_no, city, country, employee_id, employee_name, wi_mi.no_of_lunch, wi_mi.no_of_dinner, overall_count, requested_for, "
					+ "wi_mi_pi.two_wheeler_parking_lot,  wi_mi_pi.four_wheeler_parking_lot FROM (SELECT workspace_items.booking_id, workspace_items.booking_date, workspace_items.booking_type,  employee_id, employee_name, "
					+ "workspace_codes, workspace_type, location_code, floor_no, city, country, meal_items.no_of_lunch, meal_items.no_of_dinner, overall_count = COUNT(*) OVER() "
					+ " FROM (SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, workspace_type, location_code, floor_no, city, country,  "
					+ "employee_id, employee_name FROM [booking].[workspace].[workspace_items] WHERE employee_id IN (:employeeIds) AND (location_code=(:locationCode) OR floor_no=(:floorNo)) AND booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = 'EMPLOYEE') AS workspace_items  "
					+ " JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner  "
					+ "FROM [booking].[workspace].[meal_items] WHERE employee_id IN (:employeeIds) AND location_code=(:locationCode) AND booking_date>=(:bookingFromDate)  "
					+ "AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ "AND status= 'BOOKED' AND requested_for = :userType) as meal_items ON workspace_items.booking_id = meal_items.booking_id  "
					+ "AND workspace_items.booking_date = meal_items.booking_date) as wi_mi "
					+ " JOIN (SELECT  two_wheeler_parking_lot, four_wheeler_parking_lot, booking_id, booking_date, requested_for  FROM  [booking].[workspace].[parking_items]  "
					+ "WHERE employee_id IN (:employeeIds) AND location_code=(:locationCode) AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)  "
					+ "AND booking_type like (:bookingType)  AND status= 'BOOKED' AND  "
					+ "requested_for = :userType) AS wi_mi_pi ON wi_mi.booking_id = wi_mi_pi.booking_id AND wi_mi.booking_date = wi_mi_pi.booking_date ";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = fetchEmployeeReport(adminDashboardDetailsReq, query,
					adminDashboardDetailsReq.getPageNo(), "location");
		} else {
			query = "SELECT  * FROM ( SELECT workspaceItems.booking_id, workspaceItems.booking_type, workspaceItems.booking_date, "
					+ "workspaceItems.workspace_type,  workspaceItems.workspace_codes, workspaceItems.location_code, workspaceItems.floor_no,  "
					+ "workspaceItems.city, workspaceItems.country,  "
					+ "workspaceItems.employee_id, workspaceItems.employee_name,  mealItems.no_of_lunch, mealItems.no_of_dinner, overall_count = COUNT(*) OVER(), parkingItems.two_wheeler_parking_lots, parkingItems.four_wheeler_parking_lots"
					+ " FROM  (SELECT booking_id, booking_type, booking_date, workspace_code as workspace_codes, "
					+ "workspace_type, location_code, floor_no, city, country, employee_id, employee_name FROM [booking].[workspace]."
					+ "[workspace_items] WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)"
					+ " AND booking_type like (:bookingType) AND location_code=(:locationCode) OR floor_no=(:floorNo) AND status= 'BOOKED' "
					+ " AND requested_for = :userType) AS  workspaceItems JOIN "
					+ " (SELECT booking_date, STRING_AGG (two_wheeler_parking_lot, ',') as two_wheeler_parking_lots,  STRING_AGG "
					+ " (four_wheeler_parking_lot, ',') as four_wheeler_parking_lots FROM [booking].[workspace].[parking_items] WHERE "
					+ " booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND booking_type like (:bookingType) "
					+ " AND location_code=(:locationCode) AND status= 'BOOKED' "
					+ " AND requested_for = :userType GROUP BY booking_date) AS parkingItems ON "
					+ "workspaceItems.booking_date = parkingItems.booking_date "
					+ "JOIN (SELECT booking_id, booking_date, no_of_lunch, no_of_dinner FROM [booking].[workspace].[meal_items] "
					+ "WHERE booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) "
					+ "AND booking_type like (:bookingType) AND location_code=(:locationCode) AND status= 'BOOKED' AND requested_for = :userType) AS mealItems ON "
					+ "parkingItems.booking_date = mealItems.booking_date ) TMP ORDER BY booking_id, booking_date";
			logger.info(" BookingServiceHelper :: getAdminDashboardDetails : query." + query);
			bookingDetailObjs = entityManager.createNativeQuery(query)
					.setParameter("locationCode", adminDashboardDetailsReq.getLocationCode())
					.setParameter("floorNo", adminDashboardDetailsReq.getFloorNo())
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
					.setParameter("bookingType", "ADMIN%")
					.setParameter("userType", adminDashboardDetailsReq.getUserType().name()).getResultList();
		}
		return bookingDetailObjs;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> fetchEmployeeReport(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq, String query,
			int offsetNo, String locationType) {

		List<Object[]> bookingDetailObjList = new ArrayList<>();

		int batchSize = 100;

		// SQL Supports max of 2100 params only
		for (int count = 0; count < adminDashboardDetailsReq.getEmployeeIds().size(); count += batchSize) {

			List<String> subList = adminDashboardDetailsReq.getEmployeeIds().subList(count,
					Math.min(adminDashboardDetailsReq.getEmployeeIds().size(), count + batchSize));

			if (locationType.equalsIgnoreCase("all")) {
				bookingDetailObjList = entityManager.createNativeQuery(query)
						.setParameter("employeeIds", adminDashboardDetailsReq.getEmployeeIds())
						.setParameter("bookingFromDate",
								CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
						.setParameter("bookingToDate",
								CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
						.setParameter("bookingType", "ADMIN%")
						.setParameter("userType", adminDashboardDetailsReq.getUserType().name())
						.setParameter("pageNo", offsetNo).setParameter("records", adminDashboardDetailsReq.getRecords())
						.getResultList();
			} else if (locationType.equalsIgnoreCase("location")) {
				bookingDetailObjList.addAll(entityManager.createNativeQuery(query)
						.setParameter("locationCode", adminDashboardDetailsReq.getLocationCode())
						.setParameter("floorNo", adminDashboardDetailsReq.getFloorNo())
						.setParameter("employeeIds", subList)
						.setParameter("bookingFromDate",
								CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getFromDate()))
						.setParameter("bookingToDate",
								CommonUtility.stringToSQLDate(adminDashboardDetailsReq.getToDate()))
						.setParameter("bookingType", "ADMIN%").setParameter("pageNo", offsetNo)
						.setParameter("userType", adminDashboardDetailsReq.getUserType().name())
						.setParameter("records", adminDashboardDetailsReq.getRecords()).getResultList());
			}
		}
		return bookingDetailObjList;
	}

	@Override
	public AdminDashboardStatsResponseDTO getAdminDashboardStatsDetails(
			AdminDashboardStatsRequestDTO adminDashboardStatsReq) {

		AdminDashboardStatsResponseDTO adminDashboardStatsResp = new AdminDashboardStatsResponseDTO();
		List<Object[]> dashboardStatisticsObject = new ArrayList<>();

		if (adminDashboardStatsReq.getCity().equalsIgnoreCase("all")) {
			String query = "SELECT workspaceItems.avaialableSeats,workspaceSeatItems.totalSeats,workspcaBookItems.booked,parkingItems.totalTwoWheelerPlots,"
					+ " parkingItems.totalFourWheelerPlots,parkingItems.bookedTwoWheelerPlots,parkingItems.bookedFourWheelerPlots,mealItems.booking_date,"
					+ " mealItems.total_no_of_lunch, mealItems.total_no_of_dinner" + " FROM "
					+ " (SELECT booking_date,SUM(no_of_lunch) as total_no_of_lunch,SUM(no_of_dinner) as total_no_of_dinner from workspace.meal_items where booking_type like 'ADMIN%' AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) GROUP BY booking_date) as mealItems"
					+ " JOIN "
					+ " (SELECT booking_date,SUM(CAST(four_wheeler_parking_lot AS INT)) as totalFourWheelerPlots,SUM(CAST(two_wheeler_parking_lot AS INT)) as totalTwoWheelerPlots,"
					+ " COUNT(two_wheeler_parking_lot) as bookedTwoWheelerPlots ,COUNT(four_wheeler_parking_lot) as bookedFourWheelerPlots from workspace.parking_items where booking_type like 'ADMIN%' AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) GROUP BY booking_date) as parkingItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT (SELECT COUNT(workspace_code) as avaialbleSeats from workspace.workspace_master) - (SELECT COUNT(workspace_code) as avaialbleSeats from workspace.workspace_items where location_code=(:locationCode) AND status='BOOKED' AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)) AS avaialableSeats) AS workspaceItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT COUNT(workspace_code) as totalSeats from workspace.workspace_master) AS workspaceSeatItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT COUNT(status) as booked from workspace.workspace_items where booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND location_code=(:locationCode) AND status='BOOKED') as workspcaBookItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date";
			logger.info(" BookingServiceHelper :: getAdminDashboardStatsDetails : query." + query);
			dashboardStatisticsObject = entityManager.createNativeQuery(query)
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardStatsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardStatsReq.getToDate()))
					.setParameter("locationCode", adminDashboardStatsReq.getLocationCode()).getResultList();
		}

		else {
			String query = "SELECT workspaceItems.avaialableSeats,workspaceSeatItems.totalSeats,workspcaBookItems.booked,parkingItems.totalTwoWheelerPlots,"
					+ " parkingItems.totalFourWheelerPlots,parkingItems.bookedTwoWheelerPlots,parkingItems.bookedFourWheelerPlots,mealItems.booking_date,"
					+ " mealItems.total_no_of_lunch, mealItems.total_no_of_dinner" + " FROM "
					+ " (SELECT booking_date,SUM(no_of_lunch) as total_no_of_lunch,SUM(no_of_dinner) as total_no_of_dinner from workspace.meal_items where booking_type like 'ADMIN%' AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND location_code=(:locationCode) AND city=:city GROUP BY booking_date) as mealItems"
					+ " JOIN "
					+ " (SELECT booking_date,SUM(CAST(four_wheeler_parking_lot AS INT)) as totalFourWheelerPlots,SUM(CAST(two_wheeler_parking_lot AS INT)) as totalTwoWheelerPlots,"
					+ " COUNT(two_wheeler_parking_lot) as bookedTwoWheelerPlots ,COUNT(four_wheeler_parking_lot) as bookedFourWheelerPlots from workspace.parking_items where booking_type like 'ADMIN%' AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND location_code=(:locationCode) AND city=:city GROUP BY booking_date) as parkingItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT (SELECT COUNT(workspace_code) as avaialbleSeats from workspace.workspace_master) - (SELECT COUNT(workspace_code) as avaialbleSeats from workspace.workspace_items where location_code=(:locationCode) AND status='BOOKED' AND city=:city AND booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate)) AS avaialableSeats) AS workspaceItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT COUNT(workspace_code) as totalSeats from workspace.workspace_master) AS workspaceSeatItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date" + " JOIN "
					+ " (SELECT COUNT(status) as booked from workspace.workspace_items where booking_date>=(:bookingFromDate) AND booking_date<=(:bookingToDate) AND location_code=(:locationCode) AND city=:city AND status='BOOKED') as workspcaBookItems"
					+ " ON parkingItems.booking_date = mealItems.booking_date";
			logger.info(" BookingServiceHelper :: getAdminDashboardStatsDetails : query." + query);
			dashboardStatisticsObject = entityManager.createNativeQuery(query)
					.setParameter("bookingFromDate",
							CommonUtility.stringToSQLDate(adminDashboardStatsReq.getFromDate()))
					.setParameter("bookingToDate", CommonUtility.stringToSQLDate(adminDashboardStatsReq.getToDate()))
					.setParameter("city", adminDashboardStatsReq.getCity())
					.setParameter("locationCode", adminDashboardStatsReq.getLocationCode()).getResultList();

		}

		int totalTwoWhellerCount = getTwoWhellerParkinglot(adminDashboardStatsReq,
				adminDashboardStatsReq.getLocationCode(), ParkingLotType.TWO_WHEELER.toString());
		int totalForWhllercount = getFourWhellerParkinglot(adminDashboardStatsReq,
				adminDashboardStatsReq.getLocationCode(), ParkingLotType.FOUR_WHEELER.toString());

		for (Object[] dashboardStatisticsObjects : dashboardStatisticsObject) {
			DashboardStatistics dashboardStatistics = new DashboardStatistics();
			WorkspaceStatisticsDetails workspace = new WorkspaceStatisticsDetails();
			ParkingStatisticsDetails parking = new ParkingStatisticsDetails();
			MealStatisticsDetails meal = new MealStatisticsDetails();
			workspace
					.setAvailableSeats(dashboardStatisticsObjects[0] == null ? 0 : (int) dashboardStatisticsObjects[0]);
			workspace.setTotalSeats(dashboardStatisticsObjects[1] == null ? 0 : (int) dashboardStatisticsObjects[1]);
			workspace.setBookedSeats(dashboardStatisticsObjects[2] == null ? 0 : (int) dashboardStatisticsObjects[2]);
			parking.setTotalTwoWheelerSlots(totalTwoWhellerCount == 0 ? 0 : totalTwoWhellerCount);
			parking.setTotalFourWheelerSlots(totalForWhllercount == 0 ? 0 : (int) totalForWhllercount);
			parking.setBookedTwoWheelerSlots(
					dashboardStatisticsObjects[5] == null ? 0 : (int) dashboardStatisticsObjects[5]);
			parking.setBookedFourWheelerSlots(
					dashboardStatisticsObjects[6] == null ? 0 : (int) dashboardStatisticsObjects[6]);
			meal.setTotalNoOfLunch(dashboardStatisticsObjects[8] == null ? 0 : (int) dashboardStatisticsObjects[8]);
			meal.setTotalNoOfDinner(dashboardStatisticsObjects[9] == null ? 0 : (int) dashboardStatisticsObjects[9]);
			dashboardStatistics.setWorkspace(workspace);
			dashboardStatistics.setMeal(meal);
			dashboardStatistics.setParking(parking);
			adminDashboardStatsResp.setDashboardStats(dashboardStatistics);

		}

		return adminDashboardStatsResp;
	}

	private int getTwoWhellerParkinglot(AdminDashboardStatsRequestDTO adminDashboardStatsReq, String locationCode,
			String parkingType) {

		int twowheeller = 0;
		if (locationCode.contains("all")) {
			twowheeller = parkingItemsRepository.findByParkingLotType(ParkingLotType.TWO_WHEELER.toString());
		} else {
			twowheeller = parkingItemsRepository.findTwoFourWheelerLotNo(adminDashboardStatsReq.getLocationCode(),
					ParkingLotType.TWO_WHEELER.toString());
		}
		System.out.println("twowheeller" + twowheeller);
		return twowheeller;
	}

	private int getFourWhellerParkinglot(AdminDashboardStatsRequestDTO adminDashboardStatsReq, String locationCode,
			String parkingType) {

		int fourwheeller = 0;
		if (locationCode.contains("all")) {
			fourwheeller = parkingItemsRepository.findByParkingLotType(ParkingLotType.FOUR_WHEELER.toString());
		} else {
			fourwheeller = parkingItemsRepository.findTwoFourWheelerLotNo(adminDashboardStatsReq.getLocationCode(),
					ParkingLotType.TWO_WHEELER.toString());
		}
		System.out.println("fourwheeller" + fourwheeller);

		return fourwheeller;
	}

	@Override
	public List<Object> getBookingSearchDetails(BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO) {

		String employeeName = "%employeeName%";

		String query = "select employee_id,employee_name from workspace.workspace_items WHERE " + "employee_name LIKE :"
				+ employeeName + " AND location_code=:locationCode AND booking_date=:bookingDate AND status= 'BOOKED'";
		logger.info(" BookingServiceHelper :: getBookingSearchDetails : query." + query);

		List resultList = entityManager.createNativeQuery(query)
				.setParameter(employeeName, "%" + bookingSearchDetailsRequestDTO.getEmployeeName() + "%")
				.setParameter("bookingDate",
						CommonUtility.stringToSQLDate(bookingSearchDetailsRequestDTO.getRequestDate()))
				.setParameter("locationCode", bookingSearchDetailsRequestDTO.getLocationCode()).getResultList();
		List<Object[]> searchList = resultList;

		List<Object> bookingSearchList = new ArrayList<>();

		for (Object[] searchDetail : searchList) {
			BookingSearchDetailsResponseDTO searchDetailsResponseDTO = new BookingSearchDetailsResponseDTO();
			searchDetailsResponseDTO.setEmployeeId((String) searchDetail[0]);
			searchDetailsResponseDTO.setEmployeeName((String) searchDetail[1]);
			bookingSearchList.add(searchDetailsResponseDTO);
		}
		return bookingSearchList;

	}

	@Override
	public List<Object> getBookingSeatDetails(BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO) {

		String employeeName = "%employeeName%";
		String seatquery = "select location_code,workspace_code,workspace_type,floor_no,employee_id,employee_name from workspace.workspace_items WHERE "
				+ "employee_name like :" + employeeName
				+ " AND location_code=:locationCode AND booking_date=:bookingDate AND status= 'BOOKED'";
		logger.info(" BookingServiceHelper :: getBookingSeatDetails : seatquery." + seatquery);

		List resultList = entityManager.createNativeQuery(seatquery)
				.setParameter(employeeName, "%" + bookingSearchDetailsRequestDTO.getEmployeeName() + "%")
				.setParameter("bookingDate",
						CommonUtility.stringToSQLDate(bookingSearchDetailsRequestDTO.getRequestDate()))
				.setParameter("locationCode", bookingSearchDetailsRequestDTO.getLocationCode()).getResultList();
		List<Object[]> seatList = resultList;

		List<Object> bookingseatList = new ArrayList<>();

		for (Object[] seatDetail : seatList) {
			BookingSearchDetailsSeatLayoutResponseDTO detailsSeatLayoutResponseDTO = new BookingSearchDetailsSeatLayoutResponseDTO();
			detailsSeatLayoutResponseDTO.setLocationCode((String) seatDetail[0]);
			WorkspaceDetailsSeatDTO detailsSeatDTO = new WorkspaceDetailsSeatDTO();
			detailsSeatDTO.setWorkSpaceCode((String) seatDetail[1]);
			detailsSeatDTO.setWorkSpaceType((String) seatDetail[2]);
			detailsSeatDTO.setFloor((String) seatDetail[3]);
			detailsSeatDTO.setEmployeeId((String) seatDetail[4]);
			detailsSeatDTO.setEmployeeName((String) seatDetail[5]);
			detailsSeatLayoutResponseDTO.setWorkspaceDetails(detailsSeatDTO);
			bookingseatList.add(detailsSeatLayoutResponseDTO);
		}

		return bookingseatList;

	}

	@Override
	@SuppressWarnings("unchecked")
	public int getTotalSeatCount(BookingDetailsRequestDTO bookingDetailsReq) {
		int totalCount = 0;
		String totalSeatsQuery = "SELECT MAX(workspaceCount) as workspace_count FROM (SELECT count(*) as workspaceCount FROM  [booking].[workspace].[workspace_items] WHERE booking_date >= CAST( GETDATE() AS Date ) AND status='BOOKED' AND employee_id=:employeeId  AND booking_type=:bookingType GROUP BY booking_id, booking_date) wc";
		logger.info(
				" BookingServiceHelper :: getPreviousAndFutureBookingDetails : Upcoming : query." + totalSeatsQuery);
		List<Object> objs = entityManager.createNativeQuery(totalSeatsQuery)
				.setParameter("employeeId", bookingDetailsReq.getEmployeeId())
				.setParameter("bookingType", bookingDetailsReq.getBookingType().name()).getResultList();
		for (Object seatCount : objs) {
			totalCount = (int) seatCount;
		}
		return totalCount;
	}

	public static <T> List<List<T>> getBatches(List<T> collection, int batchSize) {
		int i = 0;
		List<List<T>> batches = new ArrayList<List<T>>();
		while (i < collection.size()) {
			int nextInc = Math.min(collection.size() - i, batchSize);
			List<T> batch = collection.subList(i, i + nextInc);
			batches.add(batch);
			i = i + nextInc;
		}

		return batches;
	}
}