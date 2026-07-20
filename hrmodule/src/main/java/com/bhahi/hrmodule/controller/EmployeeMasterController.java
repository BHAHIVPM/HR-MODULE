package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.EmployeeMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.EmployeeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Not under /open or /guest, so SecurityConfig's anyRequest().authOnly() applies here -
// only a full Access_token (AUTH type) can reach these, a Guest_Token cannot.
@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeMasterController {

    private final EmployeeMasterService employeeMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<EmployeeMaster>> save(@RequestBody EmployeeMaster employee) {
        ResponseMessage<EmployeeMaster> response = employeeMasterService.save(employee);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ResponseMessage<EmployeeMaster>> findById(@PathVariable int employeeId) {
        ResponseMessage<EmployeeMaster> response = employeeMasterService.findById(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<EmployeeMaster>>> findAllActive() {
        ResponseMessage<List<EmployeeMaster>> response = employeeMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
