package com.cccp13.docker.salary.controller;

import com.cccp13.docker.salary.domain.dto.SalaryStatisticsDto;
import com.cccp13.docker.salary.domain.model.SalaryCalculation;
import com.cccp13.docker.salary.domain.model.SalaryStatistics;
import com.cccp13.docker.salary.domain.service.SalaryCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-calculations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Angular default port
public class SalaryCalculationController {

    private final SalaryCalculationService salaryCalculationService;

    @GetMapping
    public ResponseEntity<List<SalaryCalculation>> getAllCalculations() {
        return ResponseEntity.ok(salaryCalculationService.getAllCalculations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryCalculation> getCalculationById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryCalculationService.getSalaryCalculationById(id));
    }

    @GetMapping("/docker/{dockerId}")
    public ResponseEntity<List<SalaryCalculation>> getDockerCalculations(@PathVariable Long dockerId) {
        return ResponseEntity.ok(salaryCalculationService.getDockerSalaryHistorybyId(dockerId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<SalaryStatisticsDto> getStatistics() {
        SalaryStatistics stats = salaryCalculationService.getSalaryStatistics();
        return ResponseEntity.ok(SalaryStatisticsDto.builder()
                .totalDockers(stats.getTotalDockers())
                .averageSalary(stats.getAverageSalary())
                .totalBonuses(stats.getTotalPayroll())
                .calculationsThisMonth(stats.getTotalCalculations())
                .averageBaseSalary(stats.getAverageBaseSalary())
                .build());
    }

    @PostMapping("/calculate-all")
    public ResponseEntity<Void> calculateAllSalaries() {
        salaryCalculationService.calculateAllSalaries();
        return ResponseEntity.ok().build();
    }
}

