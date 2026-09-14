package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_role_master")
@Data
public class UserRoleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_role_id")
    private int roleId;

    @Column(name = "db_role_name", nullable = false, unique = true)
    private String roleName;

    @Column(name = "db_remarks")
    private String remarks;

    @Column(name = "db_role_category", length = 20)
    private String roleCategory;

    @Column(name = "db_is_system")
    private boolean system;

    @Column(name = "db_is_editable")
    private boolean editable = true;

    @Column(name = "db_is_assigned")
    private boolean assignment;

    @Column(name = "db_created_at")
    private LocalDateTime createdAt;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.entryDate = LocalDateTime.now();
        this.editable = true;
    }
}