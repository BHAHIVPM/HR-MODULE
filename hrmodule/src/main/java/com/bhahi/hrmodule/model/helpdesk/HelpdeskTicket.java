package com.bhahi.hrmodule.model.helpdesk;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "helpdesk_ticket")
@Data
public class HelpdeskTicket {

    public enum TicketCategory { PAYROLL, IT_SUPPORT, HR_POLICY, FACILITIES, GENERAL }

    public enum TicketPriority { LOW, MEDIUM, HIGH, URGENT }

    public enum TicketStatus { OPEN, IN_PROGRESS, RESOLVED, CLOSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;

    @NotNull
    @Column(nullable = false)
    private int employeeId;

    private String creatorEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TicketCategory category;

    @NotNull
    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    private LocalDate createdAt = LocalDate.now();
}
