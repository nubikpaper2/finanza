package com.finanza.service;

import com.finanza.dto.CategoryRuleRequest;
import com.finanza.dto.CategoryRuleResponse;
import com.finanza.model.Category;
import com.finanza.model.CategoryRule;
import com.finanza.model.Organization;
import com.finanza.model.User;
import com.finanza.repository.CategoryRepository;
import com.finanza.repository.CategoryRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryRuleService {

    private final CategoryRuleRepository categoryRuleRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryRuleResponse createRule(CategoryRuleRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!category.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para esta categoría");
        }

        CategoryRule rule = new CategoryRule();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setType(request.getType());
        rule.setPattern(request.getPattern());
        rule.setCategory(category);
        rule.setActive(request.getActive() != null ? request.getActive() : true);
        rule.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        rule.setOrganization(user.getOrganization());
        rule.setCreatedBy(user);

        rule = categoryRuleRepository.save(rule);

        return toResponse(rule);
    }

    @Transactional
    public CategoryRuleResponse updateRule(Long id, CategoryRuleRequest request, User user) {
        CategoryRule rule = categoryRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada"));

        if (!rule.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para modificar esta regla");
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            rule.setCategory(category);
        }

        if (request.getName() != null) rule.setName(request.getName());
        if (request.getDescription() != null) rule.setDescription(request.getDescription());
        if (request.getType() != null) rule.setType(request.getType());
        if (request.getPattern() != null) rule.setPattern(request.getPattern());
        if (request.getActive() != null) rule.setActive(request.getActive());
        if (request.getPriority() != null) rule.setPriority(request.getPriority());

        rule = categoryRuleRepository.save(rule);

        return toResponse(rule);
    }

    @Transactional(readOnly = true)
    public List<CategoryRuleResponse> getAllRules(User user) {
        List<CategoryRule> rules = categoryRuleRepository.findByOrganizationOrderByPriorityDesc(
                user.getOrganization());

        List<CategoryRuleResponse> responses = new ArrayList<>();
        for (CategoryRule rule : rules) {
            responses.add(toResponse(rule));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public CategoryRuleResponse getRuleById(Long id, User user) {
        CategoryRule rule = categoryRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada"));

        if (!rule.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para ver esta regla");
        }

        return toResponse(rule);
    }

    @Transactional
    public void deleteRule(Long id, User user) {
        CategoryRule rule = categoryRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regla no encontrada"));

        if (!rule.getOrganization().equals(user.getOrganization())) {
            throw new RuntimeException("No tienes permiso para eliminar esta regla");
        }

        categoryRuleRepository.delete(rule);
    }

    /**
     * Encuentra una categoría basada en las reglas activas
     */
    @Transactional(readOnly = true)
    public Category findCategoryByRules(String description, BigDecimal amount, Organization organization) {
        List<CategoryRule> rules = categoryRuleRepository.findByOrganizationAndActiveTrueOrderByPriorityDesc(
                organization);

        for (CategoryRule rule : rules) {
            if (matchesRule(rule, description, amount)) {
                return rule.getCategory();
            }
        }

        return null;
    }

    private boolean matchesRule(CategoryRule rule, String description, BigDecimal amount) {
        if (description == null) description = "";
        String desc = description.toLowerCase();
        String pattern = rule.getPattern().toLowerCase();

        switch (rule.getType()) {
            case CONTAINS:
                return desc.contains(pattern);
            
            case STARTS_WITH:
                return desc.startsWith(pattern);
            
            case ENDS_WITH:
                return desc.endsWith(pattern);
            
            case EXACT_MATCH:
                return desc.equals(pattern);
            
            case REGEX:
                try {
                    return Pattern.compile(rule.getPattern(), Pattern.CASE_INSENSITIVE)
                            .matcher(description).find();
                } catch (Exception e) {
                    return false;
                }
            
            case AMOUNT_RANGE:
                // Formato del pattern: "min-max" (ej: "100-500")
                try {
                    String[] parts = rule.getPattern().split("-");
                    if (parts.length == 2) {
                        BigDecimal min = new BigDecimal(parts[0]);
                        BigDecimal max = new BigDecimal(parts[1]);
                        return amount.compareTo(min) >= 0 && amount.compareTo(max) <= 0;
                    }
                } catch (Exception e) {
                    return false;
                }
                return false;
            
            default:
                return false;
        }
    }

    private CategoryRuleResponse toResponse(CategoryRule rule) {
        CategoryRuleResponse response = new CategoryRuleResponse();
        response.setId(rule.getId());
        response.setName(rule.getName());
        response.setDescription(rule.getDescription());
        response.setType(rule.getType());
        response.setPattern(rule.getPattern());
        response.setCategoryId(rule.getCategory().getId());
        response.setCategoryName(rule.getCategory().getName());
        response.setActive(rule.getActive());
        response.setPriority(rule.getPriority());
        response.setCreatedAt(rule.getCreatedAt());
        response.setUpdatedAt(rule.getUpdatedAt());
        return response;
    }
}
