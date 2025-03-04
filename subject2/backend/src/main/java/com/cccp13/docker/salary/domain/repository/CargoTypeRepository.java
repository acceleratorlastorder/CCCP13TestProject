package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.CargoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoTypeRepository extends JpaRepository<CargoType, Long> {
}
