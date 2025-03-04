package com.cccp13.docker.salary.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dockers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docker extends BaseEntity {

    @Column(nullable = false)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "is_active")
    private boolean active = true;

    @Version
    private Long version;
} 