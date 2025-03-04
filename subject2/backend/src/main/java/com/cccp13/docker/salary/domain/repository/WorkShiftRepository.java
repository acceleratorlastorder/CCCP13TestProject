package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.Docker;
import com.cccp13.docker.salary.domain.model.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    List<WorkShift> findByDockerOrderByStartTimeDesc(Docker docker);
}
