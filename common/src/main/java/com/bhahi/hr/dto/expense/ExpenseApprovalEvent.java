package com.bhahi.hr.dto.expense;

import java.math.BigDecimal;

/**
 * Shared event DTO for expense approval and reimbursement events.
 */
public record ExpenseApprovalEvent(
        Long claimId,
        Integer employeeId,
        String managerEmail,
        BigDecimal amount,
        String currency,
        String status,
        String description
) {
}
