package com.bhahi.hrmodule.model.shift;

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

import java.time.LocalTime;

// Defines a reusable shift timing (e.g. "General 9-6"). Employees are mapped to a shift
// via EmployeeShift, not referenced directly from here.
@Entity
@Table(name = "shift_master")
@Data
public class ShiftMaster {

    public enum ShiftStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shiftId;

    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    private String shiftCode;

    @NotNull
    @Column(nullable = false)
    private String shiftName;

    @NotNull
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime endTime;

    private Integer breakDurationMinutes;

    // Comma separated day names, e.g. "SATURDAY,SUNDAY".
    @Column(length = 100)
    private String weeklyOffDays;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ShiftStatus status;
}
