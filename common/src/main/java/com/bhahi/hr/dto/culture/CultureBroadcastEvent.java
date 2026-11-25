package com.bhahi.hr.dto.culture;

import java.time.LocalDate;

/**
 * Shared event DTO for internal communication, company announcements, and peer recognition.
 */
public record CultureBroadcastEvent(
        Long announcementId,
        String title,
        String targetDepartment,
        String messageType,
        String authorEmail,
        LocalDate broadcastDate
) {
}
