package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.PayrollTransaction;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.PayrollTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payroll")
public class PayrollTransactionController {

    private final PayrollTransactionService payrollTransactionService;

    // Computes the payslip server-side from salary_master + attendance + leave_application.
    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<ResponseMessage<PayrollTransaction>> generate(@PathVariable int employeeId,
                                                                         @RequestParam int month,
                                                                         @RequestParam int year) {
        ResponseMessage<PayrollTransaction> response = payrollTransactionService.generate(employeeId, month, year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{payrollId}")
    public ResponseEntity<ResponseMessage<PayrollTransaction>> findById(@PathVariable int payrollId) {
        ResponseMessage<PayrollTransaction> response = payrollTransactionService.findById(payrollId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<PayrollTransaction>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<PayrollTransaction>> response = payrollTransactionService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-month")
    public ResponseEntity<ResponseMessage<List<PayrollTransaction>>> findByMonth(@RequestParam int month,
                                                                                  @RequestParam int year) {
        ResponseMessage<List<PayrollTransaction>> response = payrollTransactionService.findByMonth(month, year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<PayrollTransaction>>> findAll() {
        ResponseMessage<List<PayrollTransaction>> response = payrollTransactionService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{payrollId}/mark-paid")
    public ResponseEntity<ResponseMessage<PayrollTransaction>> markPaid(@PathVariable int payrollId) {
        ResponseMessage<PayrollTransaction> response = payrollTransactionService.markPaid(payrollId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{payrollId}/cancel")
    public ResponseEntity<ResponseMessage<PayrollTransaction>> cancel(@PathVariable int payrollId) {
        ResponseMessage<PayrollTransaction> response = payrollTransactionService.cancel(payrollId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{payrollId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int payrollId) {
        ResponseMessage<String> response = payrollTransactionService.delete(payrollId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
