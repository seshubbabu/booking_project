package com.example.booking_project.workspace.entity;

import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@ToString(includeFieldNames = true)
@Table(name= "meal_items", schema = "workspace")
public class MealItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_item_id", columnDefinition = "bigint", nullable = false)
    private BigInteger mealItemId;

    @Column(name = "booking_id", columnDefinition = "bigint", nullable = false)
    private BigInteger bookingId;

    @Column(name = "booking_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Column(name = "booking_date")
    private Date bookingDate;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "requested_for", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "no_of_lunch")
    private int noOfLunch;

    @Column(name = "no_of_dinner")
    private int noOfDinner;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatusType status;
}
