package com.bhahi.hrmodule.repository.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesktopSubMenuOperationRepository extends JpaRepository<DesktopMenuSubGroupMasterOperation, Integer> {

    boolean existsBySubGroupNameIgnoreCase(String subGroupName);

    boolean existsBySubGroupNameIgnoreCaseAndSubGroupIdNot(String subGroupName, int subGroupId);

    List<DesktopMenuSubGroupMasterOperation> findAllByOrderBySubGroupIdAsc();

    @Query("SELECT MAX(s.subGroupId) FROM DesktopMenuSubGroupMasterOperation s WHERE s.subGroupId < 999")
    Integer findMaxId();
}