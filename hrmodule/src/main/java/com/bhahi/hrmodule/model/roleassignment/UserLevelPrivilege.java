package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_level_privilege")
@Data
public class UserLevelPrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int tableId;

    @Column(name = "db_login_id", length = 12, nullable = false)
    private String loginId;

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

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    protected void onCreate() {
        this.entryDate = LocalDateTime.now();
    }
}