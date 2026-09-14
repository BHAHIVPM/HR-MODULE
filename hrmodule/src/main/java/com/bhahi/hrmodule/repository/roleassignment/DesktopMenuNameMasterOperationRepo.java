package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.DesktopMenuNameMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesktopMenuNameMasterOperationRepo extends JpaRepository<DesktopMenuNameMasterOperation, Integer> {

    List<DesktopMenuNameMasterOperation> findByIsPrivilege(String isPrivilege);
}