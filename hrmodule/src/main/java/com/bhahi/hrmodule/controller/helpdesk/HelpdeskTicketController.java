package com.bhahi.hrmodule.controller.helpdesk;

import com.bhahi.hrmodule.model.helpdesk.HelpdeskTicket;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.helpdesk.HelpdeskTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/helpdesk/tickets")
public class HelpdeskTicketController {

    private final HelpdeskTicketService ticketService;

    @PostMapping
    public ResponseEntity<ResponseMessage<HelpdeskTicket>> createTicket(@RequestBody HelpdeskTicket ticket) {
        ResponseMessage<HelpdeskTicket> response = ticketService.createTicket(ticket);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ResponseMessage<HelpdeskTicket>> findById(@PathVariable int ticketId) {
        ResponseMessage<HelpdeskTicket> response = ticketService.findById(ticketId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseMessage<List<HelpdeskTicket>>> findByEmployeeId(@PathVariable int employeeId) {
        ResponseMessage<List<HelpdeskTicket>> response = ticketService.findByEmployeeId(employeeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<ResponseMessage<HelpdeskTicket>> updateStatus(
            @PathVariable int ticketId,
            @RequestParam HelpdeskTicket.TicketStatus status) {
        ResponseMessage<HelpdeskTicket> response = ticketService.updateStatus(ticketId, status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
