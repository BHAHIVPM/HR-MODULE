package com.bhahi.hrmodule.repository.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesktopMenuMasterOperationRepository extends JpaRepository<DesktopMenuNameMasterOperation, Integer> {

    boolean existsByMenuNameIgnoreCaseAndMainGroupId(String menuName, int mainGroupId);

    boolean existsByMenuNameIgnoreCaseAndMainGroupIdAndMenuNameIdNot(String menuName, int mainGroupId, int menuNameId);

    List<DesktopMenuNameMasterOperation> findByMainGroupIdOrderByHierarchyIdAsc(int mainGroupId);

    List<DesktopMenuNameMasterOperation> findAllByOrderByHierarchyIdAsc();

    List<DesktopMenuNameMasterOperation> findByIsPrivilege(String isPrivilege);

    @Query("SELECT MAX(m.menuNameId) FROM DesktopMenuNameMasterOperation m WHERE m.menuNameId < 9999")
    Integer findMaxId();

    @Query("SELECT m FROM DesktopMenuNameMasterOperation m ORDER BY m.hierarchyId")
    List<DesktopMenuNameMasterOperation> findAllOrderedByHierarchy();
}