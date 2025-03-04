package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.Docker;
import com.cccp13.docker.salary.domain.repository.DockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DockerService {

    @Autowired
    private final DockerRepository dockerRepository;

    @Transactional(readOnly = true)
    public List<Docker> getAllDockers() {
        return dockerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Docker getDockerById(Long id) {
        return dockerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docker not found with id: " + id));
    }

    @Transactional
    public Docker createDocker(Docker docker) {
        return dockerRepository.save(docker);
    }

    @Transactional
    public Docker updateDocker(Long id, Docker docker) {
        Docker existingDocker = getDockerById(id);
        existingDocker.setQualification(docker.getQualification());
        existingDocker.setExperienceYears(docker.getExperienceYears());
        existingDocker.setBaseSalary(docker.getBaseSalary());
        existingDocker.setActive(docker.isActive());
        return dockerRepository.save(existingDocker);
    }

    @Transactional
    public void deleteDocker(Long id) {
        dockerRepository.deleteById(id);
    }
} 