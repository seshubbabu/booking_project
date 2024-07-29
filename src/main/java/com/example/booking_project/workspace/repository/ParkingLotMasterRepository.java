package com.example.booking_project.workspace.repository;

import java.math.BigInteger;
import java.util.List;

import com.example.booking_project.workspace.entity.ParkingLotMasterEntity;
import jakarta.transaction.Transactional;

//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository("ParkingLotMasterEntity")
@Transactional(rollbackOn = { Exception.class })
public interface ParkingLotMasterRepository extends JpaRepository<ParkingLotMasterEntity, BigInteger> {

	@Query(value = "SELECT parking_lot_no FROM [booking].[workspace].[parking_lot_master] where location_code = :locationCode and parking_lot_type = :parkingType and parking_lot_no > (:lastAllocatedParkingLotNo) ORDER BY parking_lot_no OFFSET 0 ROWS "
			+ "FETCH NEXT :twoWheelersExpectedCount ROWS ONLY;", nativeQuery = true)
	List<String> findNextParkingLotNo(String locationCode, String parkingType, String lastAllocatedParkingLotNo,
			int twoWheelersExpectedCount);

	@Query(value = "SELECT (SELECT count(*) as noOfTwoWheelerPlots FROM [booking].[workspace].[parking_lot_master] where location_code=:locationCode AND parking_lot_type=:parkingType) - "
			+ "(SELECT count(*) as noOfTwoWheelerPlots from [booking].[workspace].[parking_items] where  booking_date = CAST( GETDATE()+1 AS Date) AND location_code=:locationCode)", nativeQuery = true)
	int getTwoFourWheelerCount(String locationCode, String parkingType);

	@Query(value = "SELECT (SELECT COUNT(workspace_code) as avaialbleSeats from [booking].[workspace].[workspace_master]) - "
			+ "(SELECT COUNT(workspace_code) as avaialbleSeats from [booking].[workspace].[workspace_items] where  booking_date = CAST( GETDATE()+1 AS Date) AND location_code=:locationCode AND status='BOOKED')", nativeQuery = true)
	int getAvaialbleSeats(String locationCode);

	@Query(value = "SELECT ISNULL(status,'AVAILABLE') from workspace.workspace_items where  booking_date=CAST( GETDATE()+1 AS Date) AND location_code=:locationCode AND workspace_code=:workspaceCode AND status='BOOKED'", nativeQuery = true)
	String getBookedSeatStatus(String locationCode, String workspaceCode);

	@Query(value = "select count(*) as totalTwoFourWheelerPlots "
			+ "from [booking].[workspace].[parking_lot_master] where location_code=:locationCode AND parking_lot_type=:parkingLotType", nativeQuery = true)
	int findTwoFourWheelerLotNo(String locationCode, String parkingLotType);

	@Query(value = "select count(*) as totalTwoFourWheelerPlots "
			+ "from [booking].[workspace].[parking_lot_master] where parking_lot_type=:parkingLotType", nativeQuery = true)
	int findByParkingLotType(String parkingLotType);

}
