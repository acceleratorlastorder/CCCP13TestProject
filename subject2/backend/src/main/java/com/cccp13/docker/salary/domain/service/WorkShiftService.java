package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.Docker;
import com.cccp13.docker.salary.domain.model.WorkShift;
import com.cccp13.docker.salary.domain.repository.DockerRepository;
import com.cccp13.docker.salary.domain.repository.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkShiftService {

    @Autowired
    private final WorkShiftRepository workShiftRepository;
    @Autowired
    private final DockerRepository dockerRepository;

    @Transactional(readOnly = true)
    public List<WorkShift> getAllWorkShifts() {
        return workShiftRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkShift getWorkShiftById(Long id) {
        return workShiftRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("WorkShift not found with id: " + id));
    }

    @Transactional
    public WorkShift createWorkShift(WorkShift workShift) {
        // Vérifier que le docker existe
        Docker docker = dockerRepository.findById(workShift.getDocker().getId())
            .orElseThrow(() -> new RuntimeException("Docker not found with id: " + workShift.getDocker().getId()));
        
        // Set the docker on the work shift
        workShift.setDocker(docker);

        // Vérifier que les dates sont valides
        LocalDateTime startTime = workShift.getStartTime();
        LocalDateTime endTime = workShift.getEndTime();
        
        if (endTime.isBefore(workShift.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Vérifier qu'il n'y a pas de chevauchement avec d'autres shifts du même docker
        List<WorkShift> existingShifts = workShiftRepository.findByDockerOrderByStartTimeDesc(workShift.getDocker());
        for (WorkShift existingShift : existingShifts) {
            if ((startTime.isBefore(existingShift.getEndTime()) && endTime.isAfter(existingShift.getStartTime()))) {
                throw new IllegalStateException("Work shift overlaps with existing shift");
            }
        }

        return workShiftRepository.save(workShift);
    }

    @Transactional
    public WorkShift updateWorkShift(Long id, WorkShift workShift) {
        // Retrieve the existing work shift by ID
        WorkShift existingShift = getWorkShiftById(id);

        // Validate if the Docker is changed and exists
        Long newDockerId = workShift.getDocker().getId();
        Long currentDockerId = existingShift.getDocker() != null ? existingShift.getDocker().getId() : null;

        if (!newDockerId.equals(currentDockerId)) {
            dockerRepository.findById(newDockerId)
                    .orElseThrow(() -> new RuntimeException("Docker not found with id: " + newDockerId));
        }

        // Validate start and end times
        LocalDateTime startTime = workShift.getStartTime();
        LocalDateTime endTime = workShift.getEndTime();

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping shifts for the same Docker (excluding the current shift)
        List<WorkShift> existingShifts = workShiftRepository.findByDockerOrderByStartTimeDesc(workShift.getDocker());

        for (WorkShift otherShift : existingShifts) {
            if (!otherShift.getId().equals(id)) { // Exclude the shift being updated
                LocalDateTime otherStart = otherShift.getStartTime();
                LocalDateTime otherEnd = otherShift.getEndTime();

                if (startTime.isBefore(otherEnd) && endTime.isAfter(otherStart)) {
                    throw new IllegalStateException("Work shift overlaps with existing shift");
                }
            }
        }

        // Update the shift details
        existingShift.setDocker(workShift.getDocker());
        existingShift.setType(workShift.getType());
        existingShift.setStartTime(startTime);
        existingShift.setEndTime(endTime);
        existingShift.setWeekend(workShift.isWeekend());
        existingShift.setHoliday(workShift.isHoliday());

        // Save and return the updated work shift
        return workShiftRepository.save(existingShift);
    }

    @Transactional
    public void deleteWorkShift(Long id) {
        workShiftRepository.deleteById(id);
    }
} 