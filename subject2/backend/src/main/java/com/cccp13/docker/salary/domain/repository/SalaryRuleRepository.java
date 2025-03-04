package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.SalaryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryRuleRepository extends JpaRepository<SalaryRule, Long> {
    List<SalaryRule> findByActiveTrueAndValidFromBeforeAndValidToAfter(
            LocalDateTime validFrom, LocalDateTime validTo);
} 