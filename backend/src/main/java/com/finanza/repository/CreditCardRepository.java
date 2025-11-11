package com.finanza.repository;

import com.finanza.model.CreditCard;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    
    List<CreditCard> findByOrganizationOrderByNameAsc(Organization organization);
    
    List<CreditCard> findByOrganizationAndActiveOrderByNameAsc(Organization organization, Boolean active);
    
    Optional<CreditCard> findByIdAndOrganization(Long id, Organization organization);
    
    boolean existsByIdAndOrganization(Long id, Organization organization);
}
