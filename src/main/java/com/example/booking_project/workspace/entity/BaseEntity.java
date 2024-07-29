package com.example.booking_project.workspace.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<T> {

    @CreatedBy
    @Column(name="created_by", length= 50, updatable = false, nullable = false)
    protected T createdBy;

    @CreatedDate
    @Column(name="created_date", columnDefinition = "datetime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date creationDate;

    @LastModifiedBy
    @Column(name= "last_modified_by", length= 50, nullable= false)
    protected T lastModifiedBy;

    @LastModifiedDate
    @Column(name="last_modified_date",columnDefinition = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModifiedDate;
}
