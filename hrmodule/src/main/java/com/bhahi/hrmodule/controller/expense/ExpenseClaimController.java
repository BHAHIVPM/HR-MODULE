package com.bhahi.hrmodule.controller.expense;

import com.bhahi.hrmodule.model.expense.ExpenseClaim;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.expense.ExpenseClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense/claims")
public class ExpenseClaimController {

    private final ExpenseClaimService expenseService;

    @PostMapping("/submit")
    public ResponseEntity<ResponseMessage<ExpenseClaim>> save(@RequestBody ExpenseClaim claim) {
        ResponseMessage<ExpenseClaim> response = expenseService.save(claim);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ResponseMessage<ExpenseClaim>> findById(@PathVariable int claimId) {
        ResponseMessage<ExpenseClaim> response = expenseService.findById(claimId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<ExpenseClaim>>> findByEmployeeId(@PathVariable int employeeId) {
        ResponseMessage<List<ExpenseClaim>> response = expenseService.findByEmployeeId(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{claimId}/status")
    public ResponseEntity<ResponseMessage<ExpenseClaim>> updateStatus(
            @PathVariable int claimId,
            @RequestParam ExpenseClaim.ClaimStatus status) {
        ResponseMessage<ExpenseClaim> response = expenseService.updateStatus(claimId, status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
