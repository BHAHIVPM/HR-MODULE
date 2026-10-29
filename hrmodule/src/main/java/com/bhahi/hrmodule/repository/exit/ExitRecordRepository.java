package com.bhahi.hrmodule.repository.exit;

import com.bhahi.hrmodule.model.exit.ExitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExitRecordRepository extends JpaRepository<ExitRecord, Integer> {
    Optional<ExitRecord> findByEmployeeId(int employeeId);
}
