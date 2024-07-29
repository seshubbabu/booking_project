package com.example.booking_project.workspace.service.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.booking_project.common.exception.SeatAlreadyBookedException;
import com.example.booking_project.workspace.common.dto.BlockRequestDTO;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;
import com.example.booking_project.workspace.common.dto.SelectedDeatilsRequest;
import com.example.booking_project.workspace.dto.BookingDetailsResponseDTO;
import com.example.booking_project.workspace.entity.BookingDetailsEntity;
import com.example.booking_project.workspace.entity.MealItemsEntity;
import com.example.booking_project.workspace.entity.ParkingItemsEntity;
import com.example.booking_project.workspace.entity.WorkspaceItemsEntity;
import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.ParkingLotType;
import com.example.booking_project.workspace.repository.BookingDetailsRepository;
import com.example.booking_project.workspace.repository.MealItemBookingRepository;
import com.example.booking_project.workspace.repository.ParkingItemBookingRepository;
import com.example.booking_project.workspace.repository.ParkingLotMasterRepository;
import com.example.booking_project.workspace.repository.WorkspaceItemsBookingRepository;
import com.example.booking_project.workspace.service.BookingService;
import com.example.booking_project.workspace.util.BookingServiceHelper;
import com.example.booking_project.workspace.util.CommonUtility;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 11, 2023
 */
@Service
public class BookingServiceImpl implements BookingService {

	private static Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

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

	@Override
	public CommonResponseDTO<?> blockSeat(BlockRequestDTO blockRequestDTO) {

		CommonResponseDTO<?> commonReponse = new CommonResponseDTO<>();

		if (null == blockRequestDTO.getBookingId()) {

			// Create Booking Details
			logger.info("BookingServiceImpl :: blockSeat :: assignWorkspace :: STARTED");

			// 1.) Seat No. Availability Check
			checkRequestedSeatNoIsAvailable(blockRequestDTO);

			// 2.) Save Booking Details
			BookingDetailsEntity bookingDetailsEntity = BookingServiceHelper
					.convertBookingReqDTOToBookingDetailsEntity(blockRequestDTO);
			/*
			BookingDetailsEntity bookingDetailsEntity = BookingServiceHelper
					.convertBookingReqDTOToBookingDetailsEntity(blockRequestDTO);

			 */
			bookingDetailsEntity = bookingDetailsRepository.save(bookingDetailsEntity);

			BigInteger bookingId = null != bookingDetailsEntity.getBookingId()
					? bookingDetailsEntity.getBookingId().abs()
					: new BigInteger("0");

			// 3.) Book Seat
			blockRequestDTO.getSelectedDeatils().stream().forEach(selectedDeatil -> {

				allocateSeatNosToCurrentBooking(blockRequestDTO, selectedDeatil, bookingId);

				// 5.) Allocate Parking Lot No
				allocateDefaultParkingLotNoToCurrentBooking(blockRequestDTO, selectedDeatil, bookingId);

				// 6.) Book meal
				addMealNoToCurrentBooking(blockRequestDTO, selectedDeatil, bookingId);

			});
			Map<String, List<BookingDetailsResponseDTO>> bookingResponseDTO = fetchBookingDetailsByBookingId(bookingId);
			commonReponse.setStatus(HttpStatus.CREATED.value());
			commonReponse.setData(bookingResponseDTO);

			logger.info("WorkspaceBookingServiceImpl :: blockSeat :: assignWorkspace :: ENDED");

		} else {

			// update Booking Details
			logger.info("WorkspaceBookingServiceImpl :: blockSeat ::  updateWorkspace :: STARTED");

			// Update workspace Details
			blockRequestDTO.getSelectedDeatils().stream().forEach(selectedDetail -> {

				workspaceItemsRepository.updateWorkspaceDetails(blockRequestDTO.getBookingId(),
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()),
						selectedDetail.getWorkspaceCode());

				// update meal details
				mealItemsRepository.updateMealItemsDetails(blockRequestDTO.getBookingId(),
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()), selectedDetail.getNoOfLunch(),
						selectedDetail.getNoOfDinner());

				// update parking lot details
				updateOrAllocateParkingLots(blockRequestDTO, blockRequestDTO.getBookingId(), selectedDetail);

			});

			Map<String, List<BookingDetailsResponseDTO>> bookingResponseDTO = fetchBookingDetailsByBookingId(
					blockRequestDTO.getBookingId());
			commonReponse.setStatus(HttpStatus.OK.value());
			commonReponse.setData(bookingResponseDTO);

			logger.info("WorkspaceBookingServiceImpl :: blockSeat :: updateWorkspace :: ENDED");
		}

		return commonReponse;
	}

	private void allocateDefaultParkingLotNoToCurrentBooking(BlockRequestDTO blockRequestDTO,
			SelectedDeatilsRequest selectedDeatil, BigInteger bookingId) {

		if (selectedDeatil.getNoOfTwoWheeler() == 0 && selectedDeatil.getNoOfFourWheeler() == 0) {

			ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
			parkingItemsEntity.setBookingId(bookingId);
			parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getBookingDate()));
			parkingItemsEntity.setBookingType(blockRequestDTO.getBookingType());
			parkingItemsEntity.setEmployeeId(selectedDeatil.getEmployeeId());
			parkingItemsEntity.setEmployeeName(selectedDeatil.getEmployeeName());
			parkingItemsEntity.setUserType(blockRequestDTO.getRequestedFor());
			parkingItemsEntity.setLocationCode(blockRequestDTO.getLocationCode());
			parkingItemsEntity.setFloorNo(blockRequestDTO.getFloorNo());
			parkingItemsEntity.setCity(blockRequestDTO.getCity());
			parkingItemsEntity.setCountry(blockRequestDTO.getCountry());
			parkingItemsEntity.setStatus(BookingStatusType.BOOKED);

			parkingItemsRepository.save(parkingItemsEntity);
		}
	}

	private void checkRequestedSeatNoIsAvailable(BlockRequestDTO blockRequestDTO) {

		blockRequestDTO.getSelectedDeatils().stream().forEach(selectedDeatil -> {

			List<WorkspaceItemsEntity> seatAvailableList = workspaceItemsRepository.checkSeatAvailablity(
					CommonUtility.stringToSQLDate(selectedDeatil.getBookingDate()).toString(),
					blockRequestDTO.getLocationCode(), blockRequestDTO.getFloorNo(),
					Arrays.asList(selectedDeatil.getWorkspaceCode()));

			if (seatAvailableList.size() > 0) {
				Map<String, String> businessErrorList = new TreeMap<>();
				businessErrorList.put("B-S-102-1",
						"Seat No(s) ( " + String.join(",", selectedDeatil.getWorkspaceCode() + " ) already booked "));
				throw new SeatAlreadyBookedException("B-S-102", "Not able to Book a seat", businessErrorList);
			}
		});

	}

	private void allocateSeatNosToCurrentBooking(BlockRequestDTO bookingReqDTO, SelectedDeatilsRequest selectedDeatil,
			BigInteger bookingId) {

		WorkspaceItemsEntity workspaceItemsEntity = new WorkspaceItemsEntity();
		workspaceItemsEntity.setBookingId(bookingId);
		workspaceItemsEntity.setBookingType(bookingReqDTO.getBookingType());
		workspaceItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getBookingDate()));
		workspaceItemsEntity.setEmployeeId(selectedDeatil.getEmployeeId());
		workspaceItemsEntity.setDepartment(bookingReqDTO.getDepartmentName());
		workspaceItemsEntity.setDivision(bookingReqDTO.getDivisionName());
		workspaceItemsEntity.setEmployeeName(selectedDeatil.getEmployeeName());
		workspaceItemsEntity.setUserType(bookingReqDTO.getRequestedFor());
		workspaceItemsEntity.setWorkspaceCode(selectedDeatil.getWorkspaceCode());
		workspaceItemsEntity.setWorkspaceType(bookingReqDTO.getWorkspaceType());
		workspaceItemsEntity.setCity(bookingReqDTO.getCity());
		workspaceItemsEntity.setFloorNo(bookingReqDTO.getFloorNo());
		workspaceItemsEntity.setLocationCode(bookingReqDTO.getLocationCode());
		workspaceItemsEntity.setCountry(bookingReqDTO.getCountry());
		workspaceItemsEntity.setStatus(BookingStatusType.BOOKED);
		workspaceItemsRepository.save(workspaceItemsEntity);

	}

	private Map<String, List<BookingDetailsResponseDTO>> fetchBookingDetailsByBookingId(BigInteger bookingId) {
		Map<String, List<BookingDetailsResponseDTO>> bookingDetailsResponse = new TreeMap<>();
		try {
		//	List<BookingDetailsResponseDTO> bookingDetails = bookingServiceHelper.getBookingDetails(bookingId);
			List<BookingDetailsResponseDTO> bookingDetails = bookingServiceHelper.getBookingDetails(bookingId);

			bookingDetailsResponse = bookingDetails.stream()
					.collect(Collectors.groupingBy(BookingDetailsResponseDTO::getBookingDate, Collectors.toList()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bookingDetailsResponse;
	}

	private void updateOrAllocateParkingLots(BlockRequestDTO blockRequestDTO, BigInteger bookingId,
			SelectedDeatilsRequest selectedDetail) {

		Map<String, List<String>> allocatedNoMap = new TreeMap<>();

		List<String> twparkingItemsList = parkingItemsRepository.findAllocatedTwoWheelerLots(bookingId,
				CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()), blockRequestDTO.getLocationCode(),
				selectedDetail.getEmployeeId());

		if (selectedDetail.getNoOfTwoWheeler() > 0) {

			int i = 0;
			while (i < selectedDetail.getNoOfTwoWheeler() - twparkingItemsList.size()) {
				String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated2WParkingLotNo(
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()).toString(),
						blockRequestDTO.getLocationCode());
				allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), allocateParkingNo(blockRequestDTO, bookingId,
						selectedDetail, lastAllocatedLotNo, ParkingLotType.TWO_WHEELER.toString()));
				List<String> twoWheelerLotList = allocatedNoMap.get(ParkingLotType.TWO_WHEELER.toString());
				if (twoWheelerLotList.size() > 0) {
					for (String twoWheelerNo : twoWheelerLotList) {
						ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
						parkingItemsEntity.setBookingId(bookingId);
						parkingItemsEntity
								.setBookingDate(CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()));
						parkingItemsEntity.setBookingType(blockRequestDTO.getBookingType());
						parkingItemsEntity.setEmployeeId(selectedDetail.getEmployeeId());
						parkingItemsEntity.setEmployeeName(selectedDetail.getEmployeeName());
						parkingItemsEntity.setTwoWheelerNo(twoWheelerNo);
						parkingItemsEntity.setLocationCode(blockRequestDTO.getLocationCode());
						parkingItemsEntity.setFloorNo(blockRequestDTO.getFloorNo());
						parkingItemsEntity.setCity(blockRequestDTO.getCity());
						parkingItemsEntity.setCountry(blockRequestDTO.getCountry());
						parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
						parkingItemsRepository.save(parkingItemsEntity);
					}
				}
				i++;
			}

		} else {
			if (!twparkingItemsList.isEmpty()) {
				parkingItemsRepository.cancel2WParking(bookingId,
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()), selectedDetail.getEmployeeId());
			}
			allocatedNoMap.put(ParkingLotType.TWO_WHEELER.toString(), twparkingItemsList);
		}
		List<String> fwparkingItemsList = parkingItemsRepository.findAllocatedFourWheelerLots(bookingId,
				CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()), blockRequestDTO.getLocationCode(),
				selectedDetail.getEmployeeId());
		if (selectedDetail.getNoOfFourWheeler() > 0) {

			int i = 0;
			while (i < selectedDetail.getNoOfFourWheeler() - fwparkingItemsList.size()) {
				String lastAllocatedLotNo = parkingItemsRepository.getLastAllocated4WParkingLotNo(
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()).toString(),
						blockRequestDTO.getLocationCode());
				allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), allocateParkingNo(blockRequestDTO, bookingId,
						selectedDetail, lastAllocatedLotNo, ParkingLotType.FOUR_WHEELER.toString()));

				List<String> fourWheelerLotList = allocatedNoMap.get(ParkingLotType.FOUR_WHEELER.toString());
				if (fourWheelerLotList.size() > 0) {
					for (String fourWheelerNo : fourWheelerLotList) {
						ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
						parkingItemsEntity.setBookingId(bookingId);
						parkingItemsEntity
								.setBookingDate(CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()));
						parkingItemsEntity.setBookingType(blockRequestDTO.getBookingType());
						parkingItemsEntity.setEmployeeId(selectedDetail.getEmployeeId());
						parkingItemsEntity.setEmployeeName(selectedDetail.getEmployeeName());
						parkingItemsEntity.setFourWheelerNo(fourWheelerNo);
						parkingItemsEntity.setLocationCode(blockRequestDTO.getLocationCode());
						parkingItemsEntity.setFloorNo(blockRequestDTO.getFloorNo());
						parkingItemsEntity.setCity(blockRequestDTO.getCity());
						parkingItemsEntity.setCountry(blockRequestDTO.getCountry());
						parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
						parkingItemsRepository.save(parkingItemsEntity);
					}

				}
				i++;
			}
		} else {
			if (!fwparkingItemsList.isEmpty()) {
				parkingItemsRepository.cancel4WParking(bookingId,
						CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()), selectedDetail.getEmployeeId());
			}
			allocatedNoMap.put(ParkingLotType.FOUR_WHEELER.toString(), fwparkingItemsList);
		}
		if (selectedDetail.getNoOfTwoWheeler() == 0 && selectedDetail.getNoOfFourWheeler() == 0) {
			addEmptyParkingLotNo(blockRequestDTO, selectedDetail);
		}

	}

	private void addEmptyParkingLotNo(BlockRequestDTO blockRequestDTO, SelectedDeatilsRequest selectedDetail) {
		ParkingItemsEntity parkingItemsEntity = new ParkingItemsEntity();
		parkingItemsEntity.setBookingId(blockRequestDTO.getBookingId());
		parkingItemsEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDetail.getBookingDate()));
		parkingItemsEntity.setBookingType(blockRequestDTO.getBookingType());
		parkingItemsEntity.setEmployeeId(selectedDetail.getEmployeeId());
		parkingItemsEntity.setEmployeeName(selectedDetail.getEmployeeName());
		parkingItemsEntity.setLocationCode(blockRequestDTO.getLocationCode());
		parkingItemsEntity.setFloorNo(blockRequestDTO.getFloorNo());
		parkingItemsEntity.setCity(blockRequestDTO.getCity());
		parkingItemsEntity.setCountry(blockRequestDTO.getCountry());
		parkingItemsEntity.setStatus(BookingStatusType.BOOKED);
		parkingItemsRepository.save(parkingItemsEntity);
	}

	private List<String> allocateParkingNo(BlockRequestDTO blockRequestDTO, BigInteger bookingId,
			SelectedDeatilsRequest selectedDetail, String lastAllocatedLotNo, String parkingType) {

		List<String> parkingLotsList = new ArrayList<>();

		if (null == lastAllocatedLotNo) {
			parkingLotsList = fetchNextParkingLotNo(blockRequestDTO, "0", parkingType,
					parkingType.equalsIgnoreCase("2W") ? selectedDetail.getNoOfTwoWheeler()
							: selectedDetail.getNoOfFourWheeler());
		} else {
			parkingLotsList = fetchNextParkingLotNo(blockRequestDTO, lastAllocatedLotNo, parkingType,
					parkingType.equalsIgnoreCase("2W") ? selectedDetail.getNoOfTwoWheeler()
							: selectedDetail.getNoOfFourWheeler());
		}

		return parkingLotsList;

	}

	private List<String> fetchNextParkingLotNo(BlockRequestDTO blockRequestDTO, String lastAllocatedLotNo,
			String parkingType, int twoWheelersExpectedCount) {
		List<String> parkingLotMasterEntityList = parkingLotMasterRepository.findNextParkingLotNo(
				blockRequestDTO.getLocationCode(), parkingType, lastAllocatedLotNo, twoWheelersExpectedCount);
		return parkingLotMasterEntityList;
	}

	private void addMealNoToCurrentBooking(BlockRequestDTO bookingReqDTO, SelectedDeatilsRequest selectedDeatil,
			BigInteger bookingId) {
		MealItemsEntity mealItemEntity = new MealItemsEntity();
		mealItemEntity.setBookingId(bookingId);
		mealItemEntity.setEmployeeId(selectedDeatil.getEmployeeId());
		mealItemEntity.setEmployeeName(selectedDeatil.getEmployeeName());
		mealItemEntity.setUserType(bookingReqDTO.getRequestedFor());
		mealItemEntity.setBookingDate(CommonUtility.stringToSQLDate(selectedDeatil.getBookingDate()));
		mealItemEntity.setBookingType(bookingReqDTO.getBookingType());
		mealItemEntity.setLocationCode(bookingReqDTO.getLocationCode());
		mealItemEntity.setCity(bookingReqDTO.getCity());
		mealItemEntity.setCountry(bookingReqDTO.getCountry());
		mealItemEntity.setNoOfLunch(selectedDeatil.getNoOfLunch());
		mealItemEntity.setNoOfDinner(selectedDeatil.getNoOfDinner());
		mealItemEntity.setStatus(BookingStatusType.BOOKED);
		mealItemsRepository.save(mealItemEntity);
	}

}
