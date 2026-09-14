package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.LoginAccessMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAccessMasterRepo extends JpaRepository<LoginAccessMaster, Integer> {

    List<LoginAccessMaster> findByUserType(String userType);

    @Query("SELECT l FROM LoginAccessMaster l WHERE l.userType = :userType AND EXISTS " +
           "(SELECT 1 FROM RoleAssignmentMaster r WHERE r.loginId = l.loginId)")
    List<LoginAccessMaster> findUsersWithRoles(@Param("userType") String userType);
}