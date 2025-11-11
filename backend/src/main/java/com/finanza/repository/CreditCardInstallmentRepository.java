package com.finanza.repository;

import com.finanza.model.CreditCard;
import com.finanza.model.CreditCardInstallment;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreditCardInstallmentRepository extends JpaRepository<CreditCardInstallment, Long> {
    
    List<CreditCardInstallment> findByTransactionOrderByInstallmentNumberAsc(Transaction transaction);
    
    List<CreditCardInstallment> findByCreditCardAndOrganizationOrderByDueDateAscInstallmentNumberAsc(
        CreditCard creditCard, Organization organization);
    
    List<CreditCardInstallment> findByOrganizationAndDueDateBetweenOrderByDueDateAsc(
        Organization organization, LocalDate startDate, LocalDate endDate);
    
    List<CreditCardInstallment> findByCreditCardAndDueDateBetweenOrderByDueDateAsc(
        CreditCard creditCard, LocalDate startDate, LocalDate endDate);
    
    List<CreditCardInstallment> findByOrganizationAndPaidOrderByDueDateAsc(
        Organization organization, Boolean paid);
    
    @Query("SELECT i FROM CreditCardInstallment i WHERE i.creditCard = :card " +
           "AND i.paid = false AND i.dueDate <= :date ORDER BY i.dueDate ASC")
    List<CreditCardInstallment> findUnpaidInstallmentsDueBefore(
        @Param("card") CreditCard creditCard, @Param("date") LocalDate date);
    
    @Query("SELECT i FROM CreditCardInstallment i WHERE i.organization = :org " +
           "AND i.paid = false ORDER BY i.dueDate ASC")
    List<CreditCardInstallment> findAllUnpaidByOrganization(@Param("org") Organization organization);
    
    @Query("SELECT SUM(i.amount) FROM CreditCardInstallment i WHERE i.creditCard = :card " +
           "AND i.paid = false")
    Double getTotalUnpaidAmount(@Param("card") CreditCard creditCard);
}
