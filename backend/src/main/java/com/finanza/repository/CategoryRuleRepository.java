package com.finanza.repository;

import com.finanza.model.CategoryRule;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRuleRepository extends JpaRepository<CategoryRule, Long> {

    List<CategoryRule> findByOrganizationAndActiveTrueOrderByPriorityDesc(Organization organization);

    List<CategoryRule> findByOrganizationOrderByPriorityDesc(Organization organization);
}
