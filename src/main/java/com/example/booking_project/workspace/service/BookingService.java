package com.example.booking_project.workspace.service;

import org.springframework.stereotype.Service;

import com.example.booking_project.workspace.common.dto.BlockRequestDTO;
import com.example.booking_project.workspace.common.dto.CommonResponseDTO;

/**
 * @author Sivasankar.Thalavai
 *
 *         Mar 7, 2023
 *
 */

@Service
public interface BookingService {

	CommonResponseDTO<?> blockSeat(BlockRequestDTO blockRequestDTO);

}
