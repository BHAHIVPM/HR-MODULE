package com.bhahi.hrmodule.controller.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.menu.DesktopSubMenuOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sub-menu-operation")
public class DesktopSubMenuOperationController {

    private final DesktopSubMenuOperationService subMenuOperation;

    // =============================
    // SUB MENU CRUD OPERATIONS
    // =============================

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage<String>> addMenu(@RequestBody DesktopMenuSubGroupMasterOperation subMenuMaster) {
        ResponseMessage<String> response = subMenuOperation.addSubMenu(subMenuMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getAllSubMenu")
    public ResponseEntity<ResponseMessage<List<DesktopMenuSubGroupMasterOperation>>> getAllSubMenu() {
        ResponseMessage<List<DesktopMenuSubGroupMasterOperation>> response = subMenuOperation.getAllSubMenu();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<DesktopMenuSubGroupMasterOperation>> getMenuById(@PathVariable("id") int subMenuId) {
        ResponseMessage<DesktopMenuSubGroupMasterOperation> response = subMenuOperation.getById(subMenuId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}