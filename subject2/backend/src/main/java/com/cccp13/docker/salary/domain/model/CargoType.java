package com.cccp13.docker.salary.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cargo_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoType extends BaseEntity {

    @Column(nullable = false)
    private String name; // colis, conteneurs, vrac, voitures

    @Column(name = "risk_level")
    private Integer riskLevel;

    @Column(name = "handling_difficulty")
    private Integer handlingDifficulty;

    @Column(name = "is_active")
    private boolean active = true;

    @Version
    private Long version;
} 