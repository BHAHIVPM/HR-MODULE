package com.bhahi.hrmodule.model.expense;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense_claim")
@Data
public class ExpenseClaim {

    public enum ExpenseType { TRAVEL, MEAL, EQUIPMENT, TRANSPORT, OTHER }

    public enum ClaimStatus { PENDING, APPROVED, REJECTED, REIMBURSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int claimId;

    @NotNull
    @Column(nullable = false)
    private int employeeId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ExpenseType expenseType;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 10, nullable = false)
    private String currency = "USD";

    private String receiptUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    private String managerEmail;

    private LocalDate submittedAt = LocalDate.now();
}
