package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_assignment_master")
@Data
public class RoleAssignmentMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int tableId;

    @Column(name = "db_login_id", length = 12, nullable = false)
    private String loginId;

    @Column(name = "db_role_id", nullable = false)
    private int roleId;

    @Column(name = "db_assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        this.entryDate = LocalDateTime.now();
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }
    }
}