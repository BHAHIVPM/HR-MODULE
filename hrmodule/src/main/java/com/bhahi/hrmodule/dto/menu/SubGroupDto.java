package com.bhahi.hrmodule.dto.menu;

import lombok.Data;

import java.util.List;

@Data
public class SubGroupDto {
    private Integer subGroupId;
    private String subGroupName;
    private List<MenuItemDto> subItems;
}