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

// Master list of leave TYPES (Casual, Sick, Earned, etc). Actual leave requests live in LeaveApplication.
@Entity
@Table(name = "leave_master")
@Data
public class LeaveMaster {

    public enum LeaveMasterStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int leaveTypeId;

    @NotNull
    @Column(length = 10, nullable = false, unique = true)
    private String leaveTypeCode; // e.g. CL, SL, EL

    @NotNull
    @Column(nullable = false)
    private String leaveTypeName; // e.g. Casual Leave

    @NotNull
    @Column(nullable = false)
    private Integer defaultDaysPerYear;

    private boolean carryForwardAllowed;

    private Integer maxCarryForwardDays;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private LeaveMasterStatus status;
}
