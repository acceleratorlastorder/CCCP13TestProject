package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Find assignments by docker ID
    List<Assignment> findByDockerId(Long dockerId);

    // Find assignments by work site ID
    List<Assignment> findByWorkSiteId(Long workSiteId);

    // Find assignments by cargo type ID
    List<Assignment> findByCargoTypeId(Long cargoTypeId);
}