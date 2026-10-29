package com.bhahi.hrmodule.service.recruitment;

import com.bhahi.hrmodule.model.recruitment.JobRequisition;
import com.bhahi.hrmodule.repository.recruitment.JobRequisitionRepository;
import com.bhahi.hrmodule.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobRequisitionService {

    private final JobRequisitionRepository jobRequisitionRepository;

    public ResponseMessage<JobRequisition> save(JobRequisition requisition) {
        ResponseMessage<JobRequisition> response = new ResponseMessage<>();
        try {
            if (requisition.getStatus() == null) {
                requisition.setStatus(JobRequisition.RequisitionStatus.OPEN);
            }
            if (requisition.getCreatedAt() == null) {
                requisition.setCreatedAt(LocalDate.now());
            }
            JobRequisition saved = jobRequisitionRepository.save(requisition);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Job requisition saved successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Save failed");
            response.setMessage("Could not save job requisition. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<JobRequisition> findById(int jobId) {
        ResponseMessage<JobRequisition> response = new ResponseMessage<>();
        try {
            Optional<JobRequisition> req = jobRequisitionRepository.findById(jobId);
            if (req.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Job requisition not found.");
                response.setStatusCode(404);
                return response;
            }
            response.setResponseOutput(req.get());
            response.setHeader("Success");
            response.setMessage("Job requisition fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch job requisition. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<JobRequisition>> findAll() {
        ResponseMessage<List<JobRequisition>> response = new ResponseMessage<>();
        try {
            List<JobRequisition> list = jobRequisitionRepository.findAll();
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Job requisitions fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch job requisitions. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<List<JobRequisition>> findByStatus(JobRequisition.RequisitionStatus status) {
        ResponseMessage<List<JobRequisition>> response = new ResponseMessage<>();
        try {
            List<JobRequisition> list = jobRequisitionRepository.findByStatus(status);
            response.setResponseOutput(list);
            response.setHeader("Success");
            response.setMessage("Job requisitions fetched successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Fetch failed");
            response.setMessage("Could not fetch job requisitions. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<JobRequisition> update(int jobId, JobRequisition updates) {
        ResponseMessage<JobRequisition> response = new ResponseMessage<>();
        try {
            Optional<JobRequisition> opt = jobRequisitionRepository.findById(jobId);
            if (opt.isEmpty()) {
                response.setHeader("Not found");
                response.setMessage("Job requisition not found.");
                response.setStatusCode(404);
                return response;
            }
            JobRequisition existing = opt.get();
            if (updates.getJobTitle() != null) existing.setJobTitle(updates.getJobTitle());
            if (updates.getDepartment() != null) existing.setDepartment(updates.getDepartment());
            if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
            if (updates.getRequirements() != null) existing.setRequirements(updates.getRequirements());
            if (updates.getStatus() != null) existing.setStatus(updates.getStatus());

            JobRequisition saved = jobRequisitionRepository.save(existing);
            response.setResponseOutput(saved);
            response.setHeader("Success");
            response.setMessage("Job requisition updated successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Update failed");
            response.setMessage("Could not update job requisition. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }

    public ResponseMessage<String> delete(int jobId) {
        ResponseMessage<String> response = new ResponseMessage<>();
        try {
            if (!jobRequisitionRepository.existsById(jobId)) {
                response.setHeader("Not found");
                response.setMessage("Job requisition not found.");
                response.setStatusCode(404);
                return response;
            }
            jobRequisitionRepository.deleteById(jobId);
            response.setResponseOutput("Deleted");
            response.setHeader("Success");
            response.setMessage("Job requisition deleted successfully.");
            response.setStatusCode(200);
            return response;
        } catch (Exception e) {
            response.setHeader("Delete failed");
            response.setMessage("Could not delete job requisition. " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }
    }
}
