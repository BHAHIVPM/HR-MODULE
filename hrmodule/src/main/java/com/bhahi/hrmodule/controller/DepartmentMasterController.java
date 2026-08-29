package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.DepartmentMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.DepartmentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Not under /open or /guest, so SecurityConfig's anyRequest().authOnly() applies here.
@RestController
@RequiredArgsConstructor
@RequestMapping("/department")
public class DepartmentMasterController {

    private final DepartmentMasterService departmentMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<DepartmentMaster>> save(@RequestBody DepartmentMaster department) {
        ResponseMessage<DepartmentMaster> response = departmentMasterService.save(department);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ResponseMessage<DepartmentMaster>> findById(@PathVariable int departmentId) {
        ResponseMessage<DepartmentMaster> response = departmentMasterService.findById(departmentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<DepartmentMaster>>> findAllActive() {
        ResponseMessage<List<DepartmentMaster>> response = departmentMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<DepartmentMaster>>> findAll() {
        ResponseMessage<List<DepartmentMaster>> response = departmentMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{departmentId}")
    public ResponseEntity<ResponseMessage<DepartmentMaster>> update(@PathVariable int departmentId,
                                                                     @RequestBody DepartmentMaster updates) {
        ResponseMessage<DepartmentMaster> response = departmentMasterService.update(departmentId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int departmentId) {
        ResponseMessage<String> response = departmentMasterService.delete(departmentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
