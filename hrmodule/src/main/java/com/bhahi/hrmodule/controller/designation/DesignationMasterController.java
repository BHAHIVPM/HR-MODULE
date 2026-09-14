package com.bhahi.hrmodule.controller.designation;

import com.bhahi.hrmodule.model.designation.DesignationMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.designation.DesignationMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/designation")
public class DesignationMasterController {

    private final DesignationMasterService designationMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<DesignationMaster>> save(@RequestBody DesignationMaster designation) {
        ResponseMessage<DesignationMaster> response = designationMasterService.save(designation);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{designationId}")
    public ResponseEntity<ResponseMessage<DesignationMaster>> findById(@PathVariable int designationId) {
        ResponseMessage<DesignationMaster> response = designationMasterService.findById(designationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<ResponseMessage<List<DesignationMaster>>> findByDepartment(@PathVariable int departmentId) {
        ResponseMessage<List<DesignationMaster>> response = designationMasterService.findByDepartment(departmentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<DesignationMaster>>> findAllActive() {
        ResponseMessage<List<DesignationMaster>> response = designationMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<DesignationMaster>>> findAll() {
        ResponseMessage<List<DesignationMaster>> response = designationMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{designationId}")
    public ResponseEntity<ResponseMessage<DesignationMaster>> update(@PathVariable int designationId,
                                                                      @RequestBody DesignationMaster updates) {
        ResponseMessage<DesignationMaster> response = designationMasterService.update(designationId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{designationId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int designationId) {
        ResponseMessage<String> response = designationMasterService.delete(designationId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
