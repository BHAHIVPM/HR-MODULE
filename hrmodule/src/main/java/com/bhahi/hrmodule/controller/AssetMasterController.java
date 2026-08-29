package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.AssetMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.AssetMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/asset")
public class AssetMasterController {

    private final AssetMasterService assetMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<AssetMaster>> save(@RequestBody AssetMaster asset) {
        ResponseMessage<AssetMaster> response = assetMasterService.save(asset);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<ResponseMessage<AssetMaster>> findById(@PathVariable int assetId) {
        ResponseMessage<AssetMaster> response = assetMasterService.findById(assetId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<AssetMaster>>> findByEmployee(@PathVariable int employeeId) {
        ResponseMessage<List<AssetMaster>> response = assetMasterService.findByEmployee(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available")
    public ResponseEntity<ResponseMessage<List<AssetMaster>>> findAvailable() {
        ResponseMessage<List<AssetMaster>> response = assetMasterService.findAvailable();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<AssetMaster>>> findAll() {
        ResponseMessage<List<AssetMaster>> response = assetMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{assetId}/issue/{employeeId}")
    public ResponseEntity<ResponseMessage<AssetMaster>> issueTo(@PathVariable int assetId, @PathVariable int employeeId) {
        ResponseMessage<AssetMaster> response = assetMasterService.issueTo(assetId, employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{assetId}/return")
    public ResponseEntity<ResponseMessage<AssetMaster>> returnAsset(@PathVariable int assetId,
                                                                     @RequestParam AssetMaster.AssetCondition condition,
                                                                     @RequestParam(required = false) String remarks) {
        ResponseMessage<AssetMaster> response = assetMasterService.returnAsset(assetId, condition, remarks);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{assetId}")
    public ResponseEntity<ResponseMessage<AssetMaster>> update(@PathVariable int assetId,
                                                                @RequestBody AssetMaster updates) {
        ResponseMessage<AssetMaster> response = assetMasterService.update(assetId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{assetId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int assetId) {
        ResponseMessage<String> response = assetMasterService.delete(assetId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
