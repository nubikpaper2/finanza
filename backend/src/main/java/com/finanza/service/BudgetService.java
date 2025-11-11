package com.finanza.service;

import com.finanza.dto.BudgetRequest;
import com.finanza.dto.BudgetResponse;
import com.finanza.model.*;
import com.finanza.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BudgetResponse createBudget(BudgetRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Verificar que la categoría pertenece a la organización del usuario
        if (!category.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para esta categoría");
        }

        // Verificar si ya existe un presupuesto para esta categoría y periodo
        budgetRepository.findByOrganizationAndCategoryAndYearAndMonth(
                user.getOrganization(), category, request.getYear(), request.getMonth()
        ).ifPresent(b -> {
            throw new RuntimeException("Ya existe un presupuesto para esta categoría en este periodo");
        });

        Budget budget = new Budget();
        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setYear(request.getYear());
        budget.setMonth(request.getMonth());
        budget.setOrganization(user.getOrganization());
        budget.setCreatedBy(user);

        budget = budgetRepository.save(budget);

        return toBudgetResponse(budget, user.getOrganization());
    }

    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        if (!budget.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para modificar este presupuesto");
        }

        budget.setAmount(request.getAmount());
        budget = budgetRepository.save(budget);

        return toBudgetResponse(budget, user.getOrganization());
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByMonth(Integer year, Integer month, User user) {
        List<Budget> budgets = budgetRepository.findByOrganizationAndYearAndMonthOrderByCategoryName(
                user.getOrganization(), year, month);

        List<BudgetResponse> responses = new ArrayList<>();
        for (Budget budget : budgets) {
            responses.add(toBudgetResponse(budget, user.getOrganization()));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByYear(Integer year, User user) {
        List<Budget> budgets = budgetRepository.findByOrganizationAndYear(
                user.getOrganization(), year);

        List<BudgetResponse> responses = new ArrayList<>();
        for (Budget budget : budgets) {
            responses.add(toBudgetResponse(budget, user.getOrganization()));
        }

        return responses;
    }

    @Transactional
    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        if (!budget.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para eliminar este presupuesto");
        }

        budgetRepository.delete(budget);
    }

    private BudgetResponse toBudgetResponse(Budget budget, Organization organization) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setCategoryId(budget.getCategory().getId());
        response.setCategoryName(budget.getCategory().getName());
        response.setAmount(budget.getAmount());
        response.setYear(budget.getYear());
        response.setMonth(budget.getMonth());
        response.setCreatedAt(budget.getCreatedAt());
        response.setUpdatedAt(budget.getUpdatedAt());

        // Calcular el gasto actual del mes
        LocalDate startDate = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        BigDecimal spent = transactionRepository.sumAmountByCategoryAndDateRange(
                budget.getCategory(), startDate, endDate, organization);

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        response.setSpent(spent);
        response.setRemaining(budget.getAmount().subtract(spent));
        
        // Calcular porcentaje
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            double percentage = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            response.setPercentage(percentage);
        } else {
            response.setPercentage(0.0);
        }

        return response;
    }
}
