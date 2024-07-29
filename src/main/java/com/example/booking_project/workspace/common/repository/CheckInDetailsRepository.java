package com.example.booking_project.workspace.common.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.booking_project.workspace.common.entity.CheckInDetails;

/**
 * @author Sivasankar.Thalavai
 *
 *         Apr 3, 2023
 */
@Repository
public interface CheckInDetailsRepository extends JpaRepository<CheckInDetails, BigInteger> {

}
