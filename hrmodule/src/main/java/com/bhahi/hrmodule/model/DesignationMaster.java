package com.bhahi.hrmodule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "designation_master")
@Data
public class DesignationMaster {

    public enum DesignationStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int designationId;

    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    private String designationCode;

    @NotNull
    @Column(nullable = false)
    private String designationName;

    // References department_master.departmentId. Nullable - some designations may span departments.
    private Integer departmentId;

    private String gradeLevel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private DesignationStatus status;
}
