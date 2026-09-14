package com.bhahi.hrmodule.controller.menu;

import com.bhahi.hrmodule.dto.menu.DesktopMenuNameMasterOperationDto;
import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.menu.DesktopMenuOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu-operation")
public class DesktopMenuOperationController {

    private final DesktopMenuOperationService menuService;

    // =============================
    // MENU CRUD OPERATIONS
    // =============================

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage<String>> addMenu(@RequestBody DesktopMenuNameMasterOperation menuMaster) {
        ResponseMessage<String> response = menuService.addMenu(menuMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<DesktopMenuNameMasterOperationDto>>> getAllMenus() {
        ResponseMessage<List<DesktopMenuNameMasterOperationDto>> response = menuService.getAllMenus();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<DesktopMenuNameMasterOperationDto>> getMenuById(@PathVariable("id") int id) {
        ResponseMessage<DesktopMenuNameMasterOperationDto> response = menuService.getMenuById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage<String>> updateMenu(@PathVariable("id") int id,
                                                              @RequestBody DesktopMenuNameMasterOperation menuMaster) {
        ResponseMessage<String> response = menuService.updateMenu(id, menuMaster);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage<String>> deleteMenu(@PathVariable("id") int id) {
        ResponseMessage<String> response = menuService.deleteMenu(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // =============================
    // HIERARCHY ORDER OPERATIONS
    // =============================

    @PutMapping("/reorder-hierarchy/{dbMainGroupId}")
    public ResponseEntity<ResponseMessage<String>> reorderMenuHierarchy(
            @PathVariable("dbMainGroupId") int dbMainGroupId,
            @RequestBody List<DesktopMenuNameMasterOperationDto> orderDtos) {
        ResponseMessage<String> response = menuService.updateMenuHierarchyOrder(dbMainGroupId, orderDtos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}