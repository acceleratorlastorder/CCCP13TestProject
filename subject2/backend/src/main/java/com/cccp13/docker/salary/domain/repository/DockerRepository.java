package com.cccp13.docker.salary.domain.repository;

import com.cccp13.docker.salary.domain.model.Docker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerRepository extends JpaRepository<Docker, Long> {
}
