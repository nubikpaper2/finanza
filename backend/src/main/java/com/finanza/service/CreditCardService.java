package com.finanza.service;

import com.finanza.dto.CreditCardRequest;
import com.finanza.dto.CreditCardResponse;
import com.finanza.model.Account;
import com.finanza.model.CreditCard;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.repository.AccountRepository;
import com.finanza.repository.CreditCardInstallmentRepository;
import com.finanza.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardInstallmentRepository installmentRepository;
    private final AccountRepository accountRepository;

    public CreditCardResponse create(CreditCardRequest request, Organization organization, User user) {
        CreditCard creditCard = new CreditCard();
        creditCard.setName(request.getName());
        creditCard.setLastFourDigits(request.getLastFourDigits());
        creditCard.setClosingDay(request.getClosingDay());
        creditCard.setDueDay(request.getDueDay());
        creditCard.setCreditLimit(request.getCreditLimit());
        creditCard.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ARS");
        creditCard.setBank(request.getBank());
        creditCard.setActive(request.getActive() != null ? request.getActive() : true);
        creditCard.setOrganization(organization);
        creditCard.setCreatedBy(user);

        if (request.getAccountId() != null) {
            Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
            creditCard.setAccount(account);
        }

        creditCard = creditCardRepository.save(creditCard);
        return toResponse(creditCard);
    }

    public CreditCardResponse update(Long id, CreditCardRequest request, Organization organization) {
        CreditCard creditCard = creditCardRepository.findByIdAndOrganization(id, organization)
            .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        creditCard.setName(request.getName());
        creditCard.setLastFourDigits(request.getLastFourDigits());
        creditCard.setClosingDay(request.getClosingDay());
        creditCard.setDueDay(request.getDueDay());
        creditCard.setCreditLimit(request.getCreditLimit());
        creditCard.setCurrency(request.getCurrency());
        creditCard.setBank(request.getBank());
        creditCard.setActive(request.getActive());

        if (request.getAccountId() != null) {
            Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
            creditCard.setAccount(account);
        } else {
            creditCard.setAccount(null);
        }

        creditCard = creditCardRepository.save(creditCard);
        return toResponse(creditCard);
    }

    public List<CreditCardResponse> getAll(Organization organization) {
        return creditCardRepository.findByOrganizationOrderByNameAsc(organization)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<CreditCardResponse> getActive(Organization organization) {
        return creditCardRepository.findByOrganizationAndActiveOrderByNameAsc(organization, true)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public CreditCardResponse getById(Long id, Organization organization) {
        CreditCard creditCard = creditCardRepository.findByIdAndOrganization(id, organization)
            .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
        return toResponse(creditCard);
    }

    public void delete(Long id, Organization organization) {
        CreditCard creditCard = creditCardRepository.findByIdAndOrganization(id, organization)
            .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
        creditCardRepository.delete(creditCard);
    }

    private CreditCardResponse toResponse(CreditCard card) {
        CreditCardResponse response = new CreditCardResponse();
        response.setId(card.getId());
        response.setName(card.getName());
        response.setLastFourDigits(card.getLastFourDigits());
        response.setClosingDay(card.getClosingDay());
        response.setDueDay(card.getDueDay());
        response.setCreditLimit(card.getCreditLimit());
        response.setCurrency(card.getCurrency());
        response.setBank(card.getBank());
        response.setActive(card.getActive());

        if (card.getAccount() != null) {
            response.setAccountId(card.getAccount().getId());
            response.setAccountName(card.getAccount().getName());
        }

        // Calcular deuda actual
        Double unpaidAmount = installmentRepository.getTotalUnpaidAmount(card);
        BigDecimal currentDebt = unpaidAmount != null ? BigDecimal.valueOf(unpaidAmount) : BigDecimal.ZERO;
        response.setCurrentDebt(currentDebt);

        // Calcular crédito disponible
        if (card.getCreditLimit() != null) {
            BigDecimal availableCredit = card.getCreditLimit().subtract(currentDebt);
            response.setAvailableCredit(availableCredit);
        }

        return response;
    }
}
