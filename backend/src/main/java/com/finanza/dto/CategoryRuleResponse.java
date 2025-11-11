package com.finanza.dto;

import com.finanza.model.CategoryRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRuleResponse {
    private Long id;
    private String name;
    private String description;
    private CategoryRule.RuleType type;
    private String pattern;
    private Long categoryId;
    private String categoryName;
    private Boolean active;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
