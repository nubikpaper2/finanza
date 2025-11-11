package com.finanza.repository;

import com.finanza.model.Budget;
import com.finanza.model.Category;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByOrganizationAndYearAndMonthOrderByCategoryName(
            Organization organization, Integer year, Integer month);

    List<Budget> findByOrganizationAndYearOrderByMonthDescCategoryName(
            Organization organization, Integer year);

    Optional<Budget> findByOrganizationAndCategoryAndYearAndMonth(
            Organization organization, Category category, Integer year, Integer month);

    @Query("SELECT b FROM Budget b WHERE b.organization = :org AND b.year = :year")
    List<Budget> findByOrganizationAndYear(
            @Param("org") Organization organization, @Param("year") Integer year);
}
