package com.bhahi.hr.dto.exit;

import java.time.LocalDate;

/**
 * Shared event DTO for employee exit, resignation, and clearance workflows.
 */
public record ExitClearanceEvent(
        Long exitId,
        Integer employeeId,
        String employeeEmail,
        LocalDate resignationDate,
        LocalDate lastWorkingDay,
        String clearanceDepartment,
        String status
) {
}
