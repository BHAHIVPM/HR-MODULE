package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

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
}