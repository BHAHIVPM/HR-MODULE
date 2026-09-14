package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_privilege_master")
@Data
public class RolePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int tableId;

    @Column(name = "db_privilege_id", nullable = false, unique = true)
    private int privilegeId;

    @Column(name = "db_role_id", nullable = false)
    private int roleId;

    @Column(name = "db_menu_id", nullable = false)
    private int menuId;

    @Column(name = "db_can_view")
    private boolean canView;

    @Column(name = "db_can_add")
    private boolean canAdd;

    @Column(name = "db_can_edit")
    private boolean canEdit;

    @Column(name = "db_can_delete")
    private boolean canDelete;

    @Column(name = "db_created_at")
    private LocalDateTime createdAt;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.entryDate = LocalDateTime.now();
    }
}