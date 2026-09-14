package com.bhahi.hrmodule.service.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.*;
import com.bhahi.hrmodule.repository.roleassignment.*;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RolePrivilegeService {

    private final RolePrivilegeRepo rolePrivilegeRepo;
    private final UserRoleMasterRepo userRoleMasterRepo;
    private final DesktopMenuNameMasterOperationRepo menuRepo;
    private final DesktopMenuMainGroupMasterOperationRepo mainGroupRepo;
    private final DesktopMenuSubGroupMasterOperationRepo subGroupRepo;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Transactional
    public ResponseMessage<String> saveRolePrivilegeBatch(List<RolePrivilege> privileges) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (privileges == null || privileges.isEmpty()) {
                response.setHeader("Invalid input");
                response.setMessage("Privilege list cannot be empty.");
                response.setStatusCode(400);
                return response;
            }

            int roleId = privileges.get(0).getRoleId();
            Optional<UserRoleMaster> roleOpt = userRoleMasterRepo.findById(roleId);
            if (roleOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Role not found.");
                response.setStatusCode(404);
                return response;
            }
            if (!roleOpt.get().isEditable()) {
                response.setHeader("Forbidden");
                response.setMessage("The role cannot be edited after it has been assigned.");
                response.setStatusCode(403);
                return response;
            }

            rolePrivilegeRepo.deleteByRoleId(roleId);

            String userId = getUserId();
            boolean isDeveloper = userId.startsWith("10", 4);
            String prefix = isDeveloper ? "1" : "2";
            int nextId = generateNextPrivilegeId(prefix);

            for (int i = 0; i < privileges.size(); i++) {
                privileges.get(i).setPrivilegeId(Integer.parseInt(prefix + (nextId + i)));
            }

            rolePrivilegeRepo.saveAll(privileges);

            UserRoleMaster role = roleOpt.get();
            role.setAssignment(true);
            userRoleMasterRepo.save(role);

            response.setHeader("Success");
            response.setMessage("Role privileges have been saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate entry");
            response.setMessage("A privilege for this menu already exists for the role.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save role privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private int generateNextPrivilegeId(String prefix) {
        List<RolePrivilege> all = rolePrivilegeRepo.findAll();
        int maxId = 0;
        for (RolePrivilege rp : all) {
            String pId = String.valueOf(rp.getPrivilegeId());
            if (pId.startsWith(prefix)) {
                int num = Integer.parseInt(pId.substring(1));
                if (num > maxId) {
                    maxId = num;
                }
            }
        }
        return maxId + 1;
    }

    public ResponseMessage<List<RolePrivilege>> getPrivilegesByRoleId(int roleId) {
        ResponseMessage<List<RolePrivilege>> response = new ResponseMessage<>();
        try {
            List<RolePrivilege> privileges = rolePrivilegeRepo.findByRoleId(roleId);
            response.setResponseOutput(privileges);
            response.setHeader("Success");
            response.setMessage("Privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Map<String, Object>>> fetchAllMenusWithPrivilege(int roleId) {
        ResponseMessage<List<Map<String, Object>>> response = new ResponseMessage<>();
        try {
            List<DesktopMenuNameMasterOperation> menus = menuRepo.findByIsPrivilege("YES");
            List<RolePrivilege> existingPrivileges = rolePrivilegeRepo.findByRoleId(roleId);
            Map<Integer, RolePrivilege> privilegeMap = new HashMap<>();
            for (RolePrivilege rp : existingPrivileges) {
                privilegeMap.put(rp.getMenuId(), rp);
            }

            Map<Integer, String> mainGroupMap = new HashMap<>();
            mainGroupRepo.findAll().forEach(mg -> mainGroupMap.put(mg.getMainGroupId(), mg.getMainGroupName()));

            Map<Integer, String> subGroupMap = new HashMap<>();
            subGroupRepo.findAll().forEach(sg -> subGroupMap.put(sg.getSubGroupId(), sg.getSubGroupName()));

            List<Map<String, Object>> result = new ArrayList<>();
            for (DesktopMenuNameMasterOperation menu : menus) {
                Map<String, Object> item = new HashMap<>();
                item.put("menuId", menu.getMenuNameId());
                item.put("menuName", menu.getMenuName());
                item.put("mainGroupId", menu.getMainGroupId());
                item.put("mainGroupName", mainGroupMap.getOrDefault(menu.getMainGroupId(), ""));
                item.put("subGroupId", menu.getSubGroupId());
                item.put("subGroupName", subGroupMap.getOrDefault(menu.getSubGroupId(), ""));
                item.put("editOption", "Yes".equalsIgnoreCase(menu.getEditOption()));
                item.put("addOption", "Yes".equalsIgnoreCase(menu.getAddOption()));
                item.put("deleteOption", "Yes".equalsIgnoreCase(menu.getDeleteOption()));

                RolePrivilege existing = privilegeMap.get(menu.getMenuNameId());
                if (existing != null) {
                    item.put("roleId", roleId);
                    item.put("isAssigned", true);
                    item.put("canView", existing.isCanView());
                    item.put("canAdd", existing.isCanAdd());
                    item.put("canEdit", existing.isCanEdit());
                    item.put("canDelete", existing.isCanDelete());
                } else {
                    item.put("roleId", 0);
                    item.put("isAssigned", false);
                    item.put("canView", false);
                    item.put("canAdd", false);
                    item.put("canEdit", false);
                    item.put("canDelete", false);
                }
                result.add(item);
            }

            response.setResponseOutput(result);
            response.setHeader("Success");
            response.setMessage("Menus with privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch menus with privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    @Transactional
    public ResponseMessage<String> deletePrivilegesByRoleId(int roleId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            rolePrivilegeRepo.deleteByRoleId(roleId);
            response.setHeader("Success");
            response.setMessage("Privileges deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
