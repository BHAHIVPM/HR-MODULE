package com.bhahi.hrmodule.model.roleassignment;

import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // Plain FK value column - writable, all existing getMenuId()/setMenuId() code keeps working
    @Column(name = "db_menu_id", nullable = false)
    private int menuId;

    /**
     * The association that tells Hibernate the TARGET entity (and therefore the target table
     * desktop_menu_name_master_operation via its @Table, matched on db_menu_name_id via @Id).
     * This is what actually generates the FK constraint in the DDL.
     * Read-only (insertable=false, updatable=false): the int menuId above stays the single
     * writable source of truth for the same column, so no repeated-column conflict.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "db_menu_id", referencedColumnName = "db_menu_name_id",
            insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_user_level_privilege_menu"))
    @JsonIgnore // never serialized/deserialized; the JSON contract stays {"menuId": ...}
    private DesktopMenuNameMasterOperation menuMaster;

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