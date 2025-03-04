package com.cccp13.docker.salary.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalaryStatisticsDto {
    private Integer totalDockers;
    private BigDecimal averageSalary;
    private BigDecimal totalBonuses;
    private Integer calculationsThisMonth;
    private BigDecimal averageBaseSalary;
} 