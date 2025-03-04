package com.cccp13.docker.salary.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "salary_rules")
@Data
public class SalaryRule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "condition_expression", nullable = false)
    private String conditionExpression;

    @Column(name = "bonus_amount", nullable = false)
    private BigDecimal bonusAmount;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "rule_type")
    @Enumerated(EnumType.STRING)
    private RuleType ruleType;

    @Version
    private Long version;

    public enum RuleType {
        QUALIFICATION,
        SHIFT,
        SITE,
        CARGO,
        COMPOSITE
    }

    public SalaryRule() {
    }

    public SalaryRule(String name, String conditionExpression, double bonusAmount,
                      Integer priority, LocalDateTime validFrom, LocalDateTime validTo,
                      boolean active, RuleType ruleType) {
        this(name, conditionExpression, BigDecimal.valueOf(bonusAmount).setScale(2, RoundingMode.HALF_UP), priority, validFrom, validTo, active, ruleType);
    }

    public SalaryRule(String name, String conditionExpression, BigDecimal bonusAmount,
                      Integer priority, LocalDateTime validFrom, LocalDateTime validTo,
                      boolean active, RuleType ruleType) {
        this.name = name;
        this.conditionExpression = conditionExpression;
        this.bonusAmount = bonusAmount;
        this.priority = priority;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.active = active;
        this.ruleType = ruleType;
    }


} 