package com.bhahi.hrmodule.controller.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.RolePrivilege;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.roleassignment.RolePrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role-privilege")
@RequiredArgsConstructor
public class RolePrivilegeController {

    private final RolePrivilegeService rolePrivilegeService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/set-privilege-batch")
    public ResponseEntity<ResponseMessage<String>> setRolePrivilegeBatch(@RequestBody List<RolePrivilege> privileges) {
        ResponseMessage<String> response = rolePrivilegeService.saveRolePrivilegeBatch(privileges);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-privilege/{roleId}")
    public ResponseEntity<ResponseMessage<List<java.util.Map<String, Object>>>> getPrivilegesByRole(@PathVariable int roleId) {
        ResponseMessage<List<java.util.Map<String, Object>>> response = rolePrivilegeService.fetchAllMenusWithPrivilege(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-privilege")
    public ResponseEntity<ResponseMessage<List<RolePrivilege>>> getPrivilegesByRoleId(@RequestParam int roleId) {
        ResponseMessage<List<RolePrivilege>> response = rolePrivilegeService.getPrivilegesByRoleId(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<ResponseMessage<String>> deletePrivilegesByRole(@PathVariable int roleId) {
        ResponseMessage<String> response = rolePrivilegeService.deletePrivilegesByRoleId(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}