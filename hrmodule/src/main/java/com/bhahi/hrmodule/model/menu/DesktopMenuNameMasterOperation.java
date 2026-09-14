package com.bhahi.hrmodule.model.menu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "desktop_menu_name_master_operation")
@Data
public class DesktopMenuNameMasterOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_menu_name_id")
    private Integer menuNameId;

    @NotBlank(message = "main Name is required")
    @Column(name = "db_menu_name")
    private String menuName;

    @Column(name = "db_component_path")
    private String componentPath;

    @Column(name = "db_hierarchy_id")
    private int hierarchyId;

    @Column(name = "db_main_group_id")
    private int mainGroupId;

    @Column(name = "db_add_option")
    private String addOption;

    @Column(name = "db_edit_option")
    private String editOption;

    @Column(name = "db_delete_option")
    private String deleteOption;

    @Column(name = "db_is_privilege")
    private String isPrivilege;

    @Column(name = "db_sub_group_id")
    private Integer subGroupId;

    @Column(name = "db_entry_date")
    private LocalDateTime entryDate;

    @Transient
    private String subGroupName;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.entryDate = LocalDateTime.now();
    }
}