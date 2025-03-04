package com.cccp13.docker.salary.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "work_shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkShift extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "docker_id", nullable = false)
    private Docker docker;

    @Column(nullable = false)
    private String type; // jour, nuit, week-end

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_holiday")
    private boolean holiday = false;

    @Column(name = "is_weekend")
    private boolean weekend = false;

    @Column(name = "validated")
    private boolean validated = false;

    @Version
    private Long version;
} 