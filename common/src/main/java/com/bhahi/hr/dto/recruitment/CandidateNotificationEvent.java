package com.bhahi.hr.dto.recruitment;

/**
 * Shared event DTO for candidate notifications sent via Kafka from hrmodule to hrmod.
 */
public record CandidateNotificationEvent(
        Long candidateId,
        String email,
        String candidateName,
        String jobTitle,
        String stage,
        String templateType
) {
}
