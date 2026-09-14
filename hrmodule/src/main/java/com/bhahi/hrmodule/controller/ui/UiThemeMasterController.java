package com.bhahi.hrmodule.controller.ui;

import com.bhahi.hrmodule.model.ui.UiThemeMaster;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.ui.UiThemeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ui-theme")
public class UiThemeMasterController {

    private final UiThemeMasterService uiThemeMasterService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<UiThemeMaster>> save(@RequestBody UiThemeMaster theme) {
        ResponseMessage<UiThemeMaster> response = uiThemeMasterService.save(theme);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{themeDocumentId}")
    public ResponseEntity<ResponseMessage<UiThemeMaster>> findById(@PathVariable int themeDocumentId) {
        ResponseMessage<UiThemeMaster> response = uiThemeMasterService.findById(themeDocumentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<UiThemeMaster>>> findAll() {
        ResponseMessage<List<UiThemeMaster>> response = uiThemeMasterService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{themeDocumentId}")
    public ResponseEntity<ResponseMessage<UiThemeMaster>> update(@PathVariable int themeDocumentId,
                                                                @RequestBody UiThemeMaster updates) {
        ResponseMessage<UiThemeMaster> response = uiThemeMasterService.update(themeDocumentId, updates);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{themeDocumentId}")
    public ResponseEntity<ResponseMessage<String>> delete(@PathVariable int themeDocumentId) {
        ResponseMessage<String> response = uiThemeMasterService.delete(themeDocumentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}