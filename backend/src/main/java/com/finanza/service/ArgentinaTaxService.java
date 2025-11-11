package com.finanza.service;

import com.finanza.dto.ArgentinaTaxRequest;
import com.finanza.dto.ArgentinaTaxResponse;
import com.finanza.model.ArgentinaTax;
import com.finanza.model.ArgentinaTax.TaxType;
import com.finanza.model.Organization;
import com.finanza.model.Transaction;
import com.finanza.repository.ArgentinaTaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArgentinaTaxService {

    private final ArgentinaTaxRepository taxRepository;

    public List<ArgentinaTax> createTaxesForTransaction(Transaction transaction, 
                                                       List<ArgentinaTaxRequest> taxRequests,
                                                       Organization organization) {
        List<ArgentinaTax> taxes = new ArrayList<>();
        
        if (taxRequests != null && !taxRequests.isEmpty()) {
            for (ArgentinaTaxRequest request : taxRequests) {
                TaxType taxType = TaxType.valueOf(request.getTaxType().toUpperCase());
                
                ArgentinaTax tax = new ArgentinaTax(
                    transaction,
                    taxType,
                    request.getPercentage(),
                    request.getAmount(),
                    request.getDescription(),
                    organization
                );
                
                taxes.add(taxRepository.save(tax));
            }
        }
        
        return taxes;
    }

    public List<ArgentinaTaxResponse> getTaxesByTransaction(Long transactionId) {
        // Este método requeriría inyectar TransactionRepository
        // Por ahora retornamos lista vacía, se completará en el controller
        return new ArrayList<>();
    }

    public List<ArgentinaTaxResponse> getTaxesByDateRange(LocalDate startDate, 
                                                         LocalDate endDate, 
                                                         Organization organization) {
        return taxRepository.findByOrganizationAndDateRange(organization, startDate, endDate)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public BigDecimal calculateTaxAmount(BigDecimal baseAmount, BigDecimal percentage) {
        return baseAmount.multiply(percentage).divide(BigDecimal.valueOf(100));
    }

    public void deleteTaxesForTransaction(Transaction transaction) {
        taxRepository.deleteByTransaction(transaction);
    }

    private ArgentinaTaxResponse toResponse(ArgentinaTax tax) {
        return new ArgentinaTaxResponse(
            tax.getId(),
            tax.getTransaction().getId(),
            tax.getTaxType().name(),
            tax.getPercentage(),
            tax.getAmount(),
            tax.getDescription()
        );
    }
}
