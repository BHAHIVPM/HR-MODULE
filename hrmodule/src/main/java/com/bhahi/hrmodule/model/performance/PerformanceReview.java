package com.bhahi.hrmodule.model.performance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

// Appraisal cycles and ratings. Maps employee_master.employeeId (the person being reviewed)
// to employee_master.employeeId again (reviewerId - their manager/HR), so both sides resolve
// against the same employee table.
@Entity
@Table(name = "performance_review")
@Data
public class PerformanceReview {

    public enum ReviewStatus {DRAFT, SUBMITTED, REVIEWED, ACKNOWLEDGED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    // References employee_master.employeeId - the employee being reviewed.
    @NotNull
    @Column(nullable = false)
    private Integer employeeId;

    // References employee_master.employeeId - the reviewing manager.
    private Integer reviewerId;

    // e.g. "2026-H1", "Q1-2026", "Annual-2026".
    @NotNull
    @Column(nullable = false, length = 30)
    private String reviewCycle;

    @NotNull
    @Column(nullable = false)
    private LocalDate reviewPeriodStart;

    @NotNull
    @Column(nullable = false)
    private LocalDate reviewPeriodEnd;

    // 1.0 - 5.0 scale.
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double overallRating;

    @Column(length = 2000)
    private String achievements;

    @Column(length = 2000)
    private String strengths;

    @Column(length = 2000)
    private String areasOfImprovement;

    @Column(length = 2000)
    private String goalsForNextCycle;

    @Column(length = 2000)
    private String reviewerComments;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private ReviewStatus status;

    private LocalDate reviewDate;
}
