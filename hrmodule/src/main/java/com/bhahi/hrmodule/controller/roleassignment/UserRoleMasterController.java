package com.bhahi.hrmodule.controller.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.UserRoleMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.roleassignment.UserRoleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-role")
@RequiredArgsConstructor
public class UserRoleMasterController {

    private final UserRoleMasterService userRoleMasterService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/create-role")
    public ResponseEntity<ResponseMessage<UserRoleMaster>> createRole(@RequestBody UserRoleMaster userRoleMaster) {
        ResponseMessage<UserRoleMaster> response = userRoleMasterService.save(userRoleMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-all-role")
    public ResponseEntity<ResponseMessage<List<UserRoleMaster>>> getAllRoles() {
        ResponseMessage<List<UserRoleMaster>> response = userRoleMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-all-role-category")
    public ResponseEntity<ResponseMessage<List<UserRoleMaster>>> getAllRolesByCategory() {
        ResponseMessage<List<UserRoleMaster>> response = userRoleMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-all-view")
    public ResponseEntity<ResponseMessage<List<UserRoleMaster>>> getAllRolesWithDate() {
        ResponseMessage<List<UserRoleMaster>> response = userRoleMasterService.findAllWithDate();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getRoleById/{roleId}")
    public ResponseEntity<ResponseMessage<UserRoleMaster>> getRoleById(@PathVariable int roleId) {
        ResponseMessage<UserRoleMaster> response = userRoleMasterService.findById(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/role-privilege-update")
    public ResponseEntity<ResponseMessage<UserRoleMaster>> updateRole(@PathVariable int roleId, @RequestBody UserRoleMaster userRoleMaster) {
        ResponseMessage<UserRoleMaster> response = userRoleMasterService.update(roleId, userRoleMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<ResponseMessage<UserRoleMaster>> updateRoleById(@PathVariable int roleId, @RequestBody UserRoleMaster userRoleMaster) {
        ResponseMessage<UserRoleMaster> response = userRoleMasterService.update(roleId, userRoleMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<ResponseMessage<String>> deleteRole(@PathVariable int roleId) {
        ResponseMessage<String> response = userRoleMasterService.delete(roleId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}