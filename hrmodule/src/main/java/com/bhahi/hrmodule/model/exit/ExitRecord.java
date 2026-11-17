package com.bhahi.hrmodule.model.exit;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "exit_record")
@Data
public class ExitRecord {

    public enum ExitStatus { SUBMITTED, CLEARANCE_IN_PROGRESS, SETTLEMENT_PENDING, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int exitId;

    @NotNull
    @Column(nullable = false)
    private int employeeId;

    private String employeeEmail;

    private LocalDate resignationDate = LocalDate.now();

    private LocalDate lastWorkingDay;

    @Column(length = 50)
    private String clearanceDepartment = "ALL";

    @Column(columnDefinition = "TEXT")
    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ExitStatus status = ExitStatus.SUBMITTED;
}
