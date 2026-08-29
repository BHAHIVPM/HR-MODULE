package com.bhahi.hrmodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bhahi.hrmodule.model.UserLogin;

import java.util.Optional;

@Repository
public interface UserLoginRepo extends JpaRepository<UserLogin, Integer>{

    Optional<UserLogin> findByUserId(String userId);
    void deleteByUserId(String userId);
    boolean existsByUserId(String userId);

}
