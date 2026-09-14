package com.bhahi.hrmodule.model.leave;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_application")
@Data
public class LeaveApplication {

    public enum LeaveApplicationStatus {PENDING, APPROVED, REJECTED, CANCELLED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int leaveApplicationId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    // References leave_master.leaveTypeId.
    @NotNull
    @Column(nullable = false)
    private Integer leaveTypeId;

    @NotNull
    @Column(nullable = false)
    private LocalDate fromDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate toDate;

    // Double so half-day leave (0.5) is representable.
    @NotNull
    @Column(nullable = false)
    private Double noOfDays;

    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private LeaveApplicationStatus status;

    private LocalDateTime appliedOn;

    // References employee_master.employeeId of the approving manager/HR.
    private Integer approvedBy;

    private LocalDateTime approvedOn;

    private String remarks;
}
