package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.Assignment;
import com.cccp13.docker.salary.domain.model.Docker;
import com.cccp13.docker.salary.domain.model.SalaryCalculation;
import com.cccp13.docker.salary.domain.model.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryCalculationRepository extends JpaRepository<SalaryCalculation, Long> {
    @Query("SELECT sc FROM SalaryCalculation sc WHERE sc.assignment.docker = :docker AND sc.assignment.workShift = :workShift")
    Optional<SalaryCalculation> findByDockerAndWorkShift(@Param("docker") Docker docker, @Param("workShift") WorkShift workShift);

    List<SalaryCalculation> findByAssignment_DockerOrderByCalculationDateDesc(Docker docker);

    boolean existsByAssignment(Assignment assignment);
} 