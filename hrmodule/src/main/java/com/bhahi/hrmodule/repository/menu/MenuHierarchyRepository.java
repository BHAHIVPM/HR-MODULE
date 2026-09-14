package com.bhahi.hrmodule.repository.menu;

import com.bhahi.hrmodule.model.menu.DesktopMenuMainGroupMasterOperation;
import com.bhahi.hrmodule.model.menu.DesktopMenuNameMasterOperation;
import com.bhahi.hrmodule.model.menu.DesktopMenuSubGroupMasterOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuHierarchyRepository extends JpaRepository<DesktopMenuMainGroupMasterOperation, Integer> {

    @Query("SELECT m FROM DesktopMenuMainGroupMasterOperation m ORDER BY m.hierarchyId ASC")
    List<DesktopMenuMainGroupMasterOperation> fetchMainGroups();

    @Query("SELECT s FROM DesktopMenuSubGroupMasterOperation s")
    List<DesktopMenuSubGroupMasterOperation> fetchSubGroups();

    @Query("SELECT m FROM DesktopMenuNameMasterOperation m ORDER BY m.hierarchyId ASC")
    List<DesktopMenuNameMasterOperation> fetchMenuItems();
}