package com.bhahi.hrmodule.model.training;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "course_enrollment")
@Data
public class CourseEnrollment {

    public enum EnrollmentStatus { ASSIGNED, IN_PROGRESS, COMPLETED, OVERDUE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int enrollmentId;

    @NotNull
    @Column(nullable = false)
    private int courseId;

    @NotNull
    @Column(nullable = false)
    private int employeeId;

    private String employeeEmail;

    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ASSIGNED;

    private LocalDate enrolledAt = LocalDate.now();
}
