package com.bhahi.hrmodule.dto.menu;

import lombok.Data;

import java.util.List;

@Data
public class MenuResponseDto {
    private Integer mainGroupId;
    private int hierarchyId;
    private String mainGroupName;
    private String iconPath;
    private List<SubGroupDto> subGroup;
}