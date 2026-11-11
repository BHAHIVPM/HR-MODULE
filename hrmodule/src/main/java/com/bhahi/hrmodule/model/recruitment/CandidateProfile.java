package com.bhahi.hrmodule.model.recruitment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_profile")
@Data
public class CandidateProfile {

    public enum CandidateStage { APPLIED, SHORTLISTED, INTERVIEW, OFFERED, HIRED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int candidateId;

    @NotNull
    @Column(nullable = false)
    private int jobId;

    @NotNull
    @Column(nullable = false)
    private String fullName;

    @NotNull
    @Column(nullable = false)
    private String email;

    private String phone;

    private String resumeUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CandidateStage stage = CandidateStage.APPLIED;

    private LocalDateTime interviewDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate appliedAt = LocalDate.now();
}
