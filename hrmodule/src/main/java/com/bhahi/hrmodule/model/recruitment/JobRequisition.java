package com.bhahi.hrmodule.model.recruitment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "job_requisition")
@Data
public class JobRequisition {

    public enum RequisitionStatus { OPEN, CLOSED, ON_HOLD }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;

    @NotNull
    @Column(nullable = false)
    private String jobTitle;

    @NotNull
    @Column(nullable = false)
    private String department;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RequisitionStatus status = RequisitionStatus.OPEN;

    private String createdBy;

    private LocalDate createdAt = LocalDate.now();
}
