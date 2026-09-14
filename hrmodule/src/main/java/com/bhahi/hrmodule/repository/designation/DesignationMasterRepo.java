package com.bhahi.hrmodule.repository.designation;

import com.bhahi.hrmodule.model.designation.DesignationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationMasterRepo extends JpaRepository<DesignationMaster, Integer> {

    Optional<DesignationMaster> findByDesignationCode(String designationCode);

    List<DesignationMaster> findByDepartmentId(Integer departmentId);

    List<DesignationMaster> findByStatus(DesignationMaster.DesignationStatus status);
}
