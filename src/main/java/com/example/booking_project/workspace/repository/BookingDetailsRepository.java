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

import com.example.booking_project.workspace.entity.BookingDetailsEntity;
import com.example.booking_project.workspace.enums.BookingType;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */
@Repository("bookingDetailsRepository")
@Transactional(rollbackOn = { Exception.class })
public interface BookingDetailsRepository extends JpaRepository<BookingDetailsEntity, BigInteger> {

	@Query(nativeQuery = true, value = "select T1.booking_id AS bookingId, T1.booking_date as bookingDate, T1.workspace_code as workspaceNo, T1.workspace_type as workspaceType, "
			+ "T2.no_of_lunch as  noOfLunch, T2.no_of_dinner as noOfDinner, T3.two_wheeler_parking_lot as twoWheelerSlotNo, "
			+ "T3.four_wheeler_parking_lot as fourWheelerSlotNo from "
			+ "(select * from [workspace].[workspace_items] where booking_id ='1') T1 "
			+ "JOIN (select * from [workspace].[meal_items] where booking_id ='1')  T2 "
			+ "ON (T1.booking_date = T2.booking_date) "
			+ "JOIN (select * from [workspace].[parking_items] where booking_id ='1')  T3 "
			+ "ON (T1.booking_date = T3.booking_date) ORDER BY bookingDate, workspaceNo")
	List<BookingDetailsEntity> fetchBookingDetails(BigInteger booingId);

	@Modifying
	@Query("UPDATE BookingDetailsEntity bd SET bd.status='Cancelled' WHERE bd.bookingId =:bookingId AND bd.requestedDate = :requestedDate")
	int cancelBookingStatus(@Param("bookingId") BigInteger bookingId, @Param("requestedDate") Date requestedDate);

	BookingDetailsEntity findByBookingId(BigInteger bookingId);

	@Modifying
	@Query("UPDATE BookingDetailsEntity bd SET bd.isDefaultBookingPreference=:status WHERE bd.bookingId =:bookingId AND bd.requestedDate = :requestedDate AND bd.bookingType=:bookingType")
	int updateDefaultBookingStatus(@Param("bookingId") BigInteger bookingId, @Param("requestedDate") Date requestedDate,
			@Param("status") boolean status, @Param("bookingType") BookingType bookingType);

}
