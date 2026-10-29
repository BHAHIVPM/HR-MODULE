package com.bhahi.hrmodule.model.benefits;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee_benefit")
@Data
public class EmployeeBenefit {

    public enum PlanType { HEALTH_INSURANCE, LIFE_INSURANCE, RETIREMENT_PF, GRATUITY, FLEXIBLE_ALLOWANCE }

    public enum BenefitStatus { ACTIVE, PENDING, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int enrollmentId;

    @NotNull
    @Column(nullable = false)
    private int employeeId;

    private String employeeEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private PlanType planType;

    @NotNull
    @Column(nullable = false)
    private String planName;

    private LocalDate effectiveDate = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BenefitStatus status = BenefitStatus.ACTIVE;
}
