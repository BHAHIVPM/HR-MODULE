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

// Employee bank account used for salary disbursement. Maps 1-to-many with employee_master
// (an employee can have multiple accounts on file, only one flagged primary at a time).
@Entity
@Table(name = "bank_details")
@Data
public class BankDetails {

    public enum AccountType {SAVINGS, CURRENT}

    public enum BankDetailsStatus {ACTIVE, INACTIVE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bankDetailId;

    // References employee_master.employeeId.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    @NotNull
    @Column(nullable = false)
    private String bankName;

    private String branchName;

    @NotNull
    @Column(nullable = false, length = 30)
    private String accountNumber;

    @NotNull
    @Column(nullable = false, length = 15)
    private String ifscCode;

    @NotNull
    @Column(nullable = false)
    private String accountHolderName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private AccountType accountType;

    // Payroll should disburse to whichever record has this set to true for the employee.
    // PayrollTransactionService/BankDetailsService enforce only one primary per employee.
    private boolean isPrimary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private BankDetailsStatus status;
}
