package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.*;
import com.cccp13.docker.salary.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SalaryCalculationServiceIntegrationTest {
    @Autowired
    private SalaryCalculationRepository salaryCalculationRepository;
    @Autowired
    private DockerRepository dockerRepository;
    @Autowired
    private WorkShiftRepository workShiftRepository;
    @Autowired
    private WorkSiteRepository workSiteRepository;
    @Autowired
    private CargoTypeRepository cargoTypeRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SalaryCalculationService salaryCalculationService;

    private Assignment assignment;
    private Docker docker;
    private WorkShift workShift;
    private WorkSite workSite;
    private CargoType cargoType;

    @BeforeEach
    void setUp() {
        // Create and persist Docker
        docker = new Docker();
        docker.setQualification("contremaître");
        docker.setExperienceYears(5);
        docker.setBaseSalary(new BigDecimal("2000.00"));
        docker.setActive(true);
        docker = dockerRepository.save(docker);

        // Create and persist WorkShift
        workShift = new WorkShift();
        workShift.setDocker(docker);
        workShift.setType("nuit");
        workShift.setStartTime(LocalDateTime.now());
        workShift.setEndTime(LocalDateTime.now().plusHours(8));
        workShift.setWeekend(true);
        workShift.setHoliday(false);
        workShift = workShiftRepository.save(workShift);

        // Create and persist WorkSite
        workSite = new WorkSite();
        workSite.setType("navire");
        workSite.setLocation("Port de Marseille");
        workSite.setRiskLevel(3);
        workSite.setActive(true);
        workSite = workSiteRepository.save(workSite);

        // Create and persist CargoType
        cargoType = new CargoType();
        cargoType.setName("vrac");
        cargoType.setRiskLevel(2);
        cargoType.setHandlingDifficulty(3);
        cargoType.setActive(true);
        cargoType = cargoTypeRepository.save(cargoType);

        // Now create and persist Assignment
        assignment = new Assignment(docker, workShift, workSite, cargoType);
        assignment = assignmentRepository.save(assignment);
    }

    @Test
    void calculateSalary_ShouldCalculateCorrectly() {
        // Act
        SalaryCalculation result = salaryCalculationService.calculateSalary(assignment);
        Assignment resultAssignment = result.getAssignment();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(docker, resultAssignment.getDocker());
        assertEquals(workShift, resultAssignment.getWorkShift());
        assertEquals(workSite, resultAssignment.getWorkSite());
        assertEquals(cargoType, resultAssignment.getCargoType());
        assertEquals(docker.getBaseSalary(), result.getBaseSalary());
        System.out.println("Total Bonus: " + result.getTotalBonus());
        System.out.println("Applied Rules: " + result.getAppliedRules());
        assertTrue(result.getTotalBonus().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getTotalSalary().compareTo(result.getBaseSalary()) > 0);
        assertFalse(result.getAppliedRules().isEmpty());
    }

    @Test
    void getSalaryCalculation_ShouldReturnCalculation() {
        // Arrange
        SalaryCalculation savedCalculation = salaryCalculationService.calculateSalary(assignment);
        Assignment savedAssignment = savedCalculation.getAssignment();

        // Act
        SalaryCalculation result = salaryCalculationService.getSalaryCalculation(docker, workShift);
        Assignment resultAssignment = result.getAssignment();

        // Assert
        assertNotNull(result);
        assertEquals(savedCalculation.getId(), result.getId());
        assertEquals(savedAssignment.getDocker(), resultAssignment.getDocker());
        assertEquals(savedAssignment.getWorkShift(), resultAssignment.getWorkShift());
        assertEquals(savedCalculation.getBaseSalary(), result.getBaseSalary());
        assertEquals(savedCalculation.getTotalBonus(), result.getTotalBonus());
        assertEquals(savedCalculation.getTotalSalary(), result.getTotalSalary());
    }

    @Test
    void getDockerSalaryHistory_ShouldReturnCalculations() {
        // Arrange
        salaryCalculationService.calculateSalary(assignment);

        // Act
        List<SalaryCalculation> history = salaryCalculationService.getDockerSalaryHistory(docker);

        // Assert
        assertNotNull(history);
        assertFalse(history.isEmpty());
        assertTrue(history.stream().allMatch(calc -> calc.getAssignment().getDocker().getId().equals(docker.getId())));
    }

    @Test
    void calculateSalary_ShouldNotAllowDuplicateCalculation() {
        // Arrange
        salaryCalculationService.calculateSalary(assignment);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
            salaryCalculationService.calculateSalary(assignment)
        );
    }

    @Test
    void calculateSalary_WithDifferentQualifications_ShouldApplyDifferentBonuses() {
        // Arrange
        Docker livreur = new Docker();
        livreur.setQualification("livreur");
        livreur.setExperienceYears(3);
        livreur.setBaseSalary(new BigDecimal("1800.00"));
        livreur.setActive(true);

        livreur = dockerRepository.save(livreur);

        Assignment livreurAssignment = new Assignment(livreur, workShift, workSite, cargoType);
        livreurAssignment = assignmentRepository.save(livreurAssignment);

        // Act
        SalaryCalculation resultLivreur = salaryCalculationService.calculateSalary(livreurAssignment);
        SalaryCalculation resultContremaitre = salaryCalculationService.calculateSalary(assignment);

        // Assert
        assertNotEquals(resultLivreur.getTotalBonus(), resultContremaitre.getTotalBonus());
        assertTrue(resultContremaitre.getTotalBonus().compareTo(resultLivreur.getTotalBonus()) > 0);
    }

    @Test
    void calculateSalary_WithDifferentShiftTypes_ShouldApplyDifferentBonuses() {
        // Arrange
        WorkShift shiftJour = new WorkShift();
        shiftJour.setDocker(docker);
        shiftJour.setType("jour");
        shiftJour.setStartTime(LocalDateTime.now());
        shiftJour.setEndTime(LocalDateTime.now().plusHours(8));
        shiftJour.setWeekend(false);
        shiftJour.setHoliday(false);

        shiftJour = workShiftRepository.save(shiftJour);

        Assignment jourAssignment = new Assignment(docker, shiftJour, workSite, cargoType);
        jourAssignment = assignmentRepository.save(jourAssignment);

        // Act
        SalaryCalculation resultNuit = salaryCalculationService.calculateSalary(assignment);
        SalaryCalculation resultJour = salaryCalculationService.calculateSalary(jourAssignment);

        // Assert
        assertNotEquals(resultNuit.getTotalBonus(), resultJour.getTotalBonus());
        assertTrue(resultNuit.getTotalBonus().compareTo(resultJour.getTotalBonus()) > 0);
    }

    @Test
    void calculateSalary_WithDifferentRiskLevels_ShouldApplyDifferentBonuses() {
        // Arrange
        WorkSite siteRisque = new WorkSite();
        siteRisque.setType("navire");
        siteRisque.setLocation("Port de Marseille");
        siteRisque.setRiskLevel(5); // Higher risk level
        siteRisque.setActive(true);

        siteRisque = workSiteRepository.save(siteRisque);

        Assignment risqueAssignment = new Assignment(docker, workShift, siteRisque, cargoType);
        risqueAssignment = assignmentRepository.save(risqueAssignment);

        // Act
        SalaryCalculation resultRisque = salaryCalculationService.calculateSalary(risqueAssignment);
        SalaryCalculation resultNormal = salaryCalculationService.calculateSalary(assignment);

        // Debugging Logs
        System.out.println("--- Debugging Salary Calculation ---");
        System.out.println("Risk Level (Normal Site): " + assignment.getWorkSite().getRiskLevel());
        System.out.println("Risk Level (High Risk Site): " + risqueAssignment.getWorkSite().getRiskLevel());
        System.out.println("Total Bonus (Normal Site): " + resultNormal.getTotalBonus());
        System.out.println("Total Bonus (High Risk Site): " + resultRisque.getTotalBonus());
        System.out.println("Total Salary (Normal Site): " + resultNormal.getTotalSalary());
        System.out.println("Total Salary (High Risk Site): " + resultRisque.getTotalSalary());
        System.out.println("Applied Rules (Normal Site): " + resultNormal.getAppliedRules());
        System.out.println("Applied Rules (High Risk Site): " + resultRisque.getAppliedRules());

        // Assert
        assertNotEquals(resultRisque.getTotalBonus(), resultNormal.getTotalBonus(),
                "Expected different bonuses for different risk levels, but got the same");
        assertTrue(resultRisque.getTotalBonus().compareTo(resultNormal.getTotalBonus()) > 0,
                "High-risk site should have a higher bonus than normal site");
    }

    @Test
    void calculateSalary_WithHoliday_ShouldApplyAdditionalBonus() {
        // Arrange
        WorkShift shiftFerie = new WorkShift();
        shiftFerie.setDocker(docker);
        shiftFerie.setType("nuit");
        shiftFerie.setStartTime(LocalDateTime.now());
        shiftFerie.setEndTime(LocalDateTime.now().plusHours(8));
        shiftFerie.setWeekend(false);
        shiftFerie.setHoliday(true);

        shiftFerie = workShiftRepository.save(shiftFerie);

        Assignment ferieAssignment = new Assignment(docker, shiftFerie, workSite, cargoType);
        ferieAssignment = assignmentRepository.save(ferieAssignment);

        // Act
        SalaryCalculation resultFerie = salaryCalculationService.calculateSalary(ferieAssignment);
        salaryCalculationRepository.flush(); // Ensure DB write

        SalaryCalculation updatedResult = salaryCalculationRepository.findById(resultFerie.getId()).orElseThrow();

        // Assert
        assertNotEquals(resultFerie.getTotalBonus(), BigDecimal.ZERO, "Holiday Bonus should not be zero");
        assertTrue(updatedResult.getTotalBonus().compareTo(BigDecimal.ZERO) > 0, "Holiday Bonus should be applied");
    }


    @Test
    void calculateSalary_WithDifferentCargoTypes_ShouldApplyDifferentBonuses() {
        // Arrange
        CargoType cargoRisque = new CargoType();
        cargoRisque.setName("matières dangereuses");
        cargoRisque.setRiskLevel(5);
        cargoRisque.setHandlingDifficulty(5);
        cargoRisque.setActive(true);

        cargoRisque = cargoTypeRepository.save(cargoRisque);

        Assignment risqueAssignment = new Assignment(docker, workShift, workSite, cargoRisque);
        risqueAssignment = assignmentRepository.save(risqueAssignment);

        // Act
        SalaryCalculation resultRisque = salaryCalculationService.calculateSalary(risqueAssignment);
        SalaryCalculation resultNormal = salaryCalculationService.calculateSalary(assignment);

        // Assert
        assertNotEquals(resultRisque.getTotalBonus(), resultNormal.getTotalBonus());
        assertTrue(resultRisque.getTotalBonus().compareTo(resultNormal.getTotalBonus()) > 0);
    }

    @Test
    void calculateSalary_WithMultipleShifts_ShouldCalculateCorrectly() {
        // Arrange
        WorkShift shiftJour = new WorkShift();
        shiftJour.setDocker(docker);
        shiftJour.setType("jour");
        shiftJour.setStartTime(LocalDateTime.now());
        shiftJour.setEndTime(LocalDateTime.now().plusHours(8));
        shiftJour.setWeekend(false);
        shiftJour.setHoliday(false);

        shiftJour = workShiftRepository.save(shiftJour);

        Assignment jourAssignment = new Assignment(docker, shiftJour, workSite, cargoType);
        jourAssignment = assignmentRepository.save(jourAssignment);

        // Act
        SalaryCalculation resultNuit = salaryCalculationService.calculateSalary(assignment);
        SalaryCalculation resultJour = salaryCalculationService.calculateSalary(jourAssignment);

        // Assert
        assertNotNull(resultNuit);
        assertNotNull(resultJour);
        assertNotEquals(resultNuit.getId(), resultJour.getId());
        assertNotEquals(resultNuit.getTotalBonus(), resultJour.getTotalBonus());
    }

    @Test
    void getSalaryStatistics_ShouldCalculateCorrectly() {
        // Arrange
        SalaryCalculation calculation1 = salaryCalculationService.calculateSalary(assignment);
        
        // Create another calculation with different values
        Docker docker2 = new Docker();
        docker2.setQualification("livreur");
        docker2.setExperienceYears(3);
        docker2.setBaseSalary(new BigDecimal("1800.00"));
        docker2.setActive(true);

        docker2 = dockerRepository.save(docker2);

        WorkShift workShift2 = new WorkShift();
        workShift2.setDocker(docker2);
        workShift2.setType("jour");
        workShift2.setStartTime(LocalDateTime.now());
        workShift2.setEndTime(LocalDateTime.now().plusHours(8));
        workShift2.setWeekend(false);
        workShift2.setHoliday(false);

        workShift2 = workShiftRepository.save(workShift2);

        Assignment assignment2 = new Assignment(docker2, workShift2, workSite, cargoType);
        assignment2 = assignmentRepository.save(assignment2);


        SalaryCalculation calculation2 = salaryCalculationService.calculateSalary(assignment2);

        // Act
        SalaryStatistics stats = salaryCalculationService.getSalaryStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(2, stats.getTotalDockers());
        assertTrue(stats.getAverageSalary().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stats.getAverageBonus().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(stats.getAverageBaseSalary().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateSalary_WithMultipleShifts_ShouldCalculateCorrectlyBis() {
        // Arrange
        WorkShift shiftJour = new WorkShift();
        shiftJour.setDocker(docker);
        shiftJour.setType("jour");
        shiftJour.setStartTime(LocalDateTime.now());
        shiftJour.setEndTime(LocalDateTime.now().plusHours(8));
        shiftJour.setWeekend(false);
        shiftJour.setHoliday(false);

        shiftJour = workShiftRepository.save(shiftJour);

        Assignment jourAssignment = new Assignment(docker, shiftJour, workSite, cargoType);
        jourAssignment = assignmentRepository.save(jourAssignment);

        // Act
        SalaryCalculation resultNuit = salaryCalculationService.calculateSalary(assignment);
        SalaryCalculation resultJour = salaryCalculationService.calculateSalary(jourAssignment);

        // Assert
        assertNotNull(resultNuit);
        assertNotNull(resultJour);
        assertNotEquals(resultNuit.getTotalBonus(), resultJour.getTotalBonus());
        assertTrue(resultNuit.getTotalBonus().compareTo(resultJour.getTotalBonus()) > 0);
    }
}