package com.bhahi.hrmodule.controller.benefits;

import com.bhahi.hrmodule.model.benefits.EmployeeBenefit;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.benefits.EmployeeBenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/benefits")
public class EmployeeBenefitController {

    private final EmployeeBenefitService benefitService;

    @PostMapping("/enroll")
    public ResponseEntity<ResponseMessage<EmployeeBenefit>> enroll(@RequestBody EmployeeBenefit benefit) {
        ResponseMessage<EmployeeBenefit> response = benefitService.enroll(benefit);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<EmployeeBenefit>>> findByEmployeeId(@PathVariable int employeeId) {
        ResponseMessage<List<EmployeeBenefit>> response = benefitService.findByEmployeeId(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
