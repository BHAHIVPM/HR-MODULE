package com.bhahi.hrmodule.repository.auth;

import com.bhahi.hrmodule.model.auth.ClientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientMasterRepository extends JpaRepository<ClientMaster, Integer> {
    Optional<ClientMaster> findByClientId(Integer clientId);
    Optional<ClientMaster> findByClientCode(String clientCode);
}
