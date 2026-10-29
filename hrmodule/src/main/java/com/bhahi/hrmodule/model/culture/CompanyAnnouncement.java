package com.bhahi.hrmodule.model.culture;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "company_announcement")
@Data
public class CompanyAnnouncement {

    public enum MessageType { GENERAL_NOTICE, POLICY_UPDATE, RECOGNITION, EVENT_CELEBRATION }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int announcementId;

    @NotNull
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String targetDepartment = "ALL";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private MessageType messageType = MessageType.GENERAL_NOTICE;

    private String authorEmail;

    private LocalDate broadcastDate = LocalDate.now();
}
