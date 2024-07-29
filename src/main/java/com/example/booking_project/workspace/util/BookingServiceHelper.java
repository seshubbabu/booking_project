package com.example.booking_project.workspace.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletResponse;

import com.example.booking_project.workspace.entity.BookingDetailsEntity;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.booking_project.workspace.common.dto.BlockRequestDTO;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponse;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsResponseDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingCancellationResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsResponseDTO;
import com.example.booking_project.workspace.dto.BookingRequestDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.DefaultPreferencesRequestDTO;
import com.example.booking_project.workspace.dto.ODCSDTO;
import com.example.booking_project.workspace.dto.SelectedDeatilResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLocationResponseDTO;
import com.example.booking_project.workspace.dto.WorkstationTypesDTO;
import com.example.booking_project.workspace.entity.BookingDetailsEntity;
import com.example.booking_project.workspace.enums.BookingRequestType;
import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.ParkingLotType;
import com.example.booking_project.workspace.enums.WorkspaceType;
import com.example.booking_project.workspace.repository.BookingCustomRepository;
import com.example.booking_project.workspace.repository.BookingDetailsRepository;
import com.example.booking_project.workspace.repository.ParkingLotMasterRepository;
import com.example.booking_project.workspace.repository.WorkspaceLocationFlatResponseDTO;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Component
public class BookingServiceHelper {

	private static Logger logger = LoggerFactory.getLogger(BookingServiceHelper.class);

	@Autowired
	BookingCustomRepository bookingCustomRepository;

	@Autowired
	BookingDetailsRepository bookingDetailsRepository;

	@Autowired
	ParkingLotMasterRepository parkingItemsRepository;

	public static BookingDetailsEntity convertBookingReqDTOToBookingDetailsEntity(Object requestDTO) {

		BookingDetailsEntity bookingDetailsEntity = new BookingDetailsEntity();

		if (requestDTO instanceof BookingRequestDTO) {
			BookingRequestDTO bookingRequest = (BookingRequestDTO) requestDTO;
			bookingDetailsEntity.setBookingType(bookingRequest.getBookingType());
			bookingDetailsEntity.setRequestedId(bookingRequest.getRequesterId());
			bookingDetailsEntity.setRequesterName(bookingRequest.getRequesterName());
			bookingDetailsEntity.setRequestedDate(CommonUtility.stringToSQLDate((bookingRequest.getRequestedDate())));
			bookingDetailsEntity.setUserType(bookingRequest.getRequestedFor());
			bookingDetailsEntity.setCreatedBy(bookingRequest.getRequesterId());
			bookingDetailsEntity.setLastModifiedBy(bookingRequest.getRequesterId());
			bookingDetailsEntity
					.setBookingStartDate(CommonUtility.stringToSQLDate(bookingRequest.getBookingStartDate()));
			bookingDetailsEntity.setBookingEndDate(CommonUtility.stringToSQLDate(bookingRequest.getBookingEndDate()));
			bookingDetailsEntity.setCreatedBy(bookingRequest.getRequesterId());
			bookingDetailsEntity.setLastModifiedBy(bookingRequest.getRequesterId());
			bookingDetailsEntity.setStatus(BookingStatusType.BOOKED);
		} else if (requestDTO instanceof BlockRequestDTO) {
			BlockRequestDTO blockRequest = (BlockRequestDTO) requestDTO;
			bookingDetailsEntity.setBookingType(blockRequest.getBookingType());
			bookingDetailsEntity.setRequestedId(blockRequest.getRequesterId());
			bookingDetailsEntity.setRequesterName(blockRequest.getRequesterName());
			bookingDetailsEntity.setRequestedDate(CommonUtility.stringToSQLDate((blockRequest.getRequestedDate())));
			bookingDetailsEntity.setUserType(blockRequest.getRequestedFor());
			bookingDetailsEntity.setCreatedBy(blockRequest.getRequesterId());
			bookingDetailsEntity.setLastModifiedBy(blockRequest.getRequesterId());
			bookingDetailsEntity.setBookingStartDate(CommonUtility.stringToSQLDate(blockRequest.getBookingStartDate()));
			bookingDetailsEntity.setBookingEndDate(CommonUtility.stringToSQLDate(blockRequest.getBookingEndDate()));
			bookingDetailsEntity.setCreatedBy(blockRequest.getRequesterId());
			bookingDetailsEntity.setLastModifiedBy(blockRequest.getRequesterId());
			bookingDetailsEntity.setStatus(BookingStatusType.BOOKED);
		}

		return bookingDetailsEntity;
	}

	public CommonResponseDTO<List<WorkspaceLocationResponseDTO>> getBookingLocationDetails() throws ParseException {

		CommonResponseDTO<List<WorkspaceLocationResponseDTO>> locationCommonResp = new CommonResponseDTO<List<WorkspaceLocationResponseDTO>>();
		List<WorkspaceLocationResponseDTO> locationMasterList = new ArrayList<>();

		List<WorkspaceLocationFlatResponseDTO> locationFlatList = bookingCustomRepository.getlocationDetails();
		Map<String, Map<String, Map<String, List<WorkspaceLocationFlatResponseDTO>>>> workstationTypes = new TreeMap<>();

		workstationTypes = locationFlatList.stream()
				.collect(Collectors.groupingBy(WorkspaceLocationFlatResponseDTO::getLocationCode,
						Collectors.groupingBy(WorkspaceLocationFlatResponseDTO::getWorkspaceName,
								Collectors.groupingBy(WorkspaceLocationFlatResponseDTO::getFloorNo))));

		Set<WorkspaceLocationFlatResponseDTO> locationFilteredSet = locationFlatList.stream()
				.collect(Collectors.toCollection(
						() -> new TreeSet<>(Comparator.comparing(WorkspaceLocationFlatResponseDTO::getLocationCode))));

		List<WorkspaceLocationFlatResponseDTO> sortedList = locationFilteredSet.stream()
				.sorted(Comparator.comparing(WorkspaceLocationFlatResponseDTO::getLocationId))
				.collect(Collectors.toList());

		for (WorkspaceLocationFlatResponseDTO workspaceLocationFlatResp : sortedList) {

			WorkspaceLocationResponseDTO workspaceLocationResp = new WorkspaceLocationResponseDTO();
			Map<String, WorkstationTypesDTO> workstationTypesMap = new TreeMap<>();
			Map<String, ODCSDTO> odcDTOMap = new TreeMap<>();

			workspaceLocationResp.setLocationCode(workspaceLocationFlatResp.getLocationCode());
			workspaceLocationResp.setLocationName(workspaceLocationFlatResp.getLocationName());
			workspaceLocationResp.setCity(workspaceLocationFlatResp.getCity());
			workspaceLocationResp.setState(workspaceLocationFlatResp.getState());
			workspaceLocationResp.setCountry(workspaceLocationFlatResp.getCountry());
			workstationTypes.get(workspaceLocationFlatResp.getLocationCode()).entrySet().forEach(entry -> {
				List<String> nonODCfloors = new ArrayList<>();
				List<String> odcfloors = new ArrayList<>();

				entry.getValue().entrySet().forEach(entry1 -> {
					if (entry.getKey().equals(WorkspaceType.CABIN.toString())
							|| entry.getKey().equals(WorkspaceType.CUBICAL.toString())) {
						nonODCfloors.add(entry1.getKey());
						workstationTypesMap.put(WorkspaceType.getEnumByString(entry.getKey()),
								new WorkstationTypesDTO(nonODCfloors));
					} else {
						odcfloors.add(entry1.getKey());
						odcDTOMap.put(entry.getKey(), new ODCSDTO(odcfloors));
					}
				});
			});
			workspaceLocationResp.setWorkstationTypes(workstationTypesMap);
			workspaceLocationResp.setOdcs(odcDTOMap);
			locationMasterList.add(workspaceLocationResp);
		}

		locationCommonResp.setStatus(HttpStatus.OK.value());
		locationCommonResp.setData(locationMasterList);
		return locationCommonResp;
	}

	public List<BookingDetailsResponseDTO> getBookingDetails(BigInteger bookingId) throws ParseException {
		return bookingCustomRepository.getBookingDetails(bookingId);
	}

	public WorkspaceLayoutResponseDTO getWorkspaceLayoutDetails(WorkspaceLayoutRequestDTO workspaceLayoutRequest,
			BigInteger locationMasterId) {

		logger.info(" BookingServiceHelper :: getWorkspaceLayoutDetails : STARTED.");

		WorkspaceLayoutResponseDTO workspaceLayoutResponse = new WorkspaceLayoutResponseDTO();
		Map<String, List<WorkspaceLayoutDTO>> layoutDetailsMap = new TreeMap<>();
		workspaceLayoutRequest.getRequestedDate().stream().forEach(requestedDate -> {
			List<WorkspaceLayoutDTO> workspaceLayoutList = bookingCustomRepository.getWorkspaceLayoutDetails(
					CommonUtility.stringToSQLDate(requestedDate), workspaceLayoutRequest.getWorkspaceType().toString(),
					workspaceLayoutRequest.getLocationCode(), workspaceLayoutRequest.getFloorNo(), locationMasterId);
			if (workspaceLayoutList.size() > 0) {
				workspaceLayoutResponse.setWorkspaceType(workspaceLayoutList.get(0).getWorkspaceType());
				workspaceLayoutResponse.setLocationCode(workspaceLayoutList.get(0).getLocationCode());
				workspaceLayoutResponse.setFloorNo(workspaceLayoutList.get(0).getFloorNo());
			}
			layoutDetailsMap.put(requestedDate, workspaceLayoutList);
		});

		workspaceLayoutResponse.setWorkspaceDetails(layoutDetailsMap);

		logger.info(" BookingServiceHelper :: getWorkspaceLayoutDetails : ENDED.");

		return workspaceLayoutResponse;
	}

	public BookingCancellationResponseDTO cancelBooking(BookingCancellationRequestDTO bookingCancellationRequest) {

		logger.info(" BookingServiceHelper :: cancelBooking : STARTED.");

		BookingCancellationResponseDTO bookingCancellationResponse = new BookingCancellationResponseDTO();
		bookingCustomRepository.cancelBooking(bookingCancellationRequest);

		logger.info(" BookingServiceHelper :: cancelBooking : ENDED.");

		return bookingCancellationResponse;
	}

	public List<BookingDetailResponseDTO> getEmployeePreviousAndFutureBookingDetails(
			BookingDetailsRequestDTO bookingDetailsReq) {

		logger.info(" BookingServiceHelper :: getEmployeePreviousAndFutureBookingDetails : STARTED.");

		List<BookingDetailResponseDTO> bookingDetailResponseList = new ArrayList<>();

		List<BookingDetailsResponseDTO> bookingDetailsList = bookingCustomRepository
				.getPreviousAndFutureBookingDetails(bookingDetailsReq);

		Map<Object, List<BookingDetailsResponseDTO>> bookingDetailsMap = bookingDetailsList.stream()
				.collect(Collectors.groupingBy(map -> map.getBookingId(),
						Collectors.mapping(map -> new BookingDetailsResponseDTO(map.getBookingId(),
								map.getBookingType(), map.getBookingDate(), map.getRequesterId(), map.getEmployeeId(),
								map.getEmployeeName(), map.getRequesterName(), map.getRequestedDate(),
								map.getWorkspaceType(), map.getLocationCode(), map.getFloorNo(), map.getCity(),
								map.getCountry(), map.getWorkspaceCodes(), map.getTwoWheelerSlots(),
								map.getFourWheelerSlots(), map.getNoOfLunch(), map.getNoOfDinner(), map.getStatus(), 0,
								map.getPreviousBookedSeatStatus(), map.getNoOfTwoLotsAvailable(),
								map.getNoOfFourLotsAvailable(), map.getNoOfSeatsAvailable()), Collectors.toList())));

		bookingDetailsMap.entrySet().stream().forEach(bookingDetail -> {
			int totalSeats = 0;
			if (BookingRequestType.UPCOMING.toString().equalsIgnoreCase(bookingDetailsReq.getRequestType().toString())
					&& BookingType.AVP_BULK.toString()
							.equalsIgnoreCase(bookingDetailsReq.getBookingType().toString())) {
				totalSeats = bookingCustomRepository.getTotalSeatCount(bookingDetailsReq);
			}
			BookingDetailResponseDTO bookingResponseDTO = new BookingDetailResponseDTO();
			bookingResponseDTO.setBookingId((BigInteger) bookingDetail.getKey());
			bookingResponseDTO.setTotalNoOfSeats(totalSeats);
			Map<String, SelectedDeatilResponseDTO> allocatedDeatils = new TreeMap<>();
			bookingDetail.getValue().stream().forEach(bookingDetailObj -> {
				BookingDetailsEntity bookingDetailsEntity = bookingDetailsRepository
						.findByBookingId((BigInteger) bookingDetail.getKey());
				bookingResponseDTO.setBookingType(bookingDetailsReq.getBookingType().name());
				bookingResponseDTO.setRequesterId(bookingDetailsEntity.getRequestedId());
				bookingResponseDTO.setRequesterName(bookingDetailsEntity.getRequesterName());
				bookingResponseDTO.setRequestType(bookingDetailsReq.getRequestType().name());
				bookingResponseDTO
						.setRequestedDate(CommonUtility.sQLDateToString(bookingDetailsEntity.getRequestedDate()));
				bookingResponseDTO
						.setBookingStartDate(CommonUtility.sQLDateToString(bookingDetailsEntity.getBookingStartDate()));
				bookingResponseDTO
						.setBookingEndDate(CommonUtility.sQLDateToString(bookingDetailsEntity.getBookingEndDate()));
				bookingResponseDTO.setWorkspaceType(bookingDetailObj.getWorkspaceType());
				bookingResponseDTO.setLocationCode(bookingDetailObj.getLocationCode());
				bookingResponseDTO.setFloorNo(bookingDetailObj.getFloorNo());
				bookingResponseDTO.setCity(bookingDetailObj.getCity());
				bookingResponseDTO.setCountry(bookingDetailObj.getCountry());
				List<String> workspaceCodes = (null != bookingDetailObj.getWorkspaceCodes())
						? Stream.of(bookingDetailObj.getWorkspaceCodes().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				List<String> twoWheelerSlots = (null != bookingDetailObj.getTwoWheelerSlots())
						? Stream.of(bookingDetailObj.getTwoWheelerSlots().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				List<String> fourWheelerSlots = (null != bookingDetailObj.getFourWheelerSlots())
						? Stream.of(bookingDetailObj.getFourWheelerSlots().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				String parkingType = ((twoWheelerSlots.size() == 0 && fourWheelerSlots.size() == 0)
						? ParkingLotType.NONE.toString()
						: twoWheelerSlots.size() > 0 && !(fourWheelerSlots.size() > 0)
								? ParkingLotType.TWO_WHEELER.toString()
								: (!(twoWheelerSlots.size() > 0) && fourWheelerSlots.size() > 0
										? ParkingLotType.FOUR_WHEELER.toString()
										: ParkingLotType.BOTH.toString()));
				allocatedDeatils.put(bookingDetailObj.getBookingDate(),
						new SelectedDeatilResponseDTO(bookingDetailObj.getEmployeeId(),
								bookingDetailObj.getEmployeeName(), workspaceCodes, bookingDetailObj.getNoOfLunch(),
								bookingDetailObj.getNoOfDinner(), parkingType, twoWheelerSlots, fourWheelerSlots,
								bookingDetailObj.getStatus(), 0, 0, 0));

				bookingResponseDTO.setNoOfTwoLotsAvailable(bookingDetailObj.getNoOfTwoLotsAvailable());
				bookingResponseDTO.setNoOfFourLotsAvailable(bookingDetailObj.getNoOfFourLotsAvailable());
				bookingResponseDTO.setNoOfSeatsAvailable(bookingDetailObj.getNoOfSeatsAvailable());
				bookingResponseDTO.setPreviousBookedSeatStatus(bookingDetailObj.getPreviousBookedSeatStatus());
				bookingResponseDTO.setAllocatedDeatils(allocatedDeatils);
				int noOfTwoLotsAvailable = getTwoWhellerParkinglot(bookingDetailsReq.getLocationCode(),
						ParkingLotType.TWO_WHEELER.toString());
				int noOfFourLotsAvailable = getFourWhellerParkinglot(bookingDetailsReq.getLocationCode(),
						ParkingLotType.FOUR_WHEELER.toString());
				int avaialableSeats = getAvaialbleSeats(bookingDetailsReq.getLocationCode());
				String bookedSeatStatus = getBookedSeatStatus(bookingDetailsReq.getLocationCode(),
						bookingResponseDTO.getWorkspaceType());
				bookingResponseDTO.setNoOfSeatsAvailable(avaialableSeats);
				bookingResponseDTO.setNoOfTwoLotsAvailable(noOfTwoLotsAvailable);
				bookingResponseDTO.setNoOfFourLotsAvailable(noOfFourLotsAvailable);
				bookingResponseDTO.setPreviousBookedSeatStatus(bookedSeatStatus);
				if (bookedSeatStatus == null) {
					bookingResponseDTO.setPreviousBookedSeatStatus("AVAILABLE");
				}
				System.out.println(bookingResponseDTO.getPreviousBookedSeatStatus());
			});
			bookingDetailResponseList.add(bookingResponseDTO);
		});

		logger.info(" BookingServiceHelper :: getEmployeePreviousAndFutureBookingDetails : ENDED.");

		return bookingDetailResponseList;
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
		return bookedSeatStatus;
	}

	public AdminDashboardDetailsResponseDTO getAdminDashboardDetails(
			AdminDashboardDetailsRequestDTO adminDashboardDetailsReq) {
		logger.info(" BookingServiceHelper :: getAdminDashboardDetails : adminDashboardDetailsReq : STARTED.");
		AdminDashboardDetailsResponseDTO adminDashboardDetailsResponse = new AdminDashboardDetailsResponseDTO();
		adminDashboardDetailsResponse = bookingCustomRepository.getAdminDashboardDetails(adminDashboardDetailsReq);
		logger.info(" BookingServiceHelper :: getAdminDashboardDetails : adminDashboardDetailsReq : ENDED.");
		return adminDashboardDetailsResponse;
	}

	public AdminDashboardStatsResponseDTO getAdminDashboardStats(
			AdminDashboardStatsRequestDTO adminDashboardDetailsReq) {
		logger.info(" BookingServiceHelper :: getAdminDashboardStats :  STARTED.");
		AdminDashboardStatsResponseDTO adminDashboardStatsResponseDTO = new AdminDashboardStatsResponseDTO();
		adminDashboardStatsResponseDTO = bookingCustomRepository
				.getAdminDashboardStatsDetails(adminDashboardDetailsReq);
		logger.info(" BookingServiceHelper :: getAdminDashboardStats :  ENDED.");
		return adminDashboardStatsResponseDTO;
	}

	public byte[] downloadAdminDashboardDetails(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq,
			HttpServletResponse response) throws IOException, NumberFormatException {
		AdminDashboardDetailsResponseDTO adminDashboardDetailsResponse = new AdminDashboardDetailsResponseDTO();
		adminDashboardDetailsResponse = bookingCustomRepository.getAdminDashboardDetails(adminDashboardDetailsReq);

		List<AdminDashboardDetailsResponse> finalList = adminDashboardDetailsResponse.getBookingDetails();

		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] columns = { "BookingId", "BookingType", "BookingDate", "UserType", "WorkspaceCodes", "LocationCode",
				"FloorNo", "TwoWheelerLots", "FourWheelerLots", "WorkspaceType", "EmployeeId", "EmployeeName", "City",
				"Country" };

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		XSSFSheet sheet = workbook.createSheet("Employee");

		// Create a Font for styling header cells
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		XSSFCellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.DIAMONDS);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Create cells
		for (int i = 0; i < columns.length; i++) {
			XSSFCell cell = (XSSFCell) headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		int rowNum = 1;
		for (AdminDashboardDetailsResponse bookingDetails : finalList) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(bookingDetails.getBookingId());
			row.createCell(1).setCellValue(bookingDetails.getBookingType());
			row.createCell(2).setCellValue(bookingDetails.getBookingDate());
			headerCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
			XSSFCell cell = (XSSFCell) row.createCell(2);
			cell.setCellValue(bookingDetails.getBookingDate());
			row.createCell(3).setCellValue(bookingDetails.getUserType());
			row.createCell(4).setCellValue(bookingDetails.getWorkspaceCodes());
			row.createCell(5).setCellValue(bookingDetails.getLocationCode());
			row.createCell(6).setCellValue(bookingDetails.getFloorNo());
			row.createCell(7).setCellValue(bookingDetails.getTwoWheelerLots());
			row.createCell(8).setCellValue(bookingDetails.getFourWheelerLots());
			row.createCell(9).setCellValue(bookingDetails.getWorkspaceType());
			row.createCell(10).setCellValue(bookingDetails.getEmployeeId().toString());
			row.createCell(11).setCellValue(bookingDetails.getEmployeeName());
			row.createCell(12).setCellValue(bookingDetails.getCity());
			row.createCell(13).setCellValue(bookingDetails.getCountry());
			cell.setCellStyle(style);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		workbook.close();
		return baos.toByteArray();
	}

	public List<Object> getBookingSearchDetails(BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO) {

		if (bookingSearchDetailsRequestDTO.getAction().equalsIgnoreCase("employee")) {
			return bookingCustomRepository.getBookingSearchDetails(bookingSearchDetailsRequestDTO);
		} else if (bookingSearchDetailsRequestDTO.getAction().equalsIgnoreCase("seat")) {
			return bookingCustomRepository.getBookingSeatDetails(bookingSearchDetailsRequestDTO);
		}
		return null;
	}

	public Object makeAsDefaultPrefrence(DefaultPreferencesRequestDTO defaultPreferencesReq) {
		logger.info(" BookingServiceHelper :: makeAsDefaultPrefrence :  STARTED.");
		int updatedCount = 0;
		if (defaultPreferencesReq.getBookingType().equals(BookingType.MARTIAN)
				|| defaultPreferencesReq.getBookingType().equals(BookingType.AVP_SELF)
				|| defaultPreferencesReq.getBookingType().equals(BookingType.ADMIN_SELF)) {
			updatedCount = bookingDetailsRepository.updateDefaultBookingStatus(defaultPreferencesReq.getBookingId(),
					CommonUtility.stringToSQLDate(defaultPreferencesReq.getBookingDate()),
					defaultPreferencesReq.isStatus(), defaultPreferencesReq.getBookingType());
		} else {
			Map<String, String> businessErrorList = new TreeMap<>();
			throw new NoDefaultPrefrenceException("B-S-101-003", "Cannot made as Default pref for the current role",
					businessErrorList);
		}
		logger.info(" BookingServiceHelper :: makeAsDefaultPrefrence :  ENDED.");
		return updatedCount > 0 ? "Sucessfully Updated as Default Preferences"
				: "Failed to Update the Default Preferences";
	}

}
