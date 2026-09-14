package com.bhahi.hrmodule.controller.document;

import com.bhahi.hrmodule.model.document.DocumentMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.document.DocumentMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document")
public class DocumentMasterController {

    private final DocumentMasterService documentMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<DocumentMaster>> save(@RequestBody DocumentMaster document) {
        ResponseMessage<DocumentMaster> response = documentMasterService.save(document);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ResponseMessage<DocumentMaster>> findById(@PathVariable int documentId) {
        ResponseMessage<DocumentMaster> response = documentMasterService.findById(documentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<DocumentMaster>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<DocumentMaster>> response = documentMasterService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<DocumentMaster>>> findAll() {
        ResponseMessage<List<DocumentMaster>> response = documentMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ResponseMessage<DocumentMaster>> verify(@PathVariable int documentId) {
        ResponseMessage<DocumentMaster> response = documentMasterService.verify(documentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{documentId}")
    public ResponseEntity<ResponseMessage<DocumentMaster>> update(@PathVariable int documentId,
                                                                   @RequestBody DocumentMaster updates) {
        ResponseMessage<DocumentMaster> response = documentMasterService.update(documentId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int documentId) {
        ResponseMessage<String> response = documentMasterService.delete(documentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
