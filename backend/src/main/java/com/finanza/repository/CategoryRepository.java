package com.finanza.repository;

import com.finanza.model.Category;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByOrganizationAndActiveTrue(Organization organization);
    
    List<Category> findByOrganizationAndTypeAndActiveTrue(Organization organization, Category.TransactionType type);
    
    Optional<Category> findByIdAndOrganization(Long id, Organization organization);
    
    boolean existsByNameAndOrganization(String name, Organization organization);
}
