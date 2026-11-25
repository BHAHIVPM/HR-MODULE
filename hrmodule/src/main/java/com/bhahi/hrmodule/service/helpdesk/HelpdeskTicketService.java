package com.bhahi.hrmodule.service.helpdesk;

import com.bhahi.hr.dto.OTP.KafkaEventType;
import com.bhahi.hr.dto.helpdesk.HelpdeskTicketEvent;
import com.bhahi.hrmodule.model.helpdesk.HelpdeskTicket;
import com.bhahi.hrmodule.repository.helpdesk.HelpdeskTicketRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import com.bhahi.hrmodule.service.auth.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpdeskTicketService {

    private final HelpdeskTicketRepository ticketRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public ResponseMessage<HelpdeskTicket> createTicket(HelpdeskTicket ticket) {
        ResponseMessage<HelpdeskTicket> response = new ResponseMessage<>();
        try {
            if (ticket.getStatus() == null) {
                ticket.setStatus(HelpdeskTicket.TicketStatus.OPEN);
            }
            if (ticket.getPriority() == null) {
                ticket.setPriority(HelpdeskTicket.TicketPriority.MEDIUM);
            }
            if (ticket.getCreatedAt() == null) {
                ticket.setCreatedAt(LocalDate.now());
            }
            HelpdeskTicket saved = ticketRepository.save(ticket);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Helpdesk ticket created successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Creation failed");
            response.setMessage("Could not create helpdesk ticket. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<HelpdeskTicket> findById(int ticketId) {
        ResponseMessage<HelpdeskTicket> response = new ResponseMessage<>();
        try {
            Optional<HelpdeskTicket> opt = ticketRepository.findById(ticketId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Helpdesk ticket not found.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(opt.get());
            response.setHeader("Success");
            response.setMessage("Helpdesk ticket fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch helpdesk ticket. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<HelpdeskTicket>> findByEmployeeId(int employeeId) {
        ResponseMessage<List<HelpdeskTicket>> response = new ResponseMessage<>();
        try {
            List<HelpdeskTicket> list = ticketRepository.findByEmployeeId(employeeId);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Helpdesk tickets fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch helpdesk tickets. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<HelpdeskTicket> updateStatus(int ticketId, HelpdeskTicket.TicketStatus status) {
        ResponseMessage<HelpdeskTicket> response = new ResponseMessage<>();
        try {
            Optional<HelpdeskTicket> opt = ticketRepository.findById(ticketId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Helpdesk ticket not found.");
                response.setStatusCode(404);
                return response;
            }
            HelpdeskTicket existing = opt.get();
            existing.setStatus(status);
            HelpdeskTicket saved = ticketRepository.save(existing);

            publishEvent(saved);

            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Helpdesk ticket status updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update ticket status. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    private void publishEvent(HelpdeskTicket ticket) {
        try {
            HelpdeskTicketEvent event = new HelpdeskTicketEvent(
                    (long) ticket.getTicketId(),
                    ticket.getEmployeeId(),
                    ticket.getCreatorEmail(),
                    ticket.getCategory().name(),
                    ticket.getSubject(),
                    ticket.getPriority().name(),
                    ticket.getStatus().name()
            );

            kafkaEventPublisher.publish(
                    KafkaEventType.HELPDESK_TICKET,
                    ticket.getCreatorEmail() != null ? ticket.getCreatorEmail() : "support",
                    event
            );
        } catch (Exception e) {
            log.error("Failed to publish helpdesk ticket event for ticketId={}", ticket.getTicketId(), e);
        }
    }
}
