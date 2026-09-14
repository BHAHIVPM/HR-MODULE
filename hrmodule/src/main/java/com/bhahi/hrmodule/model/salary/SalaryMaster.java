package com.bhahi.hrmodule.model.salary;

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

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary_master")
@Data
public class SalaryMaster {

    public enum SalaryStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal conveyanceAllowance = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal otherAllowance = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal providentFund = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal professionalTax = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal incomeTax = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    // Computed server-side in SalaryMasterService (gross = earnings, net = gross - deductions).
    // Not meant to be set directly by clients - whatever is sent in is overwritten on save/update.
    @Column(precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal netSalary;

    @NotNull
    @Column(nullable = false)
    private LocalDate effectiveFrom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private SalaryStatus status;
}
