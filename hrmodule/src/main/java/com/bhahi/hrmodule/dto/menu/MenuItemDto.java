package com.bhahi.hrmodule.dto.menu;

import lombok.Data;

@Data
public class MenuItemDto {
    private Integer menuNameId;
    private String menuName;
    private String componentPath;
    private int hierarchyId;
    private String addOption;
    private String editOption;
    private String deleteOption;
    private String isPrivilege;

    private boolean canView;
    private boolean canAdd;
    private boolean canEdit;
    private boolean canDelete;
}