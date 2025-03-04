package com.cccp13.docker.salary.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "salary_calculations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalaryCalculation extends BaseEntity {

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "assignment_id")
    @JsonManagedReference
    private Assignment assignment;

    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @Column(name = "total_bonus")
    private BigDecimal totalBonus = BigDecimal.ZERO;

    @Column(name = "total_salary", nullable = false)
    private BigDecimal totalSalary;

    @Column(name = "calculation_date", nullable = false)
    private LocalDateTime calculationDate;

    @OneToMany(mappedBy = "salaryCalculation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AppliedRule> appliedRules = new ArrayList<>();

    @Version
    private Long version;

    public SalaryCalculation(Assignment assignment) {
        this.assignment = assignment;
        this.baseSalary = assignment.getDocker().getBaseSalary();
    }

    @Entity
    @Table(name = "applied_rules")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = "salaryCalculation")
    public static class AppliedRule {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "salary_calculation_id", nullable = false)
        @JsonBackReference
        private SalaryCalculation salaryCalculation;

        @ManyToOne
        @JoinColumn(name = "salary_rule_id", nullable = false)
        private SalaryRule salaryRule;

        @Column(name = "bonus_amount", nullable = false)
        private BigDecimal bonusAmount;

        @Column(name = "rule_name", nullable = false)
        private String ruleName;
    }
} 