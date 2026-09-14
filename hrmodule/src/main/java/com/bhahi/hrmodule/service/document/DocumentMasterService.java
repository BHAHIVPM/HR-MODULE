package com.bhahi.hrmodule.service.document;

import com.bhahi.hrmodule.model.document.DocumentMaster;
import com.bhahi.hrmodule.repository.document.DocumentMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentMasterService {

    private final DocumentMasterRepo documentMasterRepo;

    public ResponseMessage<DocumentMaster> save(DocumentMaster document) {
        ResponseMessage<DocumentMaster> response = new ResponseMessage<>();
        try {
            if (document.getStatus() == null) {
                document.setStatus(DocumentMaster.DocumentStatus.ACTIVE);
            }
            if (document.getUploadedOn() == null) {
                document.setUploadedOn(LocalDateTime.now());
            }
            DocumentMaster saved = documentMasterRepo.save(document);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Document saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the document. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DocumentMaster> findById(int documentId) {
        ResponseMessage<DocumentMaster> response = new ResponseMessage<>();
        try {
            Optional<DocumentMaster> document = documentMasterRepo.findById(documentId);
            if (document.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No document found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(document.get());
            response.setHeader("Success");
            response.setMessage("Document fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the document. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DocumentMaster>> findByEmployee(int employeeId) {
        ResponseMessage<List<DocumentMaster>> response = new ResponseMessage<>();
        try {
            List<DocumentMaster> documents = documentMasterRepo.findByEmployeeId(employeeId);
            response.setResponseOutput(documents);
            response.setHeader("Success");
            response.setMessage("Documents fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch documents. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DocumentMaster>> findAll() {
        ResponseMessage<List<DocumentMaster>> response = new ResponseMessage<>();
        try {
            List<DocumentMaster> documents = documentMasterRepo.findAll();
            response.setResponseOutput(documents);
            response.setHeader("Success");
            response.setMessage("Documents fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch documents. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DocumentMaster> verify(int documentId) {
        ResponseMessage<DocumentMaster> response = new ResponseMessage<>();
        try {
            Optional<DocumentMaster> existingOpt = documentMasterRepo.findById(documentId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No document found for this id.");
                response.setStatusCode(404);
                return response;
            }
            DocumentMaster existing = existingOpt.get();
            existing.setVerified(true);
            DocumentMaster saved = documentMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Document marked as verified.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not verify the document. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DocumentMaster> update(int documentId, DocumentMaster updates) {
        ResponseMessage<DocumentMaster> response = new ResponseMessage<>();
        try {
            Optional<DocumentMaster> existingOpt = documentMasterRepo.findById(documentId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No document found for this id.");
                response.setStatusCode(404);
                return response;
            }

            DocumentMaster existing = existingOpt.get();
            existing.setDocumentType(updates.getDocumentType());
            existing.setDocumentName(updates.getDocumentName());
            existing.setDocumentNumber(updates.getDocumentNumber());
            existing.setFilePath(updates.getFilePath());
            existing.setIssuedDate(updates.getIssuedDate());
            existing.setExpiryDate(updates.getExpiryDate());
            existing.setStatus(updates.getStatus());

            DocumentMaster saved = documentMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Document updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the document. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int documentId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!documentMasterRepo.existsById(documentId)) {
                response.setHeader("Not found");
                response.setMessage("No document found for this id.");
                response.setStatusCode(404);
                return response;
            }
            documentMasterRepo.deleteById(documentId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Document deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the document. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
