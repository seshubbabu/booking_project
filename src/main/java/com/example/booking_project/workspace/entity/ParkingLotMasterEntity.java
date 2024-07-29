package com.example.booking_project.workspace.entity;

import com.example.booking_project.workspace.enums.ParkingLotType;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Data
@Table(name="parking_lot_master", schema="workspace", uniqueConstraints ={
        @UniqueConstraint(columnNames= {"location_code", "parking_lot_type", "parking_lot_no"})
})
public class ParkingLotMasterEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 1505213766374526945L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_lot_master_id", columnDefinition = "bigint", nullable = false)
    private BigInteger parkingLotMasterId;

    @Column(name = "parking_lot_no", length = 15, nullable = false)
    private String parkingLotNo;

    @Column(name = "parking_lot_desc", length = 15, nullable = false)
    private String parkingLotDesc;

    @Column(name = "parking_lot_type", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ParkingLotType parkingLotType;

    @Column(name = "parking_cover_type", length = 15, nullable = false)
    private String parkingCoverType;

    @Column(name = "parking_desc", length = 50)
    private String parkingDesc;

    @Column(name = "location_code", columnDefinition = "String", nullable = false)
    private String locationCode;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "remarks", length = 100)
    private String remarks;

}
