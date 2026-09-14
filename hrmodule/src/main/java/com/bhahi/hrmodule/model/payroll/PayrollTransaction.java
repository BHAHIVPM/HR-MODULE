package com.bhahi.hrmodule.model.payroll;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// One generated payslip per employee per month. Computed by PayrollTransactionService from
// salary_master (pay structure), attendance (days present), and leave_application (paid vs
// unpaid leave) - see salaryId/employeeId below for the source records used.
@Entity
@Table(name = "payroll_transaction",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId", "payrollMonth", "payrollYear"}))
@Data
public class PayrollTransaction {

    public enum PayrollStatus {GENERATED, PAID, CANCELLED, ON_HOLD}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payrollId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    // References salary_master.salaryId - the salary structure this payslip was computed from.
    @NotNull
    @Column(nullable = false)
    private Integer salaryId;

    @NotNull
    @Column(nullable = false)
    private Integer payrollMonth; // 1-12

    @NotNull
    @Column(nullable = false)
    private Integer payrollYear;

    private Integer workingDays;

    private Integer presentDays;

    private Double paidLeaveDays;

    // Loss-of-pay days (unapproved absence / leave beyond entitlement).
    private Double lopDays;

    @Column(precision = 12, scale = 2)
    private BigDecimal grossEarnings;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalDeductions;

    // LOP deduction for the days not worked/paid, on top of the salary structure's deductions.
    @Column(precision = 12, scale = 2)
    private BigDecimal lopDeduction;

    @Column(precision = 12, scale = 2)
    private BigDecimal netPay;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private PayrollStatus status;

    private LocalDateTime generatedOn;

    private LocalDateTime paidOn;

    private String remarks;
}
