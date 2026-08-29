package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.LeaveMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveMasterRepo extends JpaRepository<LeaveMaster, Integer> {

    Optional<LeaveMaster> findByLeaveTypeCode(String leaveTypeCode);

    List<LeaveMaster> findByStatus(LeaveMaster.LeaveMasterStatus status);
}
