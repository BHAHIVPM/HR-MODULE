package com.bhahi.hrmodule.controller.culture;

import com.bhahi.hrmodule.model.culture.CompanyAnnouncement;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.culture.CompanyAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/culture/announcements")
public class CompanyAnnouncementController {

    private final CompanyAnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<ResponseMessage<CompanyAnnouncement>> broadcast(@RequestBody CompanyAnnouncement announcement) {
        ResponseMessage<CompanyAnnouncement> response = announcementService.broadcast(announcement);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<CompanyAnnouncement>>> findAll() {
        ResponseMessage<List<CompanyAnnouncement>> response = announcementService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
