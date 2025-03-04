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
@Table(name = "work_sites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSite extends BaseEntity {

    @Column(nullable = false)
    private String type; // parc, barge, navire

    @Column(nullable = false)
    private String location;

    @Column(name = "risk_level")
    private Integer riskLevel;

    @Column(name = "is_active")
    private boolean active = true;

    @Version
    private Long version;
} 