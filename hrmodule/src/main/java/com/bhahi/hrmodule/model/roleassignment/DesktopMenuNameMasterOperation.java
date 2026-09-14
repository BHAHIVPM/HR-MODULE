package com.bhahi.hrmodule.model.roleassignment;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "desktop_menu_name_master_operation")
@Data
public class DesktopMenuNameMasterOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "db_menu_name_id")
    private int menuNameId;

    @Column(name = "db_menu_name")
    private String menuName;

    @Column(name = "db_main_group_id")
    private int mainGroupId;

    @Column(name = "db_sub_group_id")
    private int subGroupId;

    @Column(name = "db_edit_option", length = 5)
    private String editOption;

    @Column(name = "db_add_option", length = 5)
    private String addOption;

    @Column(name = "db_delete_option", length = 5)
    private String deleteOption;

    @Column(name = "db_is_privilege", length = 5)
    private String isPrivilege;
}