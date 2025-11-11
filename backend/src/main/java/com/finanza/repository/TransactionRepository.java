package com.finanza.repository;

import com.finanza.model.Account;
import com.finanza.model.Category;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByOrganization(Organization organization, Pageable pageable);
    
    Page<Transaction> findByAccount(Account account, Pageable pageable);
    
    Optional<Transaction> findByIdAndOrganization(Long id, Organization organization);
    
    @Query("SELECT t FROM Transaction t WHERE t.organization = :org " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    Page<Transaction> findByOrganizationAndDateRange(
        @Param("org") Organization organization,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.organization = :org " +
           "AND t.category.id = :categoryId")
    Page<Transaction> findByOrganizationAndCategory(
        @Param("org") Organization organization,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.organization = :org " +
           "AND t.type = :type")
    Page<Transaction> findByOrganizationAndType(
        @Param("org") Organization organization,
        @Param("type") Transaction.TransactionType type,
        Pageable pageable
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.organization = :org " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:type IS NULL OR t.type = :type)")
    Page<Transaction> findByFilters(
        @Param("org") Organization organization,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("categoryId") Long categoryId,
        @Param("type") Transaction.TransactionType type,
        Pageable pageable
    );
    
    List<Transaction> findByAccountAndTransactionDateBetween(
        Account account, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    List<Transaction> findByOrganizationAndTransactionDateBetween(
        Organization organization,
        LocalDate startDate,
        LocalDate endDate
    );
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.category = :category " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.organization = :org " +
           "AND t.type = 'EXPENSE'")
    BigDecimal sumAmountByCategoryAndDateRange(
        @Param("category") Category category,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("org") Organization organization
    );
}
