package com.bhahi.hrmodule.controller.employee;

import com.bhahi.hrmodule.model.employee.EmployeeMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.employee.EmployeeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<EmployeeMaster>>> findAll() {
        ResponseMessage<List<EmployeeMaster>> response = employeeMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<ResponseMessage<EmployeeMaster>> update(@PathVariable int employeeId,
                                                                  @RequestBody EmployeeMaster updates) {
        ResponseMessage<EmployeeMaster> response = employeeMasterService.update(employeeId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int employeeId) {
        ResponseMessage<String> response = employeeMasterService.delete(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
