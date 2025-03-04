package com.cccp13.docker.salary.domain.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "docker_id")
    private Docker docker;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_shift_id")
    private WorkShift workShift;

    @ManyToOne(optional = false)
    @JoinColumn(name = "work_site_id")
    private WorkSite workSite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cargo_type_id")
    private CargoType cargoType;

    @Column(name = "validated")
    private boolean validated = false;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @OneToOne(mappedBy = "assignment", cascade = CascadeType.ALL)
    @JsonBackReference
    private SalaryCalculation salaryCalculation;

    public Assignment(Docker docker, WorkShift workShift, WorkSite workSite, CargoType cargoType) {
        this.docker = docker;
        this.workShift = workShift;
        this.workSite = workSite;
        this.cargoType = cargoType;

    }
}
