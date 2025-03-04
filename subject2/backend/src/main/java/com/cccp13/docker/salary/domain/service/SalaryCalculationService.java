package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.*;
import com.cccp13.docker.salary.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryCalculationService {
    @Autowired
    private final AssignmentRepository assignmentRepository;
    @Autowired
    private final SalaryCalculationRepository salaryCalculationRepository;
    @Autowired
    private final DockerRepository dockerRepository;

    @Autowired
    private final RuleEvaluationService ruleEvaluationService;

    @Transactional
    public SalaryCalculation calculateSalary(Assignment assignment) {
        log.info("Calculating salary for assignment with dockerId: {} and workShiftId: {}",
                assignment.getDocker().getId(), assignment.getWorkShift().getId());

        if (salaryCalculationRepository.existsByAssignment(assignment)) {
            throw new IllegalStateException("Un calcul de salaire existe déjà pour cet assignement");
        }

        SalaryCalculation calculation = new SalaryCalculation(assignment);
        calculation.setCalculationDate(LocalDateTime.now());

        List<SalaryRule> applicableRules = ruleEvaluationService.evaluateRules(calculation);
        BigDecimal bonusSum = BigDecimal.ZERO;

        for (SalaryRule rule : applicableRules) {
            SalaryCalculation.AppliedRule appliedRule = new SalaryCalculation.AppliedRule();
            appliedRule.setSalaryCalculation(calculation);
            appliedRule.setSalaryRule(rule);
            appliedRule.setBonusAmount(rule.getBonusAmount());
            appliedRule.setRuleName(rule.getName());
            calculation.getAppliedRules().add(appliedRule);
            bonusSum = bonusSum.add(rule.getBonusAmount());
        }

        calculation.setTotalBonus(bonusSum);
        calculation.setTotalSalary(calculation.getBaseSalary().add(bonusSum));

        calculation = salaryCalculationRepository.save(calculation);
        salaryCalculationRepository.flush(); // Ensure immediate database write

        log.info("Salary Calculation Persisted: {}", calculation.getTotalBonus());
        return calculation;
    }


    @Cacheable(value = "salaryCalculations", key = "#docker.id + '-' + #workShift.id")
    public SalaryCalculation getSalaryCalculation(Docker docker, WorkShift workShift) {
        return salaryCalculationRepository.findByDockerAndWorkShift(docker, workShift)
                .orElseThrow(() -> new EntityNotFoundException("Aucun calcul de salaire trouvé pour ce docker et ce shift"));
    }

    public List<SalaryCalculation> getDockerSalaryHistorybyId(long dockerId) {
        Docker docker = fetchEntity(dockerRepository, dockerId, "Docker");
        return getDockerSalaryHistory(docker);
    }

    public List<SalaryCalculation> getDockerSalaryHistory(Docker docker) {
        return salaryCalculationRepository.findByAssignment_DockerOrderByCalculationDateDesc(docker);
    }

    @Transactional(readOnly = true)
    public List<SalaryCalculation> getAllCalculations() {
        return salaryCalculationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SalaryCalculation getSalaryCalculationById(Long id) {
        return salaryCalculationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salary calculation not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public SalaryStatistics getSalaryStatistics() {
        List<SalaryCalculation> allCalculations = salaryCalculationRepository.findAll();

        return SalaryStatistics.builder()
                .averageSalary(computeAverage(allCalculations, SalaryCalculation::getTotalSalary))
                .totalPayroll(computeTotal(allCalculations, SalaryCalculation::getTotalSalary))
                .averageBonus(computeAverage(allCalculations, SalaryCalculation::getTotalBonus))
                .highestSalary(findExtremum(allCalculations, SalaryCalculation::getTotalSalary, true))
                .lowestSalary(findExtremum(allCalculations, SalaryCalculation::getTotalSalary, false))
                .totalCalculations(allCalculations.size())
                .totalDockers((int) dockerRepository.count())
                .averageBaseSalary(computeAverage(allCalculations, SalaryCalculation::getBaseSalary))
                .averageExperienceYears(computeAverage(allCalculations, calc -> BigDecimal.valueOf(calc.getAssignment().getDocker().getExperienceYears())))
                .averageRiskLevel(computeAverage(allCalculations, calc -> BigDecimal.valueOf(calc.getAssignment().getWorkSite().getRiskLevel())))
                .build();
    }

    private BigDecimal computeAverage(List<SalaryCalculation> calculations, Function<SalaryCalculation, BigDecimal> mapper) {
        if (calculations.isEmpty()) return BigDecimal.ZERO;
        return calculations.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(calculations.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeTotal(List<SalaryCalculation> calculations, Function<SalaryCalculation, BigDecimal> mapper) {
        if (calculations.isEmpty()) return BigDecimal.ZERO;
        return calculations.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal findExtremum(List<SalaryCalculation> calculations, Function<SalaryCalculation, BigDecimal> mapper, boolean findMax) {
        if (calculations.isEmpty()) return BigDecimal.ZERO;
        return calculations.stream().map(mapper).reduce((findMax ? BigDecimal::max : BigDecimal::min)).orElse(BigDecimal.ZERO);
    }

    private <T> T fetchEntity(JpaRepository<T, Long> repository, Long id, String entityName) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(entityName + " not found with id: " + id));
    }

    @Transactional
    public void calculateAllSalaries() {
        log.info("Starting calculation of all salaries");
        List<Assignment> assignmentList = assignmentRepository.findAll();
        for (Assignment assignment : assignmentList) {
            if (!salaryCalculationRepository.existsByAssignment(assignment)) {
                calculateSalary(assignment);
            }
        }
        log.info("Completed calculation of all salaries");
    }
}
