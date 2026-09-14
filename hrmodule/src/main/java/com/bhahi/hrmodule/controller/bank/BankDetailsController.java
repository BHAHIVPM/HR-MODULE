package com.bhahi.hrmodule.controller.bank;

import com.bhahi.hrmodule.model.bank.BankDetails;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.bank.BankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-details")
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<BankDetails>> save(@RequestBody BankDetails bankDetails) {
        ResponseMessage<BankDetails> response = bankDetailsService.save(bankDetails);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{bankDetailId}")
    public ResponseEntity<ResponseMessage<BankDetails>> findById(@PathVariable int bankDetailId) {
        ResponseMessage<BankDetails> response = bankDetailsService.findById(bankDetailId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<BankDetails>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<BankDetails>> response = bankDetailsService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<BankDetails>>> findAll() {
        ResponseMessage<List<BankDetails>> response = bankDetailsService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{bankDetailId}")
    public ResponseEntity<ResponseMessage<BankDetails>> update(@PathVariable int bankDetailId,
                                                                @RequestBody BankDetails updates) {
        ResponseMessage<BankDetails> response = bankDetailsService.update(bankDetailId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{bankDetailId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int bankDetailId) {
        ResponseMessage<String> response = bankDetailsService.delete(bankDetailId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
