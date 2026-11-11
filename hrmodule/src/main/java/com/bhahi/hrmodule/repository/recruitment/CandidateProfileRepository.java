package com.bhahi.hrmodule.repository.recruitment;

import com.bhahi.hrmodule.model.recruitment.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Integer> {
    List<CandidateProfile> findByJobId(int jobId);
    List<CandidateProfile> findByStage(CandidateProfile.CandidateStage stage);
}
