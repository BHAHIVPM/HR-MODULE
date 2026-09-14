package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.RoleAssignmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAssignmentMasterRepo extends JpaRepository<RoleAssignmentMaster, Integer> {

    List<RoleAssignmentMaster> findByLoginId(String loginId);

    List<RoleAssignmentMaster> findByRoleId(int roleId);

    @Modifying
    @Query("DELETE FROM RoleAssignmentMaster r WHERE r.loginId = :loginId")
    void deleteByLoginId(@Param("loginId") String loginId);

    @Query("SELECT r FROM RoleAssignmentMaster r WHERE SUBSTRING(r.loginId, 5, 2) = :code")
    List<RoleAssignmentMaster> findByLoginIdPrefix(@Param("code") String code);
}