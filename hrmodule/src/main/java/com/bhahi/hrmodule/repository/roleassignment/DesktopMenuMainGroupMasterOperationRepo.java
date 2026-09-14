package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.DesktopMenuMainGroupMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesktopMenuMainGroupMasterOperationRepo extends JpaRepository<DesktopMenuMainGroupMasterOperation, Integer> {
}