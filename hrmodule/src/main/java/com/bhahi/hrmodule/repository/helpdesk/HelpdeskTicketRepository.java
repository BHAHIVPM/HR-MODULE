package com.bhahi.hrmodule.repository.helpdesk;

import com.bhahi.hrmodule.model.helpdesk.HelpdeskTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpdeskTicketRepository extends JpaRepository<HelpdeskTicket, Integer> {
    List<HelpdeskTicket> findByEmployeeId(int employeeId);
    List<HelpdeskTicket> findByStatus(HelpdeskTicket.TicketStatus status);
}
