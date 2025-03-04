package com.cccp13.docker.salary.controller;

import com.cccp13.docker.salary.domain.service.WorkShiftValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-shifts")
@RequiredArgsConstructor
public class WorkShiftValidationController {

    private final WorkShiftValidationService workShiftValidationService;

    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validateWorkShift(@PathVariable Long id) {
        workShiftValidationService.validateWorkShift(id);
        return ResponseEntity.ok().build();
    }
} 