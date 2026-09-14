package com.bhahi.hrmodule.controller.holiday;

import com.bhahi.hrmodule.model.holiday.HolidayMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.holiday.HolidayMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holiday")
public class HolidayMasterController {

    private final HolidayMasterService holidayMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<HolidayMaster>> save(@RequestBody HolidayMaster holiday) {
        ResponseMessage<HolidayMaster> response = holidayMasterService.save(holiday);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{holidayId}")
    public ResponseEntity<ResponseMessage<HolidayMaster>> findById(@PathVariable int holidayId) {
        ResponseMessage<HolidayMaster> response = holidayMasterService.findById(holidayId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ResponseMessage<List<HolidayMaster>>> findAllActive() {
        ResponseMessage<List<HolidayMaster>> response = holidayMasterService.findAllActive();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<ResponseMessage<List<HolidayMaster>>> findByYear(@PathVariable int year) {
        ResponseMessage<List<HolidayMaster>> response = holidayMasterService.findByYear(year);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<HolidayMaster>>> findAll() {
        ResponseMessage<List<HolidayMaster>> response = holidayMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{holidayId}")
    public ResponseEntity<ResponseMessage<HolidayMaster>> update(@PathVariable int holidayId,
                                                                  @RequestBody HolidayMaster updates) {
        ResponseMessage<HolidayMaster> response = holidayMasterService.update(holidayId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{holidayId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int holidayId) {
        ResponseMessage<String> response = holidayMasterService.delete(holidayId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
