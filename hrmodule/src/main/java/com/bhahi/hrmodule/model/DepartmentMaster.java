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
@Table(name = "department_master")
@Data
public class DepartmentMaster {

    public enum DepartmentStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int departmentId;

    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    private String departmentCode;

    @NotNull
    @Column(nullable = false, unique = true)
    private String departmentName;

    // References employee_master.employeeId - head/manager of this department. Nullable.
    private Integer departmentHeadId;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private DepartmentStatus status;
}
