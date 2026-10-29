package com.bhahi.hr.dto.benefits;

import java.time.LocalDate;

/**
 * Shared event DTO for employee benefit and insurance enrollments.
 */
public record BenefitEnrollmentEvent(
        Long enrollmentId,
        Integer employeeId,
        String employeeEmail,
        String planType,
        String planName,
        LocalDate effectiveDate,
        String status
) {
}
