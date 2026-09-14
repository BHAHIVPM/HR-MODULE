package com.bhahi.hrmodule.repository.roleassignment;

import com.bhahi.hrmodule.model.roleassignment.UserLevelPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLevelPrivilegeRepo extends JpaRepository<UserLevelPrivilege, Integer> {

    List<UserLevelPrivilege> findByLoginId(String loginId);

    List<UserLevelPrivilege> findByLoginIdAndMenuId(String loginId, int menuId);

    @Modifying
    @Query("DELETE FROM UserLevelPrivilege u WHERE u.loginId = :loginId")
    void deleteByLoginId(@Param("loginId") String loginId);

    @Query("SELECT u FROM UserLevelPrivilege u WHERE SUBSTRING(u.loginId, 5, 2) = :code")
    List<UserLevelPrivilege> findByLoginIdPrefix(@Param("code") String code);
}