package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.UserRoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleMasterRepo extends JpaRepository<UserRoleMaster, Integer> {

    Optional<UserRoleMaster> findByRoleName(String roleName);

    List<UserRoleMaster> findByRoleCategory(String roleCategory);

    List<UserRoleMaster> findByRoleCategoryIsNull();
}