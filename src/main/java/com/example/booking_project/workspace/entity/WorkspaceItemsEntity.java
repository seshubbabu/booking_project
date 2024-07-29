package com.example.booking_project.workspace.entity;

import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.sql.Date;

@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
@Table(name= "workspace_items",schema = "workspace")
public class WorkspaceItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_item_id", columnDefinition = "bigint", nullable = false)
    private BigInteger workspaceItemId;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "requested_for", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "department")
    private String department;

    @Column(name = "division")
    private String division;

    @Column(name = "booking_id", columnDefinition = "bigint")
    private BigInteger bookingId;

    @Column(name = "booking_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Column(name = "booking_date", nullable = false)
    private Date bookingDate;

    @Column(name = "workspace_code", nullable = false)
    private String workspaceCode;

    @Column(name = "workspace_type", nullable = false)
    private String workspaceType;

    @Column(name = "floor_no", nullable = false)
    private String floorNo;

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
