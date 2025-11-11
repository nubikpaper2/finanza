package com.finanza.dto;

import com.finanza.model.CategoryRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRuleRequest {
    private String name;
    private String description;
    private CategoryRule.RuleType type;
    private String pattern;
    private Long categoryId;
    private Boolean active;
    private Integer priority;
}
