package com.finanza.repository;

import com.finanza.model.ArgentinaTax;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArgentinaTaxRepository extends JpaRepository<ArgentinaTax, Long> {
    
    List<ArgentinaTax> findByTransactionOrderByTaxTypeAsc(Transaction transaction);
    
    List<ArgentinaTax> findByOrganizationOrderByCreatedAtDesc(Organization organization);
    
    @Query("SELECT SUM(t.amount) FROM ArgentinaTax t WHERE t.transaction = :transaction")
    Double getTotalTaxAmountForTransaction(@Param("transaction") Transaction transaction);
    
    @Query("SELECT t FROM ArgentinaTax t WHERE t.organization = :org " +
           "AND t.transaction.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transaction.transactionDate DESC")
    List<ArgentinaTax> findByOrganizationAndDateRange(
        @Param("org") Organization organization,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate);
    
    void deleteByTransaction(Transaction transaction);
}
