package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.RolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePrivilegeRepo extends JpaRepository<RolePrivilege, Integer> {

    List<RolePrivilege> findByRoleId(int roleId);

    List<RolePrivilege> findByRoleIdAndMenuId(int roleId, int menuId);

    @Modifying
    @Query("DELETE FROM RolePrivilege r WHERE r.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") int roleId);

    Optional<RolePrivilege> findByPrivilegeId(int privilegeId);
}