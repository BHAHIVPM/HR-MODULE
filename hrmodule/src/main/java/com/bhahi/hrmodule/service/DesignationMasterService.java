package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.DesignationMaster;
import com.bhahi.hrmodule.repository.DesignationMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DesignationMasterService {

    private final DesignationMasterRepo designationMasterRepo;

    public ResponseMessage<DesignationMaster> save(DesignationMaster designation) {
        ResponseMessage<DesignationMaster> response = new ResponseMessage<>();
        try {
            if (designation.getStatus() == null) {
                designation.setStatus(DesignationMaster.DesignationStatus.ACTIVE);
            }
            DesignationMaster saved = designationMasterRepo.save(designation);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Designation saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the designation. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DesignationMaster> findById(int designationId) {
        ResponseMessage<DesignationMaster> response = new ResponseMessage<>();
        try {
            Optional<DesignationMaster> designation = designationMasterRepo.findById(designationId);
            if (designation.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No designation found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(designation.get());
            response.setHeader("Success");
            response.setMessage("Designation fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the designation. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DesignationMaster>> findByDepartment(int departmentId) {
        ResponseMessage<List<DesignationMaster>> response = new ResponseMessage<>();
        try {
            List<DesignationMaster> designations = designationMasterRepo.findByDepartmentId(departmentId);
            response.setResponseOutput(designations);
            response.setHeader("Success");
            response.setMessage("Designations fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch designations. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DesignationMaster>> findAllActive() {
        ResponseMessage<List<DesignationMaster>> response = new ResponseMessage<>();
        try {
            List<DesignationMaster> designations = designationMasterRepo.findByStatus(DesignationMaster.DesignationStatus.ACTIVE);
            response.setResponseOutput(designations);
            response.setHeader("Success");
            response.setMessage("Active designations fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch designations. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DesignationMaster>> findAll() {
        ResponseMessage<List<DesignationMaster>> response = new ResponseMessage<>();
        try {
            List<DesignationMaster> designations = designationMasterRepo.findAll();
            response.setResponseOutput(designations);
            response.setHeader("Success");
            response.setMessage("Designations fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch designations. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DesignationMaster> update(int designationId, DesignationMaster updates) {
        ResponseMessage<DesignationMaster> response = new ResponseMessage<>();
        try {
            Optional<DesignationMaster> existingOpt = designationMasterRepo.findById(designationId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No designation found for this id.");
                response.setStatusCode(404);
                return response;
            }

            DesignationMaster existing = existingOpt.get();
            existing.setDesignationName(updates.getDesignationName());
            existing.setDepartmentId(updates.getDepartmentId());
            existing.setGradeLevel(updates.getGradeLevel());
            existing.setStatus(updates.getStatus());

            DesignationMaster saved = designationMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Designation updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the designation. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int designationId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!designationMasterRepo.existsById(designationId)) {
                response.setHeader("Not found");
                response.setMessage("No designation found for this id.");
                response.setStatusCode(404);
                return response;
            }
            designationMasterRepo.deleteById(designationId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Designation deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the designation. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
