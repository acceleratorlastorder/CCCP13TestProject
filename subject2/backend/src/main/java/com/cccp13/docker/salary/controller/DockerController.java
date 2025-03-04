package com.cccp13.docker.salary.controller;

import com.cccp13.docker.salary.domain.model.Docker;
import com.cccp13.docker.salary.domain.service.DockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dockers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DockerController {

    private final DockerService dockerService;

    @GetMapping
    public ResponseEntity<List<Docker>> getAllDockers() {
        return ResponseEntity.ok(dockerService.getAllDockers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docker> getDockerById(@PathVariable Long id) {
        return ResponseEntity.ok(dockerService.getDockerById(id));
    }

    @PostMapping
    public ResponseEntity<Docker> createDocker(@RequestBody Docker docker) {
        return ResponseEntity.ok(dockerService.createDocker(docker));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Docker> updateDocker(@PathVariable Long id, @RequestBody Docker docker) {
        return ResponseEntity.ok(dockerService.updateDocker(id, docker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocker(@PathVariable Long id) {
        dockerService.deleteDocker(id);
        return ResponseEntity.ok().build();
    }
} 