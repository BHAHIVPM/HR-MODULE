package com.bhahi.hrmodule.service;

import com.bhahi.hrmodule.model.DepartmentMaster;
import com.bhahi.hrmodule.repository.DepartmentMasterRepo;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentMasterService {

    private final DepartmentMasterRepo departmentMasterRepo;

    public ResponseMessage<DepartmentMaster> save(DepartmentMaster department) {
        ResponseMessage<DepartmentMaster> response = new ResponseMessage<>();
        try {
            if (department.getStatus() == null) {
                department.setStatus(DepartmentMaster.DepartmentStatus.ACTIVE);
            }
            DepartmentMaster saved = departmentMasterRepo.save(department);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Department saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save the department. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DepartmentMaster> findById(int departmentId) {
        ResponseMessage<DepartmentMaster> response = new ResponseMessage<>();
        try {
            Optional<DepartmentMaster> department = departmentMasterRepo.findById(departmentId);
            if (department.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No department found for this id.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(department.get());
            response.setHeader("Success");
            response.setMessage("Department fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch the department. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DepartmentMaster>> findAllActive() {
        ResponseMessage<List<DepartmentMaster>> response = new ResponseMessage<>();
        try {
            List<DepartmentMaster> departments = departmentMasterRepo.findByStatus(DepartmentMaster.DepartmentStatus.ACTIVE);
            response.setResponseOutput(departments);
            response.setHeader("Success");
            response.setMessage("Active departments fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch departments. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<DepartmentMaster>> findAll() {
        ResponseMessage<List<DepartmentMaster>> response = new ResponseMessage<>();
        try {
            List<DepartmentMaster> departments = departmentMasterRepo.findAll();
            response.setResponseOutput(departments);
            response.setHeader("Success");
            response.setMessage("Departments fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch departments. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<DepartmentMaster> update(int departmentId, DepartmentMaster updates) {
        ResponseMessage<DepartmentMaster> response = new ResponseMessage<>();
        try {
            Optional<DepartmentMaster> existingOpt = departmentMasterRepo.findById(departmentId);
            if (existingOpt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("No department found for this id.");
                response.setStatusCode(404);
                return response;
            }

            DepartmentMaster existing = existingOpt.get();
            existing.setDepartmentName(updates.getDepartmentName());
            existing.setDepartmentHeadId(updates.getDepartmentHeadId());
            existing.setDescription(updates.getDescription());
            existing.setStatus(updates.getStatus());

            DepartmentMaster saved = departmentMasterRepo.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Department updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update the department. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int departmentId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!departmentMasterRepo.existsById(departmentId)) {
                response.setHeader("Not found");
                response.setMessage("No department found for this id.");
                response.setStatusCode(404);
                return response;
            }
            departmentMasterRepo.deleteById(departmentId);
            response.setResponseOutput("Deleted.");
            response.setHeader("Success");
            response.setMessage("Department deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete the department. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
