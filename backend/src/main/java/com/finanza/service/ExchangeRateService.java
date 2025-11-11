package com.finanza.service;

import com.finanza.dto.ExchangeRateRequest;
import com.finanza.dto.ExchangeRateResponse;
import com.finanza.model.ExchangeRate;
import com.finanza.model.ExchangeRate.RateType;
import com.finanza.model.Organization;
import com.finanza.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateResponse createOrUpdate(ExchangeRateRequest request, Organization organization) {
        RateType rateType = RateType.valueOf(request.getRateType().toUpperCase());
        
        ExchangeRate exchangeRate = exchangeRateRepository
            .findByOrganizationAndDateAndRateType(organization, request.getDate(), rateType)
            .orElse(new ExchangeRate());
        
        exchangeRate.setDate(request.getDate());
        exchangeRate.setRateType(rateType);
        exchangeRate.setBuyRate(request.getBuyRate());
        exchangeRate.setSellRate(request.getSellRate());
        exchangeRate.setOrganization(organization);
        
        exchangeRate = exchangeRateRepository.save(exchangeRate);
        return toResponse(exchangeRate);
    }

    public List<ExchangeRateResponse> getRatesByDate(LocalDate date, Organization organization) {
        return exchangeRateRepository.findByOrganizationAndDateOrderByRateTypeAsc(organization, date)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ExchangeRateResponse> getRatesByDateRange(LocalDate startDate, LocalDate endDate, 
                                                          String rateType, Organization organization) {
        RateType type = RateType.valueOf(rateType.toUpperCase());
        return exchangeRateRepository
            .findByOrganizationAndRateTypeAndDateBetweenOrderByDateDesc(organization, type, startDate, endDate)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ExchangeRateResponse getLatestRate(String rateType, Organization organization) {
        RateType type = RateType.valueOf(rateType.toUpperCase());
        List<ExchangeRate> rates = exchangeRateRepository
            .findLatestRateBeforeDate(organization, type, LocalDate.now());
        
        if (rates.isEmpty()) {
            return null;
        }
        
        return toResponse(rates.get(0));
    }

    public BigDecimal getRateForDate(LocalDate date, String rateType, Organization organization) {
        RateType type = RateType.valueOf(rateType.toUpperCase());
        return exchangeRateRepository
            .findByOrganizationAndDateAndRateType(organization, date, type)
            .map(ExchangeRate::getAverageRate)
            .orElseGet(() -> {
                // Si no hay tasa para esa fecha, buscar la más reciente
                List<ExchangeRate> rates = exchangeRateRepository
                    .findLatestRateBeforeDate(organization, type, date);
                return rates.isEmpty() ? BigDecimal.ONE : rates.get(0).getAverageRate();
            });
    }

    public List<ExchangeRateResponse> getAllRates(Organization organization) {
        return exchangeRateRepository.findByOrganizationOrderByDateDescRateTypeAsc(organization)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public void deleteRate(Long id, Organization organization) {
        ExchangeRate rate = exchangeRateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tipo de cambio no encontrado"));
        
        if (!rate.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("No autorizado");
        }
        
        exchangeRateRepository.delete(rate);
    }

    private ExchangeRateResponse toResponse(ExchangeRate rate) {
        return new ExchangeRateResponse(
            rate.getId(),
            rate.getDate(),
            rate.getRateType().name(),
            rate.getBuyRate(),
            rate.getSellRate(),
            rate.getAverageRate()
        );
    }
}
