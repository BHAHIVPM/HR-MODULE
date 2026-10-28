package com.bhahi.hrmodule.repository.auth;

import com.bhahi.hrmodule.model.auth.ClientDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDomainRepository extends JpaRepository<ClientDomain, Integer> {
    Optional<ClientDomain> findByClientId(Integer clientId);
}
