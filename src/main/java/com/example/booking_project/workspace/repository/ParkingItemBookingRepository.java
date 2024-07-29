package com.example.booking_project.workspace.repository;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.booking_project.workspace.entity.ParkingItemsEntity;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 24, 2023
 */
@Repository("parkingItemBookingRepository")
@Transactional(rollbackOn = { Exception.class })
public interface ParkingItemBookingRepository extends JpaRepository<ParkingItemsEntity, BigInteger> {

	@Query(value = "select Top 1 two_wheeler_parking_lot from [booking].[workspace].[parking_items] where booking_date = :bookingDate"
			+ " and location_code = :locationCode ORDER BY two_wheeler_parking_lot DESC", nativeQuery = true)
	String getLastAllocated2WParkingLotNo(String bookingDate, String locationCode);

	@Query(value = "select Top 1 four_wheeler_parking_lot from [booking].[workspace].[parking_items] where booking_date = :bookingDate"
			+ " and location_code = :locationCode ORDER BY four_wheeler_parking_lot DESC", nativeQuery = true)
	String getLastAllocated4WParkingLotNo(String bookingDate, String locationCode);

	List<ParkingItemsEntity> findByBookingId(BigInteger bookingId);

	@Query(value = "select two_wheeler_parking_lot from [booking].[workspace].[parking_items] where booking_id=:bookingId AND booking_date = :bookingDate AND location_code = :locationCode AND employee_id=:employeeId AND two_wheeler_parking_lot IS NOT NULL AND status='BOOKED' ORDER BY two_wheeler_parking_lot DESC", nativeQuery = true)
	List<String> findAllocatedTwoWheelerLots(@Param("bookingId") BigInteger bookingId,
			@Param("bookingDate") Date bookingDate, @Param("locationCode") String locationCode,
			@Param("employeeId") String employeeId);

	@Query(value = "select four_wheeler_parking_lot from [booking].[workspace].[parking_items] where booking_id=:bookingId AND booking_date = :bookingDate"
			+ " AND location_code = :locationCode AND employee_id=:employeeId AND four_wheeler_parking_lot IS NOT NULL AND status='BOOKED' ORDER BY four_wheeler_parking_lot DESC", nativeQuery = true)
	List<String> findAllocatedFourWheelerLots(@Param("bookingId") BigInteger bookingId,
			@Param("bookingDate") Date bookingDate, @Param("locationCode") String locationCode,
			@Param("employeeId") String employeeId);

	@Modifying
	@Query("UPDATE ParkingItemsEntity pi SET pi.status='CANCELLED' WHERE pi.bookingId =:bookingId AND pi.bookingDate = :bookingDate AND pi.employeeId = :employeeId AND pi.twoWheelerNo IS NOT NULL AND pi.status='BOOKED'")
	int cancel2WParking(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate,
			@Param("employeeId") String employeeId);

	@Modifying
	@Query("UPDATE ParkingItemsEntity pi SET pi.status='CANCELLED' WHERE pi.bookingId =:bookingId AND pi.bookingDate = :bookingDate AND pi.employeeId = :employeeId AND pi.fourWheelerNo IS NOT NULL AND pi.status='BOOKED'")
	int cancel4WParking(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate,
			@Param("employeeId") String employeeId);

	@Modifying
	@Query("UPDATE ParkingItemsEntity pi SET pi.status='CANCELLED' WHERE pi.bookingId =:bookingId AND pi.bookingDate = :bookingDate AND pi.status='BOOKED'")
	int cancelParking(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate);

	@Modifying
	@Query("UPDATE ParkingItemsEntity pi SET pi.twoWheelerNo=:twoWheelerNo, pi.fourWheelerNo=:fourWheelerNo WHERE pi.bookingId =:bookingId AND pi.bookingDate = :bookingDate")
	int updateParkingDetails(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate,
			@Param("twoWheelerNo") String twoWheelerNo, @Param("fourWheelerNo") String fourWheelerNo);

}