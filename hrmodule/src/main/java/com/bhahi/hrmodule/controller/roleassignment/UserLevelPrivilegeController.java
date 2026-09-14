package com.bhahi.hrmodule.controller.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.UserLevelPrivilege;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.roleassignment.UserLevelPrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-level-privilege")
@RequiredArgsConstructor
public class UserLevelPrivilegeController {

    private final UserLevelPrivilegeService userLevelPrivilegeService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseMessage<List<UserLevelPrivilege>>> getAllUserLevelPrivileges() {
        ResponseMessage<List<UserLevelPrivilege>> response = userLevelPrivilegeService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getById/{loginId}")
    public ResponseEntity<ResponseMessage<List<UserLevelPrivilege>>> getPrivilegesByLoginId(@PathVariable String loginId) {
        ResponseMessage<List<UserLevelPrivilege>> response = userLevelPrivilegeService.findByLoginId(loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<ResponseMessage<String>> editUserLevelPrivilege(@RequestBody List<UserLevelPrivilege> privileges) {
        ResponseMessage<String> response = userLevelPrivilegeService.updateUserPrivileges(privileges);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/fetchById/{loginId}")
    public ResponseEntity<ResponseMessage<List<Map<String, Object>>>> fetchUserPrivilegeById(@PathVariable String loginId) {
        ResponseMessage<List<Map<String, Object>>> response = userLevelPrivilegeService.fetchUserPrivilege(loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/fetchMultiple")
    public ResponseEntity<ResponseMessage<List<Map<String, Object>>>> fetchMultipleRolePrivileges(
            @RequestParam List<Integer> roleIds, @RequestParam String loginId) {
        ResponseMessage<List<Map<String, Object>>> response = userLevelPrivilegeService.fetchMultipleRolePrivileges(roleIds, loginId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}