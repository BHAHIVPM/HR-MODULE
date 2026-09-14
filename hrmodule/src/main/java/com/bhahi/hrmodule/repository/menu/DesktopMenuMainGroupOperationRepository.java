package com.bhahi.hrmodule.repository.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesktopMenuMainGroupOperationRepository extends JpaRepository<DesktopMenuMainGroupMasterOperation, Integer> {

    boolean existsByMainGroupNameIgnoreCase(String mainGroupName);

    boolean existsByMainGroupNameIgnoreCaseAndMainGroupIdNot(String mainGroupName, Integer mainGroupId);

    Optional<DesktopMenuMainGroupMasterOperation> findByMainGroupNameIgnoreCase(String mainGroupName);

    List<DesktopMenuMainGroupMasterOperation> findAllByOrderByHierarchyIdAsc();

    List<DesktopMenuMainGroupMasterOperation> findAllByOrderByMainGroupIdAsc();

    @Query("SELECT MAX(m.mainGroupId) FROM DesktopMenuMainGroupMasterOperation m WHERE m.mainGroupId < 99")
    Integer findMaxId();

    @Query("SELECT MAX(m.hierarchyId) FROM DesktopMenuMainGroupMasterOperation m")
    Integer findMaxHierarchyId();
}