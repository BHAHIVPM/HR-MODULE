package com.bhahi.hrmodule.controller.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuMainGroupOperationDto;
import com.bhahi.hrmodule.dto.menu.MainGroupOperationOrderDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.menu.DesktopMenuMainGroupOperationService;
import com.bhahi.hrmodule.service.menu.MenuHierarchyService;
import com.bhahi.hrmodule.dto.menu.MenuResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main-group-operation")
public class DesktopMenuMainGroupOperationController {

    private final DesktopMenuMainGroupOperationService mainGroupService;
    private final MenuHierarchyService menuHierarchyService;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // =============================
    // MAIN GROUP CRUD OPERATIONS
    // =============================

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage<String>> addMainGroup(@RequestBody DesktopMenuMainGroupMasterOperation mainGroup) {
        ResponseMessage<String> response = mainGroupService.addMainGroup(mainGroup);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<DesktopMenuMainGroupOperationDto>>> getAllMainGroups() {
        ResponseMessage<List<DesktopMenuMainGroupOperationDto>> response = mainGroupService.getAllMainGroups();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<DesktopMenuMainGroupOperationDto>> getMainGroupById(@PathVariable("id") int id) {
        ResponseMessage<DesktopMenuMainGroupOperationDto> response = mainGroupService.getMainGroupById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage<String>> updateMainGroup(@PathVariable("id") int id,
                                                                    @RequestBody DesktopMenuMainGroupMasterOperation mainGroup) {
        ResponseMessage<String> response = mainGroupService.updateMainGroup(id, mainGroup);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage<String>> deleteMainGroup(@PathVariable("id") int id) {
        ResponseMessage<String> response = mainGroupService.deleteMainGroup(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // =============================
    // ORDER OPERATIONS
    // =============================

    @PutMapping("/order")
    public ResponseEntity<ResponseMessage<String>> updateMainGroupOrder(@RequestBody List<MainGroupOperationOrderDto> orderDtos) {
        ResponseMessage<String> response = mainGroupService.updateMainGroupOrder(orderDtos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // =============================
    // FULL MENU HIERARCHY
    // =============================

    @GetMapping("/full")
    public ResponseEntity<ResponseMessage<List<MenuResponseDto>>> getFullMenu() {
        ResponseMessage<List<MenuResponseDto>> response = menuHierarchyService.getMenuHierarchy();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
