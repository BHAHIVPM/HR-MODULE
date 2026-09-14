package com.bhahi.hrmodule.service.menu;

import com.bhahi.hrmodule.dto.menu.MenuItemDto;
import com.bhahi.hrmodule.dto.menu.MenuResponseDto;
import com.bhahi.hrmodule.dto.menu.SubGroupDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import com.bhahi.hrmodule.model.roleassignment.UserLevelPrivilege;
import com.bhahi.hrmodule.repository.menu.DesktopMenuMainGroupOperationRepository;
import com.bhahi.hrmodule.repository.menu.DesktopMenuMasterOperationRepository;
import com.bhahi.hrmodule.repository.menu.DesktopSubMenuOperationRepository;
import com.bhahi.hrmodule.repository.roleassignment.UserLevelPrivilegeRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuHierarchyService {

    private final DesktopMenuMainGroupOperationRepository mainGroupRepo;
    private final DesktopSubMenuOperationRepository subGroupRepo;
    private final DesktopMenuMasterOperationRepository menuItemRepo;
    private final UserLevelPrivilegeRepo userLevelPrivilegeRepo;

    private static final Logger logger = LoggerFactory.getLogger(MenuHierarchyService.class);

    public ResponseMessage<List<MenuResponseDto>> getMenuHierarchy() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("User '{}' called get full menu hierarchy (English only).", userId);
        ResponseMessage<List<MenuResponseDto>> response = new ResponseMessage<>();
        try {
            Map<Integer, UserLevelPrivilege> privilegeMap = userLevelPrivilegeRepo.findByLoginId(userId).stream().collect(Collectors.toMap(UserLevelPrivilege::getMenuId, p -> p));
            List<MenuResponseDto> menuList = buildMenuHierarchy(privilegeMap);
            response.setHeader("SUCCESS");
            response.setMessage("Menu hierarchy fetched successfully.");
            response.setStatusCode(200);
            response.setResponseOutput(menuList);
            return response;
        } catch (Exception e) {
            logger.error("Exception logging: ", e);
            response.setHeader("Error");
            response.setMessage("An unexpected error occurred.");
            response.setStatusCode(500);
            return response;
        }
    }

    public List<MenuResponseDto> buildMenuHierarchy(Map<Integer, UserLevelPrivilege> privilegeMap) {
        List<DesktopMenuMainGroupMasterOperation> mainGroups = mainGroupRepo.findAllByOrderByHierarchyIdAsc();
        List<DesktopMenuSubGroupMasterOperation> subGroups = subGroupRepo.findAll();
        List<DesktopMenuNameMasterOperation> menuItems = menuItemRepo.findAllOrderedByHierarchy();
        Map<Integer, DesktopMenuSubGroupMasterOperation> subGroupMap = mapSubGroups(subGroups);
        return buildFinalMenuList(mainGroups, menuItems, subGroupMap, privilegeMap);
    }

    private List<MenuResponseDto> buildFinalMenuList(List<DesktopMenuMainGroupMasterOperation> mainGroups, List<DesktopMenuNameMasterOperation> allMenuItems, Map<Integer, DesktopMenuSubGroupMasterOperation> subGroupMap, Map<Integer, UserLevelPrivilege> privilegeMap) {
        List<MenuResponseDto> finalList = new ArrayList<>();
        for (DesktopMenuMainGroupMasterOperation mg : mainGroups) {
            List<DesktopMenuNameMasterOperation> itemsForMainGroup = allMenuItems.stream().filter(m -> m.getMainGroupId() == mg.getMainGroupId()).filter(m -> isMenuAllowed(m, privilegeMap)).sorted(Comparator.comparingInt(DesktopMenuNameMasterOperation::getHierarchyId)).toList();
            if (!itemsForMainGroup.isEmpty()) {
                List<SubGroupDto> subGroups = buildSubGroupsInHierarchyOrder(itemsForMainGroup, subGroupMap, privilegeMap);
                if (!subGroups.isEmpty()) {
                    MenuResponseDto response = new MenuResponseDto();
                    response.setMainGroupId(mg.getMainGroupId());
                    response.setHierarchyId(mg.getHierarchyId());
                    response.setMainGroupName(mg.getMainGroupName());
                    response.setIconPath(mg.getIconPath());
                    response.setSubGroup(subGroups);
                    finalList.add(response);
                }
            }
        }
        return finalList;
    }

    private List<SubGroupDto> buildSubGroupsInHierarchyOrder(List<DesktopMenuNameMasterOperation> menuItems, Map<Integer, DesktopMenuSubGroupMasterOperation> subGroupMap, Map<Integer, UserLevelPrivilege> privilegeMap) {
        List<SubGroupDto> result = new ArrayList<>();
        SubGroupDto currentSubGroup = null;
        Integer lastSubGroupId = null;
        for (DesktopMenuNameMasterOperation m : menuItems) {
            Integer currentSubGroupId = m.getSubGroupId();
            if (!Objects.equals(currentSubGroupId, lastSubGroupId)) {
                currentSubGroup = new SubGroupDto();
                currentSubGroup.setSubGroupId(currentSubGroupId);
                DesktopMenuSubGroupMasterOperation sub = subGroupMap.get(currentSubGroupId);
                currentSubGroup.setSubGroupName(sub != null ? sub.getSubGroupName() : null);
                currentSubGroup.setSubItems(new ArrayList<>());
                result.add(currentSubGroup);
                lastSubGroupId = currentSubGroupId;
            }
            MenuItemDto item = convertMenuItem(m);
            applyPrivileges(item, m, privilegeMap);
            currentSubGroup.getSubItems().add(item);
        }
        return result;
    }

    private boolean isMenuAllowed(DesktopMenuNameMasterOperation menu, Map<Integer, UserLevelPrivilege> privilegeMap) {
        if ("NO".equalsIgnoreCase(menu.getIsPrivilege())) {
            return true;
        }
        return privilegeMap.containsKey(menu.getMenuNameId());
    }

    private void applyPrivileges(MenuItemDto item, DesktopMenuNameMasterOperation menu, Map<Integer, UserLevelPrivilege> privilegeMap) {
        if ("NO".equalsIgnoreCase(menu.getIsPrivilege())) {
            item.setCanView(true);
            item.setCanAdd("YES".equalsIgnoreCase(menu.getAddOption()));
            item.setCanEdit("YES".equalsIgnoreCase(menu.getEditOption()));
            item.setCanDelete("YES".equalsIgnoreCase(menu.getDeleteOption()));
            return;
        }
        UserLevelPrivilege p = privilegeMap.get(menu.getMenuNameId());
        if (p != null) {
            item.setCanView(p.isCanView());
            item.setCanAdd(p.isCanAdd());
            item.setCanEdit(p.isCanEdit());
            item.setCanDelete(p.isCanDelete());
        }
    }

    private Map<Integer, DesktopMenuSubGroupMasterOperation> mapSubGroups(List<DesktopMenuSubGroupMasterOperation> subGroups) {
        return subGroups.stream().collect(Collectors.toMap(DesktopMenuSubGroupMasterOperation::getSubGroupId, s -> s));
    }

    private MenuItemDto convertMenuItem(DesktopMenuNameMasterOperation dto) {
        MenuItemDto item = new MenuItemDto();
        item.setMenuNameId(dto.getMenuNameId());
        item.setMenuName(dto.getMenuName());
        item.setComponentPath(dto.getComponentPath());
        item.setHierarchyId(dto.getHierarchyId());
        item.setAddOption(dto.getAddOption());
        item.setEditOption(dto.getEditOption());
        item.setDeleteOption(dto.getDeleteOption());
        item.setIsPrivilege(dto.getIsPrivilege());
        return item;
    }
}