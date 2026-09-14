package com.bhahi.hrmodule.controller.salary;

import com.bhahi.hrmodule.model.salary.SalaryMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.salary.SalaryMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/salary")
public class SalaryMasterController {

    private final SalaryMasterService salaryMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<SalaryMaster>> save(@RequestBody SalaryMaster salary) {
        ResponseMessage<SalaryMaster> response = salaryMasterService.save(salary);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{salaryId}")
    public ResponseEntity<ResponseMessage<SalaryMaster>> findById(@PathVariable int salaryId) {
        ResponseMessage<SalaryMaster> response = salaryMasterService.findById(salaryId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<SalaryMaster>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<SalaryMaster>> response = salaryMasterService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<SalaryMaster>>> findAll() {
        ResponseMessage<List<SalaryMaster>> response = salaryMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{salaryId}")
    public ResponseEntity<ResponseMessage<SalaryMaster>> update(@PathVariable int salaryId,
                                                                 @RequestBody SalaryMaster updates) {
        ResponseMessage<SalaryMaster> response = salaryMasterService.update(salaryId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{salaryId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int salaryId) {
        ResponseMessage<String> response = salaryMasterService.delete(salaryId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
