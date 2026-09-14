package com.bhahi.hrmodule.model.menu;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "desktop_menu_sub_group_master_operation")
@Data
public class DesktopMenuSubGroupMasterOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_sub_group_id")
    private int subGroupId;

    @Column(name = "db_sub_group_name")
    private String subGroupName;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.entryDate = LocalDateTime.now();
    }
}