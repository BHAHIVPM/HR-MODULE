package com.bhahi.hr.dto.helpdesk;

/**
 * Shared event DTO for employee helpdesk and grievance ticketing.
 */
public record HelpdeskTicketEvent(
        Long ticketId,
        Integer employeeId,
        String creatorEmail,
        String category,
        String subject,
        String priority,
        String status
) {
}
