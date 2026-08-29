package com.bhahi.hrmodule.controller;

import com.bhahi.hrmodule.model.ShiftMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.ShiftMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shift")
public class ShiftMasterController {

    private final ShiftMasterService shiftMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<ShiftMaster>> save(@RequestBody ShiftMaster shift) {
        ResponseMessage<ShiftMaster> response = shiftMasterService.save(shift);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{shiftId}")
    public ResponseEntity<ResponseMessage<ShiftMaster>> findById(@PathVariable int shiftId) {
        ResponseMessage<ShiftMaster> response = shiftMasterService.findById(shiftId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<ShiftMaster>>> findAllActive() {
        ResponseMessage<List<ShiftMaster>> response = shiftMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<ShiftMaster>>> findAll() {
        ResponseMessage<List<ShiftMaster>> response = shiftMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{shiftId}")
    public ResponseEntity<ResponseMessage<ShiftMaster>> update(@PathVariable int shiftId,
                                                                @RequestBody ShiftMaster updates) {
        ResponseMessage<ShiftMaster> response = shiftMasterService.update(shiftId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{shiftId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int shiftId) {
        ResponseMessage<String> response = shiftMasterService.delete(shiftId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
