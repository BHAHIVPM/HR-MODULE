package com.bhahi.hrmodule.controller.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.RoleAssignmentMaster;
import com.bhahi.hrmodule.model.roleassignment.UserLevelPrivilege;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.roleassignment.RoleAssignmentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role-assignment")
@RequiredArgsConstructor
public class RoleAssignmentMasterController {

    private final RoleAssignmentMasterService roleAssignmentMasterService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<String>> saveRoleAssignment(
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> roleAssignmentMaps = (List<Map<String, Object>>) request.get("roleAssignmentMaster");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> userLevelPrivilegeMaps = (List<Map<String, Object>>) request.get("userLevelPrivilege");

        List<RoleAssignmentMaster> roleAssignments = new java.util.ArrayList<>();
        for (Map<String, Object> map : roleAssignmentMaps) {
            RoleAssignmentMaster ra = new RoleAssignmentMaster();
            ra.setLoginId((String) map.get("loginId"));
            ra.setRoleId((Integer) map.get("roleId"));
            roleAssignments.add(ra);
        }

        List<UserLevelPrivilege> userLevelPrivileges = new java.util.ArrayList<>();
        if (userLevelPrivilegeMaps != null) {
            for (Map<String, Object> map : userLevelPrivilegeMaps) {
                UserLevelPrivilege up = new UserLevelPrivilege();
                up.setLoginId((String) map.get("loginId"));
                up.setMenuId((Integer) map.get("menuId"));
                up.setCanView(Boolean.TRUE.equals(map.get("canView")));
                up.setCanAdd(Boolean.TRUE.equals(map.get("canAdd")));
                up.setCanEdit(Boolean.TRUE.equals(map.get("canEdit")));
                up.setCanDelete(Boolean.TRUE.equals(map.get("canDelete")));
                userLevelPrivileges.add(up);
            }
        }

        ResponseMessage<String> response = roleAssignmentMasterService.assignRoles(roleAssignments, userLevelPrivileges);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseMessage<List<com.bhahi.hrmodule.model.roleassignment.LoginAccessMaster>>> getAllRoleAssignments() {
        ResponseMessage<List<com.bhahi.hrmodule.model.roleassignment.LoginAccessMaster>> response = roleAssignmentMasterService.getAllUsersWithRoles();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getById/{loginId}")
    public ResponseEntity<ResponseMessage<List<java.util.Map<String, Object>>>> getRoleAssignmentByLoginId(@PathVariable String loginId) {
        ResponseMessage<List<java.util.Map<String, Object>>> response = roleAssignmentMasterService.getRolesWithAssignmentStatus(loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/only-assigned/{loginId}")
    public ResponseEntity<ResponseMessage<List<RoleAssignmentMaster>>> getAssignedRolesByLoginId(@PathVariable String loginId) {
        ResponseMessage<List<RoleAssignmentMaster>> response = roleAssignmentMasterService.getAssignedRolesByLoginId(loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}