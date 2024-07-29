package com.example.booking_project.workspace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
public class LocationKey implements Serializable {

    private static final long serialVersionUID= 2581110957868057258L;

    @Column(name="location_code", length=10, nullable=false)
    @Size(min=3, max=10)
    private String locationCode;

    @Column(name="floor_no",length =3, nullable=false)
    @Size(min=1, max=3)
    private int floorNo;
}
