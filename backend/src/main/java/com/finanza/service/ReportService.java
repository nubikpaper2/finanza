package com.finanza.service;

import com.finanza.dto.MonthlyReportResponse;
import com.finanza.model.Transaction;
import com.finanza.model.User;
import com.finanza.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(Integer year, Integer month, User user) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Transaction> transactions = transactionRepository.findByOrganizationAndTransactionDateBetween(
                user.getOrganization(), startDate, endDate);

        MonthlyReportResponse report = new MonthlyReportResponse();
        report.setYear(year);
        report.setMonth(month);

        // Calcular totales
        final BigDecimal[] totalIncome = {BigDecimal.ZERO};
        final BigDecimal[] totalExpenses = {BigDecimal.ZERO};

        Map<Long, CategoryData> expensesByCategory = new HashMap<>();
        Map<Long, CategoryData> incomeByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                totalIncome[0] = totalIncome[0].add(transaction.getAmount());
                if (transaction.getCategory() != null) {
                    updateCategoryData(incomeByCategory, transaction);
                }
            } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
                totalExpenses[0] = totalExpenses[0].add(transaction.getAmount());
                if (transaction.getCategory() != null) {
                    updateCategoryData(expensesByCategory, transaction);
                }
            }
        }

        report.setTotalIncome(totalIncome[0]);
        report.setTotalExpenses(totalExpenses[0]);
        report.setBalance(totalIncome[0].subtract(totalExpenses[0]));

        // Top categorías de gastos (ordenadas por monto descendente)
        List<MonthlyReportResponse.CategorySummary> topExpenses = expensesByCategory.values().stream()
                .map(data -> {
                    double percentage = totalExpenses[0].compareTo(BigDecimal.ZERO) > 0
                            ? data.amount.divide(totalExpenses[0], 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return new MonthlyReportResponse.CategorySummary(
                            data.categoryId, data.categoryName, data.amount, data.count, percentage);
                })
                .sorted(Comparator.comparing(MonthlyReportResponse.CategorySummary::getAmount).reversed())
                .collect(Collectors.toList());

        // Ingresos por categoría
        List<MonthlyReportResponse.CategorySummary> incomes = incomeByCategory.values().stream()
                .map(data -> {
                    double percentage = totalIncome[0].compareTo(BigDecimal.ZERO) > 0
                            ? data.amount.divide(totalIncome[0], 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return new MonthlyReportResponse.CategorySummary(
                            data.categoryId, data.categoryName, data.amount, data.count, percentage);
                })
                .sorted(Comparator.comparing(MonthlyReportResponse.CategorySummary::getAmount).reversed())
                .collect(Collectors.toList());

        report.setTopExpenseCategories(topExpenses);
        report.setIncomeByCategory(incomes);

        return report;
    }

    @Transactional(readOnly = true)
    public List<MonthlyReportResponse> getYearlyReport(Integer year, User user) {
        List<MonthlyReportResponse> reports = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            reports.add(getMonthlyReport(year, month, user));
        }
        return reports;
    }

    private void updateCategoryData(Map<Long, CategoryData> map, Transaction transaction) {
        Long categoryId = transaction.getCategory().getId();
        CategoryData data = map.getOrDefault(categoryId, 
                new CategoryData(categoryId, transaction.getCategory().getName()));
        data.amount = data.amount.add(transaction.getAmount());
        data.count++;
        map.put(categoryId, data);
    }

    private static class CategoryData {
        Long categoryId;
        String categoryName;
        BigDecimal amount = BigDecimal.ZERO;
        int count = 0;

        CategoryData(Long categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }
}
