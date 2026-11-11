package com.bhahi.hrmodule.repository.recruitment;

import com.bhahi.hrmodule.model.recruitment.JobRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRequisitionRepository extends JpaRepository<JobRequisition, Integer> {
    List<JobRequisition> findByStatus(JobRequisition.RequisitionStatus status);
}
