package com.cccp13.docker.salary.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalaryStatistics {
    private BigDecimal averageSalary;
    private BigDecimal totalPayroll;
    private BigDecimal averageBonus;
    private BigDecimal highestSalary;
    private BigDecimal lowestSalary;
    private Integer totalCalculations;
    private Integer totalDockers;
    private BigDecimal averageExperienceYears;
    private BigDecimal averageRiskLevel;
    private BigDecimal averageBaseSalary;
} 