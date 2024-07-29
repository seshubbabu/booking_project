package com.example.booking_project.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(includeFieldNames = true)
@Table(name = "workspace_type_master", schema = "workspace")
public class WorkspaceTypeMasterEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = 2013683770677665694L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_type_master_id", columnDefinition = "bigint", nullable = false)
    private BigInteger workspaceTypeMasterId;

    @Column(name = "workspace_type", length = 20, nullable = false)
    @Size(min = 3, max = 10)
    private String workspaceType;

    @Column(name = "workspace_type_code", length = 50, nullable = false)
    @Size(min = 1, max = 50)
    private String workspaceTypeCode;

    @Column(name = "workspace_name", length = 100)
    private String workspaceName;

    @Column(name = "location_master_id", columnDefinition = "bigint", nullable = false)
    private BigInteger locationMasterId;

    @Column(name = "capacity", length = 50, nullable = false)
    private Integer capacity;

   // @Type(type = "numeric_boolean")
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "status", columnDefinition = "BIT default 1", nullable = false)
    private boolean status;


}
