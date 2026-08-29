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

import java.time.LocalDate;

// Mapping table: which employee is on which shift, and from when. Keeping this separate
// from EmployeeMaster (rather than a single shiftId column on the employee) preserves shift
// history when someone's timing changes instead of overwriting it.
@Entity
@Table(name = "employee_shift")
@Data
public class EmployeeShift {

    public enum EmployeeShiftStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeShiftId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    // References shift_master.shiftId.
    @NotNull
    @Column(nullable = false)
    private Integer shiftId;

    @NotNull
    @Column(nullable = false)
    private LocalDate effectiveFrom;

    // Null = still in effect.
    private LocalDate effectiveTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private EmployeeShiftStatus status;
}
