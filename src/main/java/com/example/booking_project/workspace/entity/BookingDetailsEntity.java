package com.example.booking_project.workspace.entity;

import com.example.booking_project.workspace.enums.BookingStatusType;
import com.example.booking_project.workspace.enums.BookingType;
import com.example.booking_project.workspace.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;
//import java.util.Date;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "booking_details", schema="workspace")
@ToString(includeFieldNames = true)
public class BookingDetailsEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = -354966634645990742L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", columnDefinition = "bigint")
    private BigInteger bookingId;

    @Column(name="booking_type", nullable=false)
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Column(name="requested_date", nullable=false)
    private Date requestedDate;

    @Column(name="requested_id", nullable=false)
    private String requestedId;

    @Column(name="requested_name", nullable= false)
    private String requesterName;

    @Column(name="requested_for", nullable=false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name="booking_start_date", nullable=false)
    private Date bookingStartDate;

    @Column(name="booking_end_date", nullable=false)
    private Date bookingEndDate;

    @Column(name="status", nullable=false)
    @Enumerated(EnumType.STRING)
    private BookingStatusType status;

    @Column(name="is_default_booking_preference")
    private boolean isDefaultBookingPreference = false;
}
