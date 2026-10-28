package com.bhahi.hrmodule.controller.auth;

import com.bhahi.hrmodule.model.auth.ClientDetails;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.ClientDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientData")
public class ClientDetailsController {

    private final ClientDetailsService clientDetailsService;

    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<ClientDetails>> save(@RequestBody ClientDetails details) {
        ResponseMessage<ClientDetails> response = clientDetailsService.save(details);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<ClientDetails>>> findAll() {
        ResponseMessage<List<ClientDetails>> response = clientDetailsService.findAll();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ResponseMessage<ClientDetails>> findByClientId(@PathVariable Integer clientId) {
        ResponseMessage<ClientDetails> response = clientDetailsService.findByClientId(clientId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
