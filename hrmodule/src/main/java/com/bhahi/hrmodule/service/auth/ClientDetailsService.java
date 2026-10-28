package com.bhahi.hrmodule.service.auth;

import com.bhahi.hrmodule.Utils.AESUtils;
import com.bhahi.hrmodule.model.auth.ClientDetails;
import com.bhahi.hrmodule.model.auth.ClientDomain;
import com.bhahi.hrmodule.model.auth.ClientMaster;
import com.bhahi.hrmodule.repository.auth.ClientDomainRepository;
import com.bhahi.hrmodule.repository.auth.ClientMasterRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientDetailsService {

    private final ClientMasterRepository clientMasterRepository;
    private final ClientDomainRepository clientDomainRepository;

    @Transactional
    public ResponseMessage<ClientDetails> save(ClientDetails details) {
        ResponseMessage<ClientDetails> response = new ResponseMessage<>();
        try {
            if (details.getClientId() == null) {
                response.setHeader("Validation Error");
                response.setMessage("Client ID is required.");
                response.setStatusCode(400);
                return response;
            }

            if (clientMasterRepository.existsById(details.getClientId())) {
                response.setHeader("Conflict");
                response.setMessage("Client with ID " + details.getClientId() + " already exists.");
                response.setStatusCode(409);
                return response;
            }

            // Save ClientMaster
            ClientMaster master = new ClientMaster();
            master.setClientId(details.getClientId());
            master.setLedgerAccountId("LEDGER_" + details.getClientId());
            master.setOrganizationName(details.getOrganizationName() != null ? details.getOrganizationName() : "Org_" + details.getClientId());
            master.setTradeName(details.getTradeName() != null ? details.getTradeName() : "Trade_" + details.getClientId());
            master.setClientCode(details.getClientCode() != null ? details.getClientCode() : "C_" + details.getClientId());
            master.setIndustry(1);
            master.setRegion(1);
            master.setRegistrationNo("REG_" + details.getClientId());
            master.setDateOfIncorporation(LocalDate.now());
            master.setBusinessEntityType(details.getBusinessEntityType() != null ? details.getBusinessEntityType() : "Private Limited");
            master.setContactPersonName(details.getContactPersonName() != null ? details.getContactPersonName() : "Admin");
            master.setPrimaryContactNumber(details.getMobileNumber() != null ? details.getMobileNumber() : "9999999999");
            master.setEmailAddress(details.getEmailAddress() != null ? details.getEmailAddress() : "client@domain.com");
            master.setRoaAddressLine1("Address Line 1");
            master.setRoaCity("City");
            master.setRoaState(1);
            master.setRoaPinCode("123456");
            clientMasterRepository.save(master);

            // Save ClientDomain with encrypted password
            ClientDomain domain = new ClientDomain();
            domain.setClientId(details.getClientId());
            domain.setDomainUrl(details.getDomainUrl() != null ? details.getDomainUrl() : "jdbc:mysql://localhost:3306/db_" + details.getClientId());
            domain.setUserName(details.getDatabaseUserName() != null ? details.getDatabaseUserName() : "root");
            
            String encryptedPassword;
            try {
                String rawPassword = details.getDatabasePassword() != null ? details.getDatabasePassword() : "123456";
                encryptedPassword = AESUtils.encrypt(rawPassword);
            } catch (Exception e) {
                encryptedPassword = details.getDatabasePassword(); // fallback
            }
            domain.setDatabasePassword(encryptedPassword);
            domain.setDomainHostName(details.getDomainHostName());
            domain.setDomainHostIp(details.getDomainHostIp());
            clientDomainRepository.save(domain);

            details.setEntryDate(master.getEntryDate());
            response.setResponseOutput(details);
            response.setHeader("Success");
            response.setMessage("Client details saved successfully.");
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setHeader("Error");
            response.setMessage("Failed to save client details: " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<ClientDetails>> findAll() {
        ResponseMessage<List<ClientDetails>> response = new ResponseMessage<>();
        try {
            List<ClientMaster> masters = clientMasterRepository.findAll();
            List<ClientDetails> list = new ArrayList<>();
            for (ClientMaster m : masters) {
                ClientDetails d = new ClientDetails();
                d.setClientId(m.getClientId());
                d.setOrganizationName(m.getOrganizationName());
                d.setTradeName(m.getTradeName());
                d.setClientCode(m.getClientCode());
                d.setBusinessEntityType(m.getBusinessEntityType());
                d.setContactPersonName(m.getContactPersonName());
                d.setMobileNumber(m.getPrimaryContactNumber());
                d.setEmailAddress(m.getEmailAddress());
                d.setEntryDate(m.getEntryDate());

                Optional<ClientDomain> domainOpt = clientDomainRepository.findByClientId(m.getClientId());
                if (domainOpt.isPresent()) {
                    ClientDomain dom = domainOpt.get();
                    d.setDomainUrl(dom.getDomainUrl());
                    d.setDatabaseUserName(dom.getUserName());
                    try {
                        d.setDatabasePassword(AESUtils.decrypt(dom.getDatabasePassword()));
                    } catch (Exception ex) {
                        d.setDatabasePassword(dom.getDatabasePassword());
                    }
                    d.setDomainHostName(dom.getDomainHostName());
                    d.setDomainHostIp(dom.getDomainHostIp());
                }
                list.add(d);
            }
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Fetched all client details successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Error");
            response.setMessage("Failed to fetch client details: " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<ClientDetails> findByClientId(Integer clientId) {
        ResponseMessage<ClientDetails> response = new ResponseMessage<>();
        try {
            Optional<ClientMaster> masterOpt = clientMasterRepository.findByClientId(clientId);
            if (masterOpt.isEmpty()) {
                response.setHeader("Not Found");
                response.setMessage("Client not found with ID: " + clientId);
                response.setStatusCode(404);
                return response;
            }
            ClientMaster m = masterOpt.get();
            ClientDetails d = new ClientDetails();
            d.setClientId(m.getClientId());
            d.setOrganizationName(m.getOrganizationName());
            d.setTradeName(m.getTradeName());
            d.setClientCode(m.getClientCode());
            d.setBusinessEntityType(m.getBusinessEntityType());
            d.setContactPersonName(m.getContactPersonName());
            d.setMobileNumber(m.getPrimaryContactNumber());
            d.setEmailAddress(m.getEmailAddress());
            d.setEntryDate(m.getEntryDate());

            Optional<ClientDomain> domainOpt = clientDomainRepository.findByClientId(m.getClientId());
            if (domainOpt.isPresent()) {
                ClientDomain dom = domainOpt.get();
                d.setDomainUrl(dom.getDomainUrl());
                d.setDatabaseUserName(dom.getUserName());
                try {
                    d.setDatabasePassword(AESUtils.decrypt(dom.getDatabasePassword()));
                } catch (Exception ex) {
                    d.setDatabasePassword(dom.getDatabasePassword());
                }
                d.setDomainHostName(dom.getDomainHostName());
                d.setDomainHostIp(dom.getDomainHostIp());
            }

            response.setResponseOutput(d);
            response.setHeader("Success");
            response.setMessage("Client details fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Error");
            response.setMessage("Failed to fetch client details: " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
