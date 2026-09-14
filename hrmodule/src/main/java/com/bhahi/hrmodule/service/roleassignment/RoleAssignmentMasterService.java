package com.bhahi.hrmodule.service.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.*;
import com.bhahi.hrmodule.repository.roleassignment.*;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleAssignmentMasterService {

    private final RoleAssignmentMasterRepo roleAssignmentMasterRepo;
    private final UserLevelPrivilegeRepo userLevelPrivilegeRepo;
    private final UserRoleMasterRepo userRoleMasterRepo;
    private final RolePrivilegeRepo rolePrivilegeRepo;
    private final LoginAccessMasterRepo loginAccessMasterRepo;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getUserCategory(String loginId) {
        String code = loginId.substring(4, 6);
        return switch (code) {
            case "10" -> "SUPERADMIN";
            case "11" -> "ADMIN";
            case "12" -> "USER";
            default -> throw new RuntimeException("Forbidden: You don't have permission to access this data.");
        };
    }

    @Transactional
    public ResponseMessage<String> assignRoles(List<RoleAssignmentMaster> roleAssignments,
                                                List<UserLevelPrivilege> userLevelPrivileges) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (roleAssignments == null || roleAssignments.isEmpty()) {
                response.setHeader("Invalid input");
                response.setMessage("Role assignment list cannot be empty.");
                response.setStatusCode(400);
                return response;
            }

            String loginId = roleAssignments.get(0).getLoginId();

            // Clear existing assignments and privileges for this user
            userLevelPrivilegeRepo.deleteByLoginId(loginId);
            roleAssignmentMasterRepo.deleteByLoginId(loginId);

            // Save new role assignments
            for (RoleAssignmentMaster assignment : roleAssignments) {
                assignment.setLoginId(loginId);
            }
            roleAssignmentMasterRepo.saveAll(roleAssignments);

            // Disable edit flag for assigned roles
            for (RoleAssignmentMaster assignment : roleAssignments) {
                userRoleMasterRepo.findById(assignment.getRoleId()).ifPresent(role -> {
                    role.setEditable(false);
                    userRoleMasterRepo.save(role);
                });
            }

            // Save user-level privileges (only those with at least one permission)
            if (userLevelPrivileges != null && !userLevelPrivileges.isEmpty()) {
                List<UserLevelPrivilege> toSave = new ArrayList<>();
                for (UserLevelPrivilege priv : userLevelPrivileges) {
                    if (priv.isCanView() || priv.isCanAdd() || priv.isCanEdit() || priv.isCanDelete()) {
                        toSave.add(priv);
                    }
                }
                if (!toSave.isEmpty()) {
                    userLevelPrivilegeRepo.saveAll(toSave);
                }
            }

            response.setHeader("Success");
            response.setMessage("Roles have been assigned successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Assignment failed");
            response.setMessage("Could not assign roles. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<RoleAssignmentMaster>> getAssignedRolesByLoginId(String loginId) {
        ResponseMessage<List<RoleAssignmentMaster>> response = new ResponseMessage<>();
        try {
            List<RoleAssignmentMaster> assignments = roleAssignmentMasterRepo.findByLoginId(loginId);
            response.setResponseOutput(assignments);
            response.setHeader("Success");
            response.setMessage("Assigned roles fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch assigned roles. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<LoginAccessMaster>> getAllUsersWithRoles() {
        ResponseMessage<List<LoginAccessMaster>> response = new ResponseMessage<>();
        try {
            String userId = getUserId();
            String userType = getUserCategory(userId);
            List<LoginAccessMaster> users = loginAccessMasterRepo.findByUserType(userType);

            // Filter users who have at least one role assignment
            List<LoginAccessMaster> usersWithRoles = new ArrayList<>();
            for (LoginAccessMaster user : users) {
                List<RoleAssignmentMaster> assignments = roleAssignmentMasterRepo.findByLoginId(user.getLoginId());
                if (!assignments.isEmpty()) {
                    usersWithRoles.add(user);
                }
            }

            response.setResponseOutput(usersWithRoles);
            response.setHeader("Success");
            response.setMessage("Users with roles fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch users with roles. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<Map<String, Object>>> getRolesWithAssignmentStatus(String loginId) {
        ResponseMessage<List<Map<String, Object>>> response = new ResponseMessage<>();
        try {
            List<UserRoleMaster> allRoles = userRoleMasterRepo.findAll();
            List<RoleAssignmentMaster> userAssignments = roleAssignmentMasterRepo.findByLoginId(loginId);

            Map<Integer, RoleAssignmentMaster> assignmentMap = new HashMap<>();
            for (RoleAssignmentMaster ra : userAssignments) {
                assignmentMap.put(ra.getRoleId(), ra);
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (UserRoleMaster role : allRoles) {
                Map<String, Object> item = new HashMap<>();
                item.put("roleId", role.getRoleId());
                item.put("roleName", role.getRoleName());
                item.put("isSystem", role.isSystem());
                item.put("isEditable", role.isEditable());
                item.put("isAssignment", role.isAssignment());
                item.put("remarks", role.getRemarks());
                item.put("roleCategory", role.getRoleCategory());
                item.put("createdAt", role.getCreatedAt());

                RoleAssignmentMaster assignment = assignmentMap.get(role.getRoleId());
                if (assignment != null) {
                    item.put("isSelected", true);
                    item.put("assignedAt", assignment.getAssignedAt());
                    item.put("loginId", assignment.getLoginId());
                } else {
                    item.put("isSelected", false);
                }
                result.add(item);
            }

            response.setResponseOutput(result);
            response.setHeader("Success");
            response.setMessage("Roles with assignment status fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch roles with assignment status. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
