package com.bhahi.hrmodule.dto.menu;

import lombok.Data;

@Data
public class DesktopMenuNameMasterOperationDto {
    private Integer menuNameId;
    private String menuName;
    private String componentPath;
    private int hierarchyId;
    private int mainGroupId;
    private String addOption;
    private String editOption;
    private String deleteOption;
    private String isPrivilege;
    private String subGroupName;
    private Integer subGroupId;
}