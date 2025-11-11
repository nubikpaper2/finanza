package com.finanza.repository;

import com.finanza.model.ExchangeRate;
import com.finanza.model.ExchangeRate.RateType;
import com.finanza.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    
    List<ExchangeRate> findByOrganizationAndDateOrderByRateTypeAsc(Organization organization, LocalDate date);
    
    Optional<ExchangeRate> findByOrganizationAndDateAndRateType(
        Organization organization, LocalDate date, RateType rateType);
    
    List<ExchangeRate> findByOrganizationAndRateTypeAndDateBetweenOrderByDateDesc(
        Organization organization, RateType rateType, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT e FROM ExchangeRate e WHERE e.organization = :org AND e.rateType = :rateType " +
           "AND e.date <= :date ORDER BY e.date DESC")
    List<ExchangeRate> findLatestRateBeforeDate(
        @Param("org") Organization organization, 
        @Param("rateType") RateType rateType, 
        @Param("date") LocalDate date);
    
    List<ExchangeRate> findByOrganizationOrderByDateDescRateTypeAsc(Organization organization);
    
    void deleteByOrganizationAndDateAndRateType(Organization organization, LocalDate date, RateType rateType);
}
