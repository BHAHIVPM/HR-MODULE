package com.bhahi.hrmodule.model.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "desktop_menu_main_group_master_operation")
@Data
@EqualsAndHashCode(callSuper = false)
public class DesktopMenuMainGroupMasterOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_main_group_id")
    private Integer mainGroupId;

    @Column(name = "db_hierarchy_id")
    private int hierarchyId;

    @NotBlank(message = "main menu Name is required")
    @Column(name = "db_main_group_name")
    private String mainGroupName;

    @Column(name = "db_icon_path")
    private String iconPath;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.entryDate = LocalDateTime.now();
    }
}