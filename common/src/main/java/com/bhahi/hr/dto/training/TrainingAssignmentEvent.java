package com.bhahi.hr.dto.training;

import java.time.LocalDate;

/**
 * Shared event DTO for training and LMS course assignments.
 */
public record TrainingAssignmentEvent(
        Long enrollmentId,
        Integer employeeId,
        String employeeEmail,
        Integer courseId,
        String courseName,
        LocalDate dueDate,
        String reminderType
) {
}
