package com.bhahi.hrmodule.dto.menu;

import lombok.Data;

@Data
public class DesktopMenuMainGroupOperationDto {
    private Integer mainGroupId;
    private int hierarchyId;
    private String mainGroupName;
    private String iconPath;
}