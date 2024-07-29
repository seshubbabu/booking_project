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

import com.example.booking_project.workspace.entity.WorkspaceItemsEntity;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 10, 2023
 */
@Repository("WorkspaceItemsBookingRepository")
@Transactional(rollbackOn = { Exception.class })
public interface WorkspaceItemsBookingRepository extends JpaRepository<WorkspaceItemsEntity, BigInteger> {

	@Query(value = "SELECT * FROM [booking].[workspace].[workspace_items] where booking_date = :bookingDate AND location_code = :locationCode AND floor_no = :floorNo AND workspace_code IN (:workspaceCode) AND status = 'BOOKED'", nativeQuery = true)
	List<WorkspaceItemsEntity> checkSeatAvailablity(@Param("bookingDate") String bookingDate,
			@Param("locationCode") String locationCode, @Param("floorNo") String floorNo,
			@Param("workspaceCode") List<String> workspaceCodes);

	List<WorkspaceItemsEntity> findByBookingId(BigInteger bookingId);

	@Query(value = "SELECT * FROM [booking].[workspace].[workspace_items] where booking_date = :bookingDate AND location_code = :locationCode AND floor_no = :floorNo AND workspace_type (:workspaceType) AND status = 'BOOKED'", nativeQuery = true)
	List<WorkspaceItemsEntity> getByBookingDateAndLocationCodeAndFloorNoAndWorkspaceType(Date bookingDate,
			String locationCode, String floorNo, String workspaceType);

	@Modifying
	@Query("UPDATE WorkspaceItemsEntity wi SET wi.status='CANCELLED' WHERE wi.bookingId =:bookingId AND wi.bookingDate = :bookingDate")
	int cancelWorkspace(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate);

	@Query(value = "SELECT * FROM [booking].[workspace].[workspace_items] where booking_date = :bookingDate AND location_code = :locationCode AND employee_id=:employeeId AND status = 'BOOKED'", nativeQuery = true)
	WorkspaceItemsEntity findBookingAvailablityByEmployeeAndDate(@Param("bookingDate") Date bookingDate,
			@Param("locationCode") String locationCode, @Param("employeeId") String employeeId);

	@Modifying
	@Query("UPDATE WorkspaceItemsEntity wi SET wi.workspaceCode=:workspaceCode WHERE wi.bookingId =:bookingId AND wi.bookingDate = :bookingDate")
	int updateWorkspaceDetails(@Param("bookingId") BigInteger bookingId, @Param("bookingDate") Date bookingDate,
			@Param("workspaceCode") String workspaceCode);
}
