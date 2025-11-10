package com.finanza.repository;

import com.finanza.model.Account;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOrganization(Organization organization);
    List<Account> findByOrganizationAndActiveTrue(Organization organization);
    Optional<Account> findByIdAndOrganization(Long id, Organization organization);
}
