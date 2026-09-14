package com.bhahi.hrmodule.model.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee_master")
@Data
public class EmployeeMaster {

    public enum EmployeeStatus {ACTIVE, INACTIVE, RESIGNED, TERMINATED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeId;

    // Links back to user_login.login_id, if this employee also has portal access.
    // Nullable - not every employee needs a login (e.g. someone HR is still onboarding).
    @Column(length = 12)
    private String loginId;

    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    private String employeeCode;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @NotNull
    @Column(length = 150, nullable = false)
    private String email;

    @NotNull
    @Column(length = 10, nullable = false)
    private String mobileNo;

    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate dateOfJoining;

    // Kept as plain text for now - swap for a DepartmentMaster/DesignationMaster
    // foreign key once those tables exist and you need reporting/filtering by them.
    private String department;

    private String designation;

    // Self-referencing manager id (another employee_id), nullable for top-level roles.
    private Integer reportingManagerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private EmployeeStatus status;
}
