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
public class UserLevelPrivilegeService {

    private final UserLevelPrivilegeRepo userLevelPrivilegeRepo;
    private final RoleAssignmentMasterRepo roleAssignmentMasterRepo;
    private final RolePrivilegeRepo rolePrivilegeRepo;
    private final DesktopMenuNameMasterOperationRepo menuRepo;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public ResponseMessage<UserLevelPrivilege> save(UserLevelPrivilege privilege) {
        ResponseMessage<UserLevelPrivilege> response = new ResponseMessage<>();
        try {
            UserLevelPrivilege saved = userLevelPrivilegeRepo.save(privilege);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("User privilege saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (DataIntegrityViolationException e) {
            response.setHeader("Duplicate entry");
            response.setMessage("A privilege for this menu already exists for the user.");
            response.setStatusCode(409);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save user privilege. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<UserLevelPrivilege>> findAll() {
        ResponseMessage<List<UserLevelPrivilege>> response = new ResponseMessage<>();
        try {
            List<UserLevelPrivilege> privileges = userLevelPrivilegeRepo.findAll();
            response.setResponseOutput(privileges);
            response.setHeader("Success");
            response.setMessage("User privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch user privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<UserLevelPrivilege>> findByLoginId(String loginId) {
        ResponseMessage<List<UserLevelPrivilege>> response = new ResponseMessage<>();
        try {
            List<UserLevelPrivilege> privileges = userLevelPrivilegeRepo.findByLoginId(loginId);
            response.setResponseOutput(privileges);
            response.setHeader("Success");
            response.setMessage("User privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch user privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    @Transactional
    public ResponseMessage<String> updateUserPrivileges(List<UserLevelPrivilege> privileges) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (privileges == null || privileges.isEmpty()) {
                response.setHeader("Invalid input");
                response.setMessage("Privilege list cannot be empty.");
                response.setStatusCode(400);
                return response;
            }

            String loginId = privileges.get(0).getLoginId();
            userLevelPrivilegeRepo.deleteByLoginId(loginId);

            List<UserLevelPrivilege> toSave = new ArrayList<>();
            for (UserLevelPrivilege priv : privileges) {
                if (priv.isCanView() || priv.isCanAdd() || priv.isCanEdit() || priv.isCanDelete()) {
                    toSave.add(priv);
                }
            }

            if (!toSave.isEmpty()) {
                userLevelPrivilegeRepo.saveAll(toSave);
            }

            response.setHeader("Success");
            response.setMessage("User privileges updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update user privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Map<String, Object>>> fetchUserPrivilege(String loginId) {
        ResponseMessage<List<Map<String, Object>>> response = new ResponseMessage<>();
        try {
            List<DesktopMenuNameMasterOperation> menus = menuRepo.findByIsPrivilege("YES");
            List<RoleAssignmentMaster> roleAssignments = roleAssignmentMasterRepo.findByLoginId(loginId);

            Map<Integer, RolePrivilege> mergedRolePrivileges = new HashMap<>();
            for (RoleAssignmentMaster assignment : roleAssignments) {
                List<RolePrivilege> rolePrivileges = rolePrivilegeRepo.findByRoleId(assignment.getRoleId());
                for (RolePrivilege rp : rolePrivileges) {
                    if (mergedRolePrivileges.containsKey(rp.getMenuId())) {
                        RolePrivilege existing = mergedRolePrivileges.get(rp.getMenuId());
                        existing.setCanView(existing.isCanView() || rp.isCanView());
                        existing.setCanAdd(existing.isCanAdd() || rp.isCanAdd());
                        existing.setCanEdit(existing.isCanEdit() || rp.isCanEdit());
                        existing.setCanDelete(existing.isCanDelete() || rp.isCanDelete());
                    } else {
                        RolePrivilege copy = new RolePrivilege();
                        copy.setMenuId(rp.getMenuId());
                        copy.setCanView(rp.isCanView());
                        copy.setCanAdd(rp.isCanAdd());
                        copy.setCanEdit(rp.isCanEdit());
                        copy.setCanDelete(rp.isCanDelete());
                        mergedRolePrivileges.put(rp.getMenuId(), copy);
                    }
                }
            }

            List<UserLevelPrivilege> userPrivileges = userLevelPrivilegeRepo.findByLoginId(loginId);
            Map<Integer, UserLevelPrivilege> userPrivilegeMap = new HashMap<>();
            for (UserLevelPrivilege up : userPrivileges) {
                userPrivilegeMap.put(up.getMenuId(), up);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (DesktopMenuNameMasterOperation menu : menus) {
                Map<String, Object> item = new HashMap<>();
                item.put("menuId", menu.getMenuNameId());
                item.put("menuName", menu.getMenuName());
                item.put("editOption", "Yes".equalsIgnoreCase(menu.getEditOption()));

                UserLevelPrivilege userPriv = userPrivilegeMap.get(menu.getMenuNameId());
                if (userPriv != null) {
                    item.put("canView", userPriv.isCanView());
                    item.put("canAdd", userPriv.isCanAdd());
                    item.put("canEdit", userPriv.isCanEdit());
                    item.put("canDelete", userPriv.isCanDelete());
                } else {
                    item.put("canView", false);
                    item.put("canAdd", false);
                    item.put("canEdit", false);
                    item.put("canDelete", false);
                }
                item.put("isEditable", "Yes".equalsIgnoreCase(menu.getEditOption()));
                result.add(item);
            }

            response.setResponseOutput(result);
            response.setHeader("Success");
            response.setMessage("User privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch user privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Map<String, Object>>> fetchMultipleRolePrivileges(List<Integer> roleIds, String loginId) {
        ResponseMessage<List<Map<String, Object>>> response = new ResponseMessage<>();
        try {
            Map<Integer, Map<String, Object>> mergedPrivileges = new HashMap<>();

            for (int roleId : roleIds) {
                List<RolePrivilege> rolePrivileges = rolePrivilegeRepo.findByRoleId(roleId);
                for (RolePrivilege rp : rolePrivileges) {
                    if (mergedPrivileges.containsKey(rp.getMenuId())) {
                        Map<String, Object> existing = mergedPrivileges.get(rp.getMenuId());
                        existing.put("canView", (Boolean) existing.get("canView") || rp.isCanView());
                        existing.put("canAdd", (Boolean) existing.get("canAdd") || rp.isCanAdd());
                        existing.put("canEdit", (Boolean) existing.get("canEdit") || rp.isCanEdit());
                        existing.put("canDelete", (Boolean) existing.get("canDelete") || rp.isCanDelete());
                    } else {
                        Map<String, Object> item = new HashMap<>();
                        item.put("menuId", rp.getMenuId());
                        item.put("roleId", roleId);
                        item.put("canView", rp.isCanView());
                        item.put("canAdd", rp.isCanAdd());
                        item.put("canEdit", rp.isCanEdit());
                        item.put("canDelete", rp.isCanDelete());
                        mergedPrivileges.put(rp.getMenuId(), item);
                    }
                }
            }

            List<UserLevelPrivilege> userPrivileges = userLevelPrivilegeRepo.findByLoginId(loginId);
            for (UserLevelPrivilege up : userPrivileges) {
                if (mergedPrivileges.containsKey(up.getMenuId())) {
                    Map<String, Object> item = mergedPrivileges.get(up.getMenuId());
                    item.put("canView", up.isCanView());
                    item.put("canAdd", up.isCanAdd());
                    item.put("canEdit", up.isCanEdit());
                    item.put("canDelete", up.isCanDelete());
                }
            }

            response.setResponseOutput(new ArrayList<>(mergedPrivileges.values()));
            response.setHeader("Success");
            response.setMessage("Multiple role privileges fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch multiple role privileges. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
