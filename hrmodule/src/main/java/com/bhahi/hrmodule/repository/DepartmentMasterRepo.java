package com.bhahi.hrmodule.repository;

import com.bhahi.hrmodule.model.DepartmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentMasterRepo extends JpaRepository<DepartmentMaster, Integer> {

    Optional<DepartmentMaster> findByDepartmentCode(String departmentCode);

    List<DepartmentMaster> findByStatus(DepartmentMaster.DepartmentStatus status);
}
