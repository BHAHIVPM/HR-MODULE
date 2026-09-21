package com.bhahi.hrmodule.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bhahi.hrmodule.model.auth.UserLogin;

import java.util.Optional;

@Repository
public interface UserLoginRepo extends JpaRepository<UserLogin, Integer>{

    Optional<UserLogin> findByUserId(String userId);
    void deleteByUserId(String userId);
    boolean existsByUserId(String userId);

    boolean existsByUserMail(String userMail);
    boolean existsByMobileNo(String mobileNo);
    Optional<UserLogin> findByUserMail(String userMail);
    Optional<UserLogin> findByMobileNo(String mobileNo);

    // Finds the highest 5-digit counter (digits 7-11) among users sharing the same
    // 6-digit prefix (4-digit client id + 2-digit user type code).
    // The Verhoeff check digit (digit 12) is stripped automatically by only looking
    // at characters 7 through 11 of the login_id column.
    @Query(value = "SELECT MAX(CAST(SUBSTRING(user_id, 7, 5) AS UNSIGNED)) " +
                   "FROM user_login WHERE user_id LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    Optional<Integer> findMaxCounterByPrefix(@Param("prefix") String prefix);
}
