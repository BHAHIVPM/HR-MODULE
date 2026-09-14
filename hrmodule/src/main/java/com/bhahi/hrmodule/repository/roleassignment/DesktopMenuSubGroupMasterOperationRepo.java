package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.DesktopMenuSubGroupMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesktopMenuSubGroupMasterOperationRepo extends JpaRepository<DesktopMenuSubGroupMasterOperation, Integer> {
}