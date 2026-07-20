package com.bhahi.hrmodule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bhahi.hrmodule.model.UserLogin;

import java.util.Optional;

@Repository
public interface UserLoginRepo extends JpaRepository<UserLogin, Integer>{

    Optional<UserLogin> findByLoginId(String loginId);

}
