package com.example.booking_project.workspace.repository;

import java.math.BigInteger;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.booking_project.workspace.entity.LocationMasterEntity;

/**
 * @author Sivasankar.Thalavai
 *
 *         May 12, 2023
 */
@Repository("locationMasterRepository")
@Transactional(rollbackOn = { Exception.class })
public interface LocationMasterRepository extends JpaRepository<LocationMasterEntity, BigInteger> {

	LocationMasterEntity findByLocationCodeAndFloorNo(String locationCode, String floorNo);

	@Query(value = "SELECT location_code FROM workspace.location_master ", nativeQuery = true)
	List<String> fetchAllLocations();
}