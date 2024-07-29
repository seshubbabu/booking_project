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

import com.example.booking_project.workspace.entity.MealItemsEntity;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 24, 2023
 */
@Repository("mealItemBookingRepository")
@Transactional(rollbackOn = { Exception.class })
public interface MealItemBookingRepository extends JpaRepository<MealItemsEntity, BigInteger> {

	List<MealItemsEntity> findByBookingId(BigInteger bookingId);

	@Modifying
	@Query("UPDATE MealItemsEntity mi SET mi.status='CANCELLED' WHERE mi.bookingId =:bookingId AND mi.bookingDate = :bookingDate")
	int cancelMeal(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate);

	@Modifying
	@Query("UPDATE MealItemsEntity mi SET mi.noOfLunch=:noOfLunch, mi.noOfDinner=:noOfDinner WHERE mi.bookingId =:bookingId AND mi.bookingDate = :bookingDate")
	int updateMealItemsDetails(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate,
			@Param("noOfLunch") int noOfLunch, @Param("noOfDinner") int noOfDinner);

}