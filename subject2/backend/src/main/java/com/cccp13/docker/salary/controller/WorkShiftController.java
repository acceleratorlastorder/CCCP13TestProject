package com.cccp13.docker.salary.controller;

import com.cccp13.docker.salary.domain.model.WorkShift;
import com.cccp13.docker.salary.domain.service.WorkShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @GetMapping
    public ResponseEntity<List<WorkShift>> getAllWorkShifts() {
        return ResponseEntity.ok(workShiftService.getAllWorkShifts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkShift> getWorkShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(workShiftService.getWorkShiftById(id));
    }

    @PostMapping
    public ResponseEntity<WorkShift> createWorkShift(@RequestBody WorkShift workShift) {
        return ResponseEntity.ok(workShiftService.createWorkShift(workShift));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkShift> updateWorkShift(@PathVariable Long id, @RequestBody WorkShift workShift) {
        return ResponseEntity.ok(workShiftService.updateWorkShift(id, workShift));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkShift(@PathVariable Long id) {
        workShiftService.deleteWorkShift(id);
        return ResponseEntity.ok().build();
    }
} 