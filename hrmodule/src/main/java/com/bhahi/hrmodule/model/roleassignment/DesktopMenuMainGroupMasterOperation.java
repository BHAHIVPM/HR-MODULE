package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "desktop_menu_main_group_master_operation")
@Data
public class DesktopMenuMainGroupMasterOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_main_group_id")
    private int mainGroupId;

    @Column(name = "db_main_group_name")
    private String mainGroupName;
}