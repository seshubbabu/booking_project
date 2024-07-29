package com.example.booking_project.workspace.service.impl;

import java.sql.Date;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.booking_project.common.exception.NoSufficientParkingLotsException;
import com.example.booking_project.common.exception.SeatAlreadyBookedException;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardDetailsResponseDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsRequestDTO;
import com.example.booking_project.workspace.dto.AdminDashboardStatsResponseDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRequestDTO;
import com.example.booking_project.workspace.dto.BookingCancellationRespDetails;
import com.example.booking_project.workspace.dto.BookingCancellationResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailResponseDTO;
import com.example.booking_project.workspace.dto.BookingDetailsRequestDTO;
import com.example.booking_project.workspace.dto.BookingDetailsResponseDTO;
import com.example.booking_project.workspace.dto.BookingRequestDTO;
import com.example.booking_project.workspace.dto.BookingResponseDTO;
import com.example.booking_project.workspace.dto.BookingSearchDetailsRequestDTO;
import com.example.booking_project.workspace.dto.DefaultPreferencesRequestDTO;
import com.example.booking_project.workspace.dto.OfficeWorkspaceResponseDTO;
import com.example.booking_project.workspace.dto.SelectedDeatilRequestDTO;
import com.example.booking_project.workspace.dto.SelectedDeatilResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutRequestDTO;
import com.example.booking_project.workspace.dto.WorkspaceLayoutResponseDTO;
import com.example.booking_project.workspace.dto.WorkspaceLocationResponseDTO;
import com.example.booking_project.workspace.entity.BookingDetailsEntity;
import com.example.booking_project.workspace.entity.LocationMasterEntity;
import com.example.booking_project.workspace.entity.MealItemsEntity;
import com.example.booking_project.workspace.entity.ParkingItemsEntity;
import com.example.booking_project.workspace.entity.WorkspaceItemsEntity;
import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.ParkingLotType;
import com.example.booking_project.workspace.repository.BookingDetailsRepository;
import com.example.booking_project.workspace.repository.LocationMasterRepository;
import com.example.booking_project.workspace.repository.MealItemBookingRepository;
import com.example.booking_project.workspace.repository.ParkingItemBookingRepository;
import com.example.booking_project.workspace.repository.ParkingLotMasterRepository;
import com.example.booking_project.workspace.repository.WorkspaceItemsBookingRepository;
import com.example.booking_project.workspace.service.WorkspaceBookingService;
import com.example.booking_project.workspace.util.BookingServiceHelper;
import com.example.booking_project.workspace.util.CommonUtility;
import com.example.booking_project.workspace.entity.BookingDetailsEntity;
/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Service
public class WorkspaceBookingServiceImpl implements WorkspaceBookingService {

	private static Logger logger = LoggerFactory.getLogger(WorkspaceBookingServiceImpl.class);

	@Autowired
	BookingDetailsRepository bookingDetailsRepository;

	@Autowired
	WorkspaceItemsBookingRepository workspaceItemsRepository;

	@Autowired
	MealItemBookingRepository mealItemsRepository;

	@Autowired
	ParkingLotMasterRepository parkingLotMasterRepository;

	@Autowired
	ParkingItemBookingRepository parkingItemsRepository;

	@Autowired
	BookingServiceHelper bookingServiceHelper;

	@Autowired
	LocationMasterRepository locationMasterRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
	@Override
	public CommonResponseDTO<?> createBooking(BookingRequestDTO bookingReqDTO) throws ParseException {

		CommonResponseDTO<?> commonReponse = new CommonResponseDTO<>();
		Map<String, SelectedDeatilRequestDTO> selectedDeatils = bookingReqDTO.getSelectedDeatils();

		if (null == bookingReqDTO.getBookingId()) {

			logger.info("WorkspaceBookingServiceImpl :: assignWorkspace :: STARTED");

			// 1.) Seat No. Availability Check
			checkRequestedSeatNosAvailability(bookingReqDTO);

			// Create Booking Details
			BookingDetailsEntity bookingDetailsEntity = BookingServiceHelper
					.convertBookingReqDTOToBookingDetailsEntity(bookingReqDTO);
			bookingDetailsEntity = bookingDetailsRepository.save(bookingDetailsEntity);

			BigInteger bookingId = null != bookingDetailsEntity.getBookingId()
					? bookingDetailsEntity.getBookingId().abs()
					: new BigInteger("0");

			selectedDeatils.entrySet().stream().forEach(selectedDeatil -> {

				// 2.) Book Seat
				allocateSeatNoToCurrentBooking(bookingReqDTO, selectedDeatil, bookingId);

				// 3.) Fetching Next Parking Lot No
				Map<String, List<String>> allocatedNoMap = fetchRequiredParkingLotNos(bookingReqDTO, bookingId,
						selectedDeatil, "create");

				// 4.) Allocate Parking Lot No
				allocateParkingLotNoToCurrentBooking(bookingReqDTO, selectedDeatil, bookingId,
						allocatedNoMap.get(ParkingLotType.TWO_WHEELER.toString()),
						allocatedNoMap.get(ParkingLotType.FOUR_WHEELER.toString()), "create");

				// 5.) Book meal
				addMealNoToCurrentBooking(bookingReqDTO, selectedDeatil, bookingId);

			});

			BookingResponseDTO bookingResponseDTO = fetchBookingDetailsByBookingId(bookingId);
			commonReponse.setStatus(HttpStatus.OK.value());
			commonReponse.setData(bookingResponseDTO);

			logger.info("WorkspaceBookingServiceImpl :: assignWorkspace :: ENDED");

		} else {

			logger.info("WorkspaceBookingServiceImpl :: update Booking Details :: STARTED");

			selectedDeatils.entrySet().forEach(selectedDeatil -> {

				// Update workspace Details
				selectedDeatil.getValue().getWorkspaceCodes().stream().forEach(workspaceCode -> {
					workspaceItemsRepository.updateWorkspaceDetails(bookingReqDTO.getBookingId(),
							CommonUtility.stringToSQLDate(selectedDeatil.getKey()), workspaceCode);
				});

				// update meal details
				mealItemsRepository.updateMealItemsDetails(bookingReqDTO.getBookingId(),
						CommonUtility.stringToSQLDate(selectedDeatil.getKey()),
						selectedDeatil.getValue().getNoOfLunch(), selectedDeatil.getValue().getNoOfDinner());

				// update parking lot details
				updateOrAllocateParkingLots(bookingReqDTO, bookingReqDTO.getBookingId(), selectedDeatil);
			});

			BookingResponseDTO bookingResponseDTO = fetchBookingDetailsByBookingId(bookingReqDTO.getBookingId());
			commonReponse.setStatus(HttpStatus.OK.value());
			commonReponse.setData(bookingResponseDTO);

			logger.info("WorkspaceBookingServiceImpl :: update Booking Details :: ENDED");
		}

		return commonReponse;
	}

	private void updateOrAllocateParkingLots(BookingRequestDTO bookingReqDTO, BigInteger bookingId,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil) {

		Map<String, List<String>> allocatedNoMap = new TreeMap<>();

		List<String> twparkingItemsList = parkingItemsRepository.findAllocatedTwoWheelerLots(bookingId,
				CommonUtility.stringToSQLDate(selectedDeatil.getKey()), bookingReqDTO.getLocationCode(),
				selectedDeatil.getValue().getEmployeeId());

		if (selectedDeatil.getValue().getNoOfTwoWheeler() > 0) {

			int i = 0;
			while (i < selectedDeatil.getValue().getNoOfTwoWheeler() - twparkingItemsList.size()) {
				String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated2WParkingLotNo(
						CommonUtility.stringToSQLDate(selectedDeatil.getKey()).toString(),
						bookingReqDTO.getLocationCode());
				allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), allocateParkingNo(bookingReqDTO, bookingId,
						selectedDeatil, lastAllocatedLotNo, ParkingLotType.TWO_WHEELER.toString()));
				List<String> twoWheelerLotList = allocatedNoMap.get(ParkingLotType.TWO_WHEELER.toString());
				if (twoWheelerLotList.size() > 0) {
					for (String twoWheelerNo : twoWheelerLotList) {
						ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
						parkingItemsEntity.setBookingId(bookingId);
						parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
						parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
						parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
						parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
						parkingItemsEntity.setTwoWheelerNo(twoWheelerNo);
						parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
						parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
						parkingItemsEntity.setCity(bookingReqDTO.getCity());
						parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
						parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
						parkingItemsRepository.save(parkingItemsEntity);
					}
				}
				i++;
			}

		} else {
			if (!twparkingItemsList.isEmpty()) {
				parkingItemsRepository.cancel2WParking(bookingId,
						CommonUtility.stringToSQLDate(selectedDeatil.getKey()),
						selectedDeatil.getValue().getEmployeeId());
			}
			allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), twparkingItemsList);
		}
		List<String> fwparkingItemsList = parkingItemsRepository.findAllocatedFourWheelerLots(bookingId,
				CommonUtility.stringToSQLDate(selectedDeatil.getKey()), bookingReqDTO.getLocationCode(),
				selectedDeatil.getValue().getEmployeeId());
		if (selectedDeatil.getValue().getNoOfFourWheeler() > 0) {

			int i = 0;
			while (i < selectedDeatil.getValue().getNoOfFourWheeler() - fwparkingItemsList.size()) {
				String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated4WParkingLotNo(
						CommonUtility.stringToSQLDate(selectedDeatil.getKey()).toString(),
						bookingReqDTO.getLocationCode());
				allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), allocateParkingNo(bookingReqDTO, bookingId,
						selectedDeatil, lastAllocatedLotNo, ParkingLotType.FOUR_WHEELER.toString()));

				List<String> fourWheelerLotList = allocatedNoMap.get(ParkingLotType.FOUR_WHEELER.toString());
				if (fourWheelerLotList.size() > 0) {
					for (String fourWheelerNo : fourWheelerLotList) {
						ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
						parkingItemsEntity.setBookingId(bookingId);
						parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
						parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
						parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
						parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
						parkingItemsEntity.setFourWheelerNo(fourWheelerNo);
						parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
						parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
						parkingItemsEntity.setCity(bookingReqDTO.getCity());
						parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
						parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
						parkingItemsRepository.save(parkingItemsEntity);
					}

				}
				i++;
			}
		} else {
			if (!fwparkingItemsList.isEmpty()) {
				parkingItemsRepository.cancel4WParking(bookingId,
						CommonUtility.stringToSQLDate(selectedDeatil.getKey()),
						selectedDeatil.getValue().getEmployeeId());
			}
			allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), fwparkingItemsList);
		}
		if (selectedDeatil.getValue().getNoOfTwoWheeler() == 0 && selectedDeatil.getValue().getNoOfFourWheeler() == 0) {
			addEmptyParkingLotNo(bookingReqDTO, selectedDeatil);
		}

	}

	private void addEmptyParkingLotNo(BookingRequestDTO bookingReqDTO,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil) {
		ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
		parkingItemsEntity.setBookingId(bookingReqDTO.getBookingId());
		parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
		parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
		parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
		parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
		parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
		parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
		parkingItemsEntity.setCity(bookingReqDTO.getCity());
		parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
		parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
		parkingItemsRepository.save(parkingItemsEntity);
	}

	private void allocateSeatNoToCurrentBooking(BookingRequestDTO bookingReqDTO,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, BigInteger bookingId) {
		SelectedDeatilRequestDTO selectedDeatilRequest = selectedDeatil.getValue();

		selectedDeatil.getValue().getWorkspaceCodes().stream().forEach(workspaceCode -> {
			WorkspaceItemsEntity workspaceItemsEntity = new WorkspaceItemsEntity();
			workspaceItemsEntity.setBookingId(bookingId);
			workspaceItemsEntity.setBookingType(bookingReqDTO.getBookingType());
			workspaceItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
			workspaceItemsEntity.setEmployeeId(selectedDeatilRequest.getEmployeeId());
			workspaceItemsEntity.setDepartment(bookingReqDTO.getDepartmentName());
			workspaceItemsEntity.setDivision(bookingReqDTO.getDivisionName());
			workspaceItemsEntity.setEmployeeName(selectedDeatilRequest.getEmployeeName());
			workspaceItemsEntity.setUserType(bookingReqDTO.getRequestedFor());
			workspaceItemsEntity.setWorkspaceCode(workspaceCode);
			workspaceItemsEntity.setWorkspaceType(bookingReqDTO.getWorkspaceType());
			workspaceItemsEntity.setCity(bookingReqDTO.getCity());
			workspaceItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
			workspaceItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
			workspaceItemsEntity.setCountry(bookingReqDTO.getCountry());
			workspaceItemsEntity.setStatus(BookingStatusType.BOOKED);
			workspaceItemsRepository.save(workspaceItemsEntity);
		});
	}

	public Boolean checkRequestedSeatNosAvailability(BookingRequestDTO bookingReqDTO) {

		logger.info("WorkspaceBookingServiceImpl :: assignWorkspace :: checkRequestedSeatNosAvailability :: STARTED");

		Boolean isSeatAvailable = false;

		if (BookingType.MARTIAN.toString().equals(bookingReqDTO.getBookingType().toString())
				|| BookingType.AVP_SELF.toString().equals(bookingReqDTO.getBookingType().toString())
				|| BookingType.ADMIN_SELF.toString().equals(bookingReqDTO.getBookingType().toString())) {

			SelectedDeatilRequestDTO selectedDeatilRequestDTO = new SelectedDeatilRequestDTO();
			selectedDeatilRequestDTO = bookingReqDTO.getSelectedDeatils().entrySet().stream().findFirst().get()
					.getValue();

			// checking whether user is already have a booking for the day
			WorkspaceItemsEntity workspaceItemsEntityToCheckAlreadyHaveBooking = workspaceItemsRepository
					.findBookingAvailablityByEmployeeAndDate(
							CommonUtility.stringToSQLDate(bookingReqDTO.getBookingStartDate()),
							bookingReqDTO.getLocationCode(), selectedDeatilRequestDTO.getEmployeeId());

			if (workspaceItemsEntityToCheckAlreadyHaveBooking == null) {

				// checking whether the requested seat no is available for the day
				isSeatAvailable = checkTheRequestedSeatNoIsAvailable(bookingReqDTO);

			} else if (!(workspaceItemsEntityToCheckAlreadyHaveBooking.getStatus()
					.equals(BookingStatusType.CANCELLED))) {

				Map<String, String> businessErrorList = new TreeMap<>();
				businessErrorList.put("B-S-101-1",
						"Already You have Booked a Seat No ( "
								+ String.join(",", workspaceItemsEntityToCheckAlreadyHaveBooking.getWorkspaceCode())
								+ " ) for " + CommonUtility.sQLDateToString(
										workspaceItemsEntityToCheckAlreadyHaveBooking.getBookingDate()));
				throw new SeatAlreadyBookedException("B-S-101",
						bookingReqDTO.getBookingType() + " cannot book multiple seats for the day", businessErrorList);
			}

		} else if (BookingType.AVP_BULK.toString().equals(bookingReqDTO.getBookingType().toString())
				|| BookingType.ADMIN_OTHERS.toString().equals(bookingReqDTO.getBookingType().toString())
				|| BookingType.ADMIN_BLOCK_RELEASE.toString().equals(bookingReqDTO.getBookingType().toString())) {

			// checking whether the requested seat no is available for the day
			isSeatAvailable = checkTheRequestedSeatNoIsAvailable(bookingReqDTO);

		}

		logger.info("WorkspaceBookingServiceImpl :: assignWorkspace :: checkRequestedSeatNosAvailability :: ENDED");

		return isSeatAvailable;
	}

	private boolean checkTheRequestedSeatNoIsAvailable(BookingRequestDTO bookingReqDTO) {

		bookingReqDTO.getSelectedDeatils().entrySet().stream().forEach(selectedDeatil -> {

			SelectedDeatilRequestDTO selectedDeatilRequestDTO = new SelectedDeatilRequestDTO();
			selectedDeatilRequestDTO = (SelectedDeatilRequestDTO) selectedDeatil.getValue();

			List<WorkspaceItemsEntity> seatAvailableList = workspaceItemsRepository.checkSeatAvailablity(
					CommonUtility.stringToSQLDate(selectedDeatil.getKey()).toString(), bookingReqDTO.getLocationCode(),
					bookingReqDTO.getFloorNo(), selectedDeatilRequestDTO.getWorkspaceCodes());

			if (seatAvailableList.size() > 0) {
				Map<String, String> businessErrorList = new TreeMap<>();
				businessErrorList.put("B-S-102-1", "Seat No(s) ( "
						+ String.join(",", selectedDeatilRequestDTO.getWorkspaceCodes()) + " ) already booked ");
				throw new SeatAlreadyBookedException("B-S-102", "Not able to Book a seat", businessErrorList);
			}
		});
		return true;

	}

	private Map<String, List<String>> fetchRequiredParkingLotNos(BookingRequestDTO bookingReqDTO, BigInteger bookingId,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, String action) {

		Map<String, List<String>> allocatedNoMap = new TreeMap<>();

		allocatedNoMap = fetchParkingLotNos(bookingReqDTO, bookingId, selectedDeatil, action);

		if ((selectedDeatil.getValue().getNoOfTwoWheeler() > allocatedNoMap.get(ParkingLotType.TWO_WHEELER.toString())
				.size())
				|| (selectedDeatil.getValue().getNoOfFourWheeler() > allocatedNoMap
						.get(ParkingLotType.FOUR_WHEELER.toString()).size())) {

			Map<String, String> businessErrorList = new TreeMap<>();
			businessErrorList.put("B-P-201",
					"Expected Two Wheeler Parking is " + selectedDeatil.getValue().getNoOfTwoWheeler()
							+ " but available is : "
							+ allocatedNoMap.get(ParkingLotType.TWO_WHEELER.toString()).size());
			businessErrorList.put("B-P-202",
					"Expected Four Wheeler Parking is " + selectedDeatil.getValue().getNoOfFourWheeler()
							+ " but available is : "
							+ allocatedNoMap.get(ParkingLotType.FOUR_WHEELER.toString()).size());

			throw new NoSufficientParkingLotsException("B-S-201", "Not able to allocate parking lot",
					businessErrorList);
		}

		return allocatedNoMap;
	}

	private Map<String, List<String>> fetchParkingLotNos(BookingRequestDTO bookingReqDTO, BigInteger bookingId,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, String action) {

		Map<String, List<String>> allocatedNoMap = new TreeMap<>();
		List<String> twoWheelerParkingList = new ArrayList<>();
		List<String> fourWheelerParkingList = new ArrayList<>();

		if (selectedDeatil.getValue().getNoOfTwoWheeler() > 0) {
			String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated2WParkingLotNo(
					CommonUtility.stringToSQLDate(selectedDeatil.getKey()).toString(), bookingReqDTO.getLocationCode());
			allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), allocateParkingNo(bookingReqDTO, bookingId,
					selectedDeatil, lastAllocatedLotNo, ParkingLotType.TWO_WHEELER.toString()));
		} else {
			allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), twoWheelerParkingList);
		}

		if (selectedDeatil.getValue().getNoOfFourWheeler() > 0) {
			String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated4WParkingLotNo(
					CommonUtility.stringToSQLDate(selectedDeatil.getKey()).toString(), bookingReqDTO.getLocationCode());
			allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), allocateParkingNo(bookingReqDTO, bookingId,
					selectedDeatil, lastAllocatedLotNo, ParkingLotType.FOUR_WHEELER.toString()));
		} else {
			allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), fourWheelerParkingList);
		}

		return allocatedNoMap;
	}

	private List<String> allocateParkingNo(BookingRequestDTO bookingReqDTO, BigInteger bookingId,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, String lastAllocatedLotNo, String parkingType) {

		List<String> parkingLotsList = new ArrayList<>();

		if (null == lastAllocatedLotNo) {
			parkingLotsList = fetchNextParkingLotNo(bookingReqDTO, "0", parkingType,
					parkingType.equalsIgnoreCase("2W") ? selectedDeatil.getValue().getNoOfTwoWheeler()
							: selectedDeatil.getValue().getNoOfFourWheeler());
		} else {
			parkingLotsList = fetchNextParkingLotNo(bookingReqDTO, lastAllocatedLotNo, parkingType,
					parkingType.equalsIgnoreCase("2W") ? selectedDeatil.getValue().getNoOfTwoWheeler()
							: selectedDeatil.getValue().getNoOfFourWheeler());
		}

		return parkingLotsList;

	}

	private List<String> fetchNextParkingLotNo(BookingRequestDTO bookingReqDTO, String lastAllocatedLotNo,
			String parkingType, int twoWheelersExpectedCount) {
		List<String> parkingLotMasterEntityList = parkingLotMasterRepository.findNextParkingLotNo(
				bookingReqDTO.getLocationCode(), parkingType, lastAllocatedLotNo, twoWheelersExpectedCount);
		return parkingLotMasterEntityList;
	}

	private void allocateParkingLotNoToCurrentBooking(BookingRequestDTO bookingReqDTO,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, BigInteger bookingId,
			List<String> twoWheelerLotList, List<String> fourWheelerLotList, String action) {

		if (twoWheelerLotList.size() > 0) {
			for (String twoWheelerNo : twoWheelerLotList) {
				ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
				parkingItemsEntity.setBookingId(bookingId);
				parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
				parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
				parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
				parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
				parkingItemsEntity.setUserType(bookingReqDTO.getRequestedFor());
				parkingItemsEntity.setTwoWheelerNo(twoWheelerNo);
				parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
				parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
				parkingItemsEntity.setCity(bookingReqDTO.getCity());
				parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
				parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
				parkingItemsRepository.save(parkingItemsEntity);
			}
		}

		if (fourWheelerLotList.size() > 0) {
			for (String fourWheelerNo : fourWheelerLotList) {
				ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
				parkingItemsEntity.setBookingId(bookingId);
				parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
				parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
				parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
				parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
				parkingItemsEntity.setUserType(bookingReqDTO.getRequestedFor());
				parkingItemsEntity.setFourWheelerNo(fourWheelerNo);
				parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
				parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
				parkingItemsEntity.setCity(bookingReqDTO.getCity());
				parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
				parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
				parkingItemsRepository.save(parkingItemsEntity);
			}
		}

		if (twoWheelerLotList.size() == 0 && fourWheelerLotList.size() == 0) {
			ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
			parkingItemsEntity.setBookingId(bookingId);
			parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
			parkingItemsEntity.setBookingType(bookingReqDTO.getBookingType());
			parkingItemsEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
			parkingItemsEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
			parkingItemsEntity.setUserType(bookingReqDTO.getRequestedFor());
			parkingItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
			parkingItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
			parkingItemsEntity.setCity(bookingReqDTO.getCity());
			parkingItemsEntity.setCountry(bookingReqDTO.getCountry());
			parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
			parkingItemsRepository.save(parkingItemsEntity);
		}

	}

	private void addMealNoToCurrentBooking(BookingRequestDTO bookingReqDTO,
			Entry<String, SelectedDeatilRequestDTO> selectedDeatil, BigInteger bookingId) {
		MealItemsEntity mealItemEntity = new MealItemsEntity();
		mealItemEntity.setBookingId(bookingId);
		mealItemEntity.setEmployeeId(selectedDeatil.getValue().getEmployeeId());
		mealItemEntity.setEmployeeName(selectedDeatil.getValue().getEmployeeName());
		mealItemEntity.setUserType(bookingReqDTO.getRequestedFor());
		mealItemEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getKey()));
		mealItemEntity.setBookingType(bookingReqDTO.getBookingType());
		mealItemEntity.setLocationCode(bookingReqDTO.getLocationCode());
		mealItemEntity.setCity(bookingReqDTO.getCity());
		mealItemEntity.setCountry(bookingReqDTO.getCountry());
		mealItemEntity.setNoOfLunch(selectedDeatil.getValue().getNoOfLunch());
		mealItemEntity.setNoOfDinner(selectedDeatil.getValue().getNoOfDinner());
		mealItemEntity.setStatus(BookingStatusType.BOOKED);
		mealItemsRepository.save(mealItemEntity);
	}

	@Override
	public CommonResponseDTO<List<OfficeWorkspaceResponseDTO>> fetchWorkspaceDetailsByLocation(String locationCode) {

		return null;
	}

	@Override
	public CommonResponseDTO<WorkspaceLayoutResponseDTO> fetchWorkspaceLayoutDetails(
			WorkspaceLayoutRequestDTO workspaceLayoutRequest) {
		logger.info("WorkspaceBookingServiceImpl :: fetchLayout :: STARTED");
		CommonResponseDTO<WorkspaceLayoutResponseDTO> wsBookingResponse = new CommonResponseDTO<>();
		LocationMasterEntity locationMasterEntity = locationMasterRepository.findByLocationCodeAndFloorNo(
				workspaceLayoutRequest.getLocationCode(), workspaceLayoutRequest.getFloorNo());
		WorkspaceLayoutResponseDTO workspaceLayoutResponse = bookingServiceHelper
				.getWorkspaceLayoutDetails(workspaceLayoutRequest, locationMasterEntity.getLocationMasterId());
		wsBookingResponse.setData(workspaceLayoutResponse);
		logger.info("WorkspaceBookingServiceImpl :: fetchLayout :: ENDED");
		return wsBookingResponse;
	}

	@Override
	public CommonResponseDTO<List<WorkspaceLocationResponseDTO>> fetchWorkspaceLocationsDetails() {
		logger.info("WorkspaceBookingServiceImpl :: fetchLayout :: STARTED");
		CommonResponseDTO<List<WorkspaceLocationResponseDTO>> wslocationResponse = new CommonResponseDTO<>();
		try {
			wslocationResponse = bookingServiceHelper.getBookingLocationDetails();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.info("WorkspaceBookingServiceImpl :: fetchLayout :: ENDED");
		return wslocationResponse;
	}

	private BookingResponseDTO fetchBookingDetailsByBookingId(BigInteger bookingId) {
		BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
		Map<String, SelectedDeatilResponseDTO> allocatedDeatils = new TreeMap<>();
		try {
			List<BookingDetailsResponseDTO> bookingDetails = bookingServiceHelper.getBookingDetails(bookingId);
			bookingResponseDTO.setBookingId(bookingId);
			bookingDetails.stream().forEach(bookingDetail -> {
				bookingResponseDTO.setBookingType(bookingDetail.getBookingType());
				bookingResponseDTO.setRequesterId(bookingDetail.getRequesterId());
				bookingResponseDTO.setRequesterName(bookingDetail.getRequesterName());
				bookingResponseDTO.setRequestedDate(bookingDetail.getRequestedDate());
				bookingResponseDTO.setWorkspaceType(bookingDetail.getWorkspaceType());
				bookingResponseDTO.setLocationCode(bookingDetail.getLocationCode());
				bookingResponseDTO.setFloorNo(bookingDetail.getFloorNo());
				bookingResponseDTO.setCity(bookingDetail.getCity());
				bookingResponseDTO.setCountry(bookingDetail.getCountry());
				List<String> workspaceCodes = (null != bookingDetail.getWorkspaceCodes())
						? Stream.of(bookingDetail.getWorkspaceCodes().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				List<String> twoWheelerSlots = (null != bookingDetail.getTwoWheelerSlots())
						? Stream.of(bookingDetail.getTwoWheelerSlots().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				List<String> fourWheelerSlots = (null != bookingDetail.getFourWheelerSlots())
						? Stream.of(bookingDetail.getFourWheelerSlots().split(",")).collect(Collectors.toList())
						: new ArrayList<>();
				String parkingType = ((twoWheelerSlots.size() == 0 && fourWheelerSlots.size() == 0)
						? ParkingLotType.NONE.toString()
						: twoWheelerSlots.size() > 0 && !(fourWheelerSlots.size() > 0)
								? ParkingLotType.TWO_WHEELER.toString()
								: (!(twoWheelerSlots.size() > 0) && fourWheelerSlots.size() > 0
										? ParkingLotType.FOUR_WHEELER.toString()
										: ParkingLotType.BOTH.toString()));
				allocatedDeatils.put(bookingDetail.getBookingDate(),
						new SelectedDeatilResponseDTO(bookingDetail.getEmployeeId(), bookingDetail.getEmployeeName(),
								workspaceCodes, bookingDetail.getNoOfLunch(), bookingDetail.getNoOfDinner(),
								parkingType, twoWheelerSlots, fourWheelerSlots, bookingDetail.getStatus(),
								bookingDetail.getNoOfSeats(), 0, 0));
			});
			bookingResponseDTO.setAllocatedDeatils(allocatedDeatils);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bookingResponseDTO;
	}

	@Override
	@Transactional
	public CommonResponseDTO<List<BookingCancellationResponseDTO>> cancelBooking(
			List<BookingCancellationRequestDTO> bookingCancellationRequest) {
		CommonResponseDTO<List<BookingCancellationResponseDTO>> commonResponse = new CommonResponseDTO<>();
		List<BookingCancellationResponseDTO> bookingCancellationDetails = new ArrayList<>();
		for (BookingCancellationRequestDTO bookingCancellationReq : bookingCancellationRequest) {
			List<BookingCancellationRespDetails> cancellationDetails = new ArrayList<>();
			BookingCancellationResponseDTO bookingCancellationResponse = new BookingCancellationResponseDTO();
			bookingCancellationResponse.setBookingId(bookingCancellationReq.getBookingId());
			for (String bookingCancelDate : bookingCancellationReq.getBookingDate()) {
				BookingCancellationRespDetails bookingCancellationRespDetails = new BookingCancellationRespDetails();
				bookingCancellationRespDetails.setBookingDate(bookingCancelDate);
				int workspaceCancelCount = workspaceItemsRepository.cancelWorkspace(
						bookingCancellationReq.getBookingId(), CommonUtility.stringToSQLDate(bookingCancelDate));
				int parkingCancelCount = parkingItemsRepository.cancelParking(bookingCancellationReq.getBookingId(),
						CommonUtility.stringToSQLDate(bookingCancelDate));
				int mealCancelCount = mealItemsRepository.cancelMeal(bookingCancellationReq.getBookingId(),
						CommonUtility.stringToSQLDate(bookingCancelDate));
				if (workspaceCancelCount > 0 && parkingCancelCount > 0 && mealCancelCount > 0) {
					bookingCancellationRespDetails.setMessage("Booking Cancelled Successfully.");
				} else {
					bookingCancellationRespDetails
							.setMessage("There are no bookings / Booking Already Cancelled for this bookingId");
				}
				cancellationDetails.add(bookingCancellationRespDetails);
				bookingCancellationResponse.setCancellationDetails(cancellationDetails);
			}
			bookingCancellationDetails.add(bookingCancellationResponse);
		}
		commonResponse.setData(bookingCancellationDetails);
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

	@Override
	public CommonResponseDTO<List<BookingDetailResponseDTO>> fetchBookingDetails(
			BookingDetailsRequestDTO bookingDetailsReq) {
		CommonResponseDTO<List<BookingDetailResponseDTO>> commonResponse = new CommonResponseDTO<>();
		List<BookingDetailResponseDTO> bookingDetails = new ArrayList<>();
		bookingDetails = bookingServiceHelper.getEmployeePreviousAndFutureBookingDetails(bookingDetailsReq);
		if (bookingDetails.size() > 0) {
			commonResponse.setData(bookingDetails);
		} else {
			commonResponse.setMessage("There are no " + bookingDetailsReq.getRequestType() + " Bookings at this time.");
		}
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

	@Override
	public CommonResponseDTO<AdminDashboardDetailsResponseDTO> fetchAdminDashboardDetails(
			AdminDashboardDetailsRequestDTO adminDashboardDetailsReq) {
		CommonResponseDTO<AdminDashboardDetailsResponseDTO> commonResponse = new CommonResponseDTO<AdminDashboardDetailsResponseDTO>();
		AdminDashboardDetailsResponseDTO adminDashboardDetailsResponse = new AdminDashboardDetailsResponseDTO();
		adminDashboardDetailsResponse = bookingServiceHelper.getAdminDashboardDetails(adminDashboardDetailsReq);
		commonResponse.setData(adminDashboardDetailsResponse);
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

	@Override
	public CommonResponseDTO<AdminDashboardStatsResponseDTO> fetchAdminDashboardStats(
			AdminDashboardStatsRequestDTO adminDashboardDetailsReq) {
		CommonResponseDTO<AdminDashboardStatsResponseDTO> commonResponse = new CommonResponseDTO<AdminDashboardStatsResponseDTO>();
		AdminDashboardStatsResponseDTO adminDashboardDetailsResponse = new AdminDashboardStatsResponseDTO();
		adminDashboardDetailsResponse = bookingServiceHelper.getAdminDashboardStats(adminDashboardDetailsReq);
		commonResponse.setData(adminDashboardDetailsResponse);
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

	@Override
	public byte[] downloadAdminDashboardDetails(AdminDashboardDetailsRequestDTO adminDashboardDetailsReq,
			HttpServletResponse response) throws IOException {
		return bookingServiceHelper.downloadAdminDashboardDetails(adminDashboardDetailsReq, response);

	}

	@Override
	public CommonResponseDTO<List<Object>> fetchBookingSearchDetails(
			BookingSearchDetailsRequestDTO bookingSearchDetailsRequestDTO) {
		CommonResponseDTO<List<Object>> commonResponse = new CommonResponseDTO<>();
		commonResponse.setData(bookingServiceHelper.getBookingSearchDetails(bookingSearchDetailsRequestDTO));
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

	@Override
	public CommonResponseDTO<String> makeAsDefaultPrefrence(DefaultPreferencesRequestDTO defaultPreferencesReq) {
		CommonResponseDTO<String> commonResponse = new CommonResponseDTO<>();
		commonResponse.setData(bookingServiceHelper.makeAsDefaultPrefrence(defaultPreferencesReq));
		commonResponse.setStatus(HttpStatus.OK.value());
		return commonResponse;
	}

}