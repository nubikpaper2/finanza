package com.finanza.service;

import com.finanza.dto.InstallmentResponse;
import com.finanza.model.CreditCard;
import com.finanza.model.CreditCardInstallment;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import com.finanza.repository.CreditCardInstallmentRepository;
import com.finanza.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditCardInstallmentService {

    private final CreditCardInstallmentRepository installmentRepository;
    private final CreditCardRepository creditCardRepository;

    public List<CreditCardInstallment> createInstallments(Transaction transaction, 
                                                         CreditCard creditCard, 
                                                         Integer totalInstallments,
                                                         Organization organization) {
        List<CreditCardInstallment> installments = new ArrayList<>();
        BigDecimal installmentAmount = transaction.getAmount()
            .divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_UP);

        LocalDate transactionDate = transaction.getTransactionDate();
        LocalDate firstDueDate = calculateNextDueDate(transactionDate, creditCard);

        for (int i = 1; i <= totalInstallments; i++) {
            LocalDate dueDate = firstDueDate.plusMonths(i - 1);
            
            CreditCardInstallment installment = new CreditCardInstallment(
                transaction,
                creditCard,
                i,
                totalInstallments,
                installmentAmount,
                dueDate,
                organization
            );
            
            installments.add(installmentRepository.save(installment));
        }

        return installments;
    }

    public List<InstallmentResponse> getUpcomingInstallments(Organization organization, 
                                                             LocalDate startDate, 
                                                             LocalDate endDate) {
        return installmentRepository
            .findByOrganizationAndDueDateBetweenOrderByDueDateAsc(organization, startDate, endDate)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<InstallmentResponse> getUnpaidInstallments(Organization organization) {
        return installmentRepository.findAllUnpaidByOrganization(organization)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<InstallmentResponse> getInstallmentsByCreditCard(Long creditCardId, 
                                                                 Organization organization) {
        CreditCard creditCard = creditCardRepository.findByIdAndOrganization(creditCardId, organization)
            .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
        
        return installmentRepository
            .findByCreditCardAndOrganizationOrderByDueDateAscInstallmentNumberAsc(creditCard, organization)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public InstallmentResponse markAsPaid(Long installmentId, Organization organization) {
        CreditCardInstallment installment = installmentRepository.findById(installmentId)
            .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        if (!installment.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("No autorizado");
        }

        installment.setPaid(true);
        installment.setPaidDate(LocalDate.now());
        installment = installmentRepository.save(installment);
        
        return toResponse(installment);
    }

    public InstallmentResponse markAsUnpaid(Long installmentId, Organization organization) {
        CreditCardInstallment installment = installmentRepository.findById(installmentId)
            .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        if (!installment.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("No autorizado");
        }

        installment.setPaid(false);
        installment.setPaidDate(null);
        installment = installmentRepository.save(installment);
        
        return toResponse(installment);
    }

    private LocalDate calculateNextDueDate(LocalDate transactionDate, CreditCard creditCard) {
        YearMonth transactionMonth = YearMonth.from(transactionDate);
        int transactionDay = transactionDate.getDayOfMonth();
        int closingDay = creditCard.getClosingDay();
        int dueDay = creditCard.getDueDay();

        YearMonth dueMonth;
        if (transactionDay <= closingDay) {
            // La transacción entra en el período actual
            dueMonth = transactionMonth.plusMonths(1);
        } else {
            // La transacción entra en el próximo período
            dueMonth = transactionMonth.plusMonths(2);
        }

        return LocalDate.of(dueMonth.getYear(), dueMonth.getMonth(), 
                          Math.min(dueDay, dueMonth.lengthOfMonth()));
    }

    private InstallmentResponse toResponse(CreditCardInstallment installment) {
        InstallmentResponse response = new InstallmentResponse();
        response.setId(installment.getId());
        response.setTransactionId(installment.getTransaction().getId());
        response.setTransactionDescription(installment.getTransaction().getDescription());
        response.setCreditCardId(installment.getCreditCard().getId());
        response.setCreditCardName(installment.getCreditCard().getName());
        response.setInstallmentNumber(installment.getInstallmentNumber());
        response.setTotalInstallments(installment.getTotalInstallments());
        response.setAmount(installment.getAmount());
        response.setDueDate(installment.getDueDate());
        response.setPaid(installment.getPaid());
        response.setPaidDate(installment.getPaidDate());
        return response;
    }
}
