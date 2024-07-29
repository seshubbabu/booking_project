package com.example.booking_project.workspace.entity;

import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name="parking_items", schema= "workspace")
@Data
@ToString(includeFieldNames=true)
public class ParkingItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_item_id", columnDefinition = "bigint", nullable = false)
    private BigInteger parkingItemId;

    @Column(name = "booking_id", columnDefinition = "bigint", nullable = false)
    private BigInteger bookingId;

    @Column(name = "booking_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Column(name = "booking_date", nullable = false)
    private Date bookingDate;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "requested_for", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "two_wheeler_parking_lot")
    private String twoWheelerNo;

    @Column(name = "four_wheeler_parking_lot")
    private String fourWheelerNo;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @Column(name = "floor_no", nullable = false)
    private String floorNo;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatusType status;
}
