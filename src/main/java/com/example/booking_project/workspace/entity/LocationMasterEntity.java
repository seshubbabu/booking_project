package com.example.booking_project.workspace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="location_master", schema="workspace", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"location_code","location_name", "floor_no"})})
@ToString(includeFieldNames = true)
public class LocationMasterEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 2013683770677665694L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ocation_master_id", columnDefinition = "bigint", nullable=false)
    private BigInteger locationMasterId;

    @Column(name="location_code", length=20, nullable=false)
    @Size(min=3, max=20)
    private String locationCode;

    @Column(name="location_name", length=50, nullable=false)
    @Size(min=1, max=50)
    private String locationName;

    @Column(name="location_desc", length=50)
    private String locationDesc;

    @Column(name="floor_no", length=20, nullable=false)
    private String floorNo;

    @Column(name="city", length=50, nullable=false)
    private String city;

    @Column(name="state", length=50, nullable=false)
    private String state;

    @Column(name="country", length=50, nullable=false)
    private String country;

    @Column(name="status", columnDefinition = "BIT default 1", nullable=false)
    private boolean status;
}
