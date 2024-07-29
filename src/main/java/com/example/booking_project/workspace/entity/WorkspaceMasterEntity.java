package com.example.booking_project.workspace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(name="workspace_master", schema="workspace", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"location_master_id", "workspace_code", "workspace_no"})})
public class WorkspaceMasterEntity extends BaseEntity<String> implements Serializable {

    private static final long serialVersionUID = -5852030900018786552L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_master_id", columnDefinition = "bigint", nullable = false)
    private BigInteger workspaceMasterId;

    @Column(name = "workspace_code", length = 20, nullable = false)
    private String workspaceCode;

    @Column(name = "workspace_no", length = 20, nullable = false)
    private String workspaceNo;

    @Column(name = "workspace_type", length = 25, nullable = false)
    private String workspaceType;

    @Column(name = "workspace_desc", length = 50)
    private String workspaceDesc;

    @Column(name = "location_master_id", columnDefinition = "bigint", nullable = false)
    private BigInteger locationMasterId;

    @Column(name = "status", nullable = false)
    private String status;

}
