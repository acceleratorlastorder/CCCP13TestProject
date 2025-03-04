package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.*;
import com.cccp13.docker.salary.domain.repository.AssignmentRepository;
import com.cccp13.docker.salary.domain.repository.DockerRepository;
import com.cccp13.docker.salary.domain.repository.SalaryCalculationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryCalculationServiceTest {

    @Mock
    private RuleEvaluationService ruleEvaluationService;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private SalaryCalculationRepository salaryCalculationRepository;
    @Mock
    private DockerRepository dockerRepository;
    @InjectMocks
    private SalaryCalculationService salaryCalculationService;

    private Assignment assignment;
    private Docker docker;

    private WorkShift workShift;
    private WorkSite workSite;
    private CargoType cargoType;
    private List<SalaryRule> applicableRules;

    @BeforeEach
    void setUp() {
        docker = new Docker();
        docker.setId(1L);
        docker.setQualification("contremaître");
        docker.setExperienceYears(5);
        docker.setBaseSalary(new BigDecimal("2000.00"));

        workShift = new WorkShift();
        workShift.setId(1L);
        workShift.setDocker(docker);
        workShift.setType("nuit");
        workShift.setStartTime(LocalDateTime.now());
        workShift.setEndTime(LocalDateTime.now().plusHours(8));
        workShift.setWeekend(true);

        workSite = new WorkSite();
        workSite.setId(1L);
        workSite.setType("navire");
        workSite.setLocation("Port de Marseille");
        workSite.setRiskLevel(3);

        cargoType = new CargoType();
        cargoType.setId(1L);
        cargoType.setName("vrac");
        cargoType.setRiskLevel(2);
        cargoType.setHandlingDifficulty(3);

        assignment = new Assignment(docker, workShift, workSite, cargoType);

        applicableRules = Arrays.asList(createRule("Prime Contremaître", BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP), 1), createRule("Prime Nuit", BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP), 2), createRule("Prime Week-end", BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP), 2), createRule("Prime Navire", BigDecimal.valueOf(120).setScale(2, RoundingMode.HALF_UP), 3), createRule("Prime Cargo Vrac", BigDecimal.valueOf(90).setScale(2, RoundingMode.HALF_UP), 4), createRule("Prime Expérience", BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP), 5));
    }

    @Test
    void calculateSalary_ShouldCalculateCorrectly() {
        // Arrange
        when(salaryCalculationRepository.existsByAssignment(assignment)).thenReturn(false);
        when(ruleEvaluationService.evaluateRules(any())).thenReturn(applicableRules);
        when(salaryCalculationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        SalaryCalculation result = salaryCalculationService.calculateSalary(assignment);

        // Assert
        assertNotNull(result);
        assertEquals(docker, result.getAssignment().getDocker());
        assertEquals(workShift, result.getAssignment().getWorkShift());
        assertEquals(workSite, result.getAssignment().getWorkSite());
        assertEquals(cargoType, result.getAssignment().getCargoType());
        assertEquals(docker.getBaseSalary(), result.getBaseSalary());
        assertEquals(new BigDecimal("710.00"), result.getTotalBonus());
        assertEquals(new BigDecimal("2710.00"), result.getTotalSalary());
        assertEquals(6, result.getAppliedRules().size());

        verify(salaryCalculationRepository).existsByAssignment(assignment);
        verify(ruleEvaluationService).evaluateRules(any());
        verify(salaryCalculationRepository).save(any());
    }

    @Test
    void calculateSalary_ShouldThrowException_WhenCalculationExists() {
        // Arrange
        when(salaryCalculationRepository.existsByAssignment(assignment)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> salaryCalculationService.calculateSalary(assignment));

        verify(salaryCalculationRepository).existsByAssignment(assignment);
        verifyNoInteractions(ruleEvaluationService);
        verifyNoMoreInteractions(salaryCalculationRepository);
    }

    @Test
    void getSalaryCalculation_ShouldReturnCalculation() {
        // Arrange
        SalaryCalculation expectedCalculation = new SalaryCalculation(assignment);
        expectedCalculation.setId(1L);
        expectedCalculation.setBaseSalary(new BigDecimal("2000.00"));
        expectedCalculation.setTotalBonus(new BigDecimal("710.00"));
        expectedCalculation.setTotalSalary(new BigDecimal("2710.00"));

        when(salaryCalculationRepository.findByDockerAndWorkShift(docker, workShift)).thenReturn(java.util.Optional.of(expectedCalculation));

        // Act
        SalaryCalculation result = salaryCalculationService.getSalaryCalculation(docker, workShift);

        // Assert
        assertNotNull(result);
        assertEquals(expectedCalculation.getId(), result.getId());
        assertEquals(expectedCalculation.getAssignment().getDocker(), result.getAssignment().getDocker());
        assertEquals(expectedCalculation.getAssignment().getWorkShift(), result.getAssignment().getWorkShift());
        assertEquals(expectedCalculation.getBaseSalary(), result.getBaseSalary());
        assertEquals(expectedCalculation.getTotalBonus(), result.getTotalBonus());
        assertEquals(expectedCalculation.getTotalSalary(), result.getTotalSalary());

        verify(salaryCalculationRepository).findByDockerAndWorkShift(docker, workShift);
    }

    @Test
    void getSalaryCalculation_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(salaryCalculationRepository.findByDockerAndWorkShift(docker, workShift)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> salaryCalculationService.getSalaryCalculation(docker, workShift));

        verify(salaryCalculationRepository).findByDockerAndWorkShift(docker, workShift);
        verifyNoMoreInteractions(salaryCalculationRepository);
    }

    @Test
    void getSalaryStatistics_ShouldCalculateCorrectly() {
        // Arrange
        List<SalaryCalculation> calculations = Arrays.asList(createCalculation(new BigDecimal("2000.00"), new BigDecimal("500.00")), createCalculation(new BigDecimal("2500.00"), new BigDecimal("750.00")), createCalculation(new BigDecimal("1800.00"), new BigDecimal("300.00")));
        when(salaryCalculationRepository.findAll()).thenReturn(calculations);
        when(dockerRepository.count()).thenReturn(3L);

        // Act
        SalaryStatistics stats = salaryCalculationService.getSalaryStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(3, stats.getTotalDockers());
        assertEquals(new BigDecimal("2616.67"), stats.getAverageSalary());
        assertEquals(new BigDecimal("516.67"), stats.getAverageBonus());
        assertEquals(new BigDecimal("2100.00"), stats.getAverageBaseSalary());
    }

    @Test
    void calculateAllSalaries_ShouldCalculateForAllAssignments() {
        // Arrange
        when(assignmentRepository.findAll()).thenReturn(Collections.singletonList(assignment));
        when(salaryCalculationRepository.existsByAssignment(any())).thenReturn(false);
        when(ruleEvaluationService.evaluateRules(any())).thenReturn(applicableRules);
        when(salaryCalculationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        salaryCalculationService.calculateAllSalaries();

        // Assert
        verify(salaryCalculationRepository, times(2)).existsByAssignment(any());
        verify(ruleEvaluationService).evaluateRules(any());
        verify(salaryCalculationRepository).save(any());
    }

    private SalaryRule createRule(String name, BigDecimal bonusAmount, Integer priority) {
        SalaryRule rule = new SalaryRule();
        rule.setName(name);
        rule.setBonusAmount(bonusAmount);
        rule.setPriority(priority);
        rule.setActive(true);
        return rule;
    }

    private SalaryCalculation createCalculation(BigDecimal baseSalary, BigDecimal bonus) {
        SalaryCalculation calc = new SalaryCalculation(assignment);
        calc.setBaseSalary(baseSalary);
        calc.setTotalBonus(bonus);
        calc.setTotalSalary(baseSalary.add(bonus));
        return calc;
    }
} 