package com.cccp13.docker.salary.domain.service;

import com.cccp13.docker.salary.domain.model.Assignment;
import com.cccp13.docker.salary.domain.model.CargoType;
import com.cccp13.docker.salary.domain.model.WorkShift;
import com.cccp13.docker.salary.domain.model.WorkSite;
import com.cccp13.docker.salary.domain.repository.CargoTypeRepository;
import com.cccp13.docker.salary.domain.repository.WorkShiftRepository;
import com.cccp13.docker.salary.domain.repository.AssignmentRepository;
import com.cccp13.docker.salary.domain.repository.WorkSiteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkShiftValidationService {
    @Autowired
    private final WorkShiftRepository workShiftRepository;
    @Autowired
    private final AssignmentRepository assignmentRepository;
    @Autowired
    private final WorkSiteRepository workSiteRepository;
    @Autowired
    private final CargoTypeRepository cargoTypeRepository;

    @Transactional
    public void validateWorkShift(Long workShiftId) {
        WorkShift workShift = workShiftRepository.findById(workShiftId)
            .orElseThrow(() -> new EntityNotFoundException("WorkShift not found with id: " + workShiftId));

        // Créer un nouvel assignment
        Assignment assignment = new Assignment();
        assignment.setDocker(workShift.getDocker());
        assignment.setWorkShift(workShift);


        // Pour les besoins de la démo, on va créer un WorkSite et un CargoType
        // Create and persist WorkSite
        WorkSite workSite = new WorkSite();
        workSite.setType("navire");
        workSite.setLocation("Port de Marseille");
        workSite.setRiskLevel(3);
        workSite.setActive(true);
        workSite = workSiteRepository.save(workSite);

        // Create and persist CargoType
        CargoType cargoType = new CargoType();
        cargoType.setName("vrac");
        cargoType.setRiskLevel(2);
        cargoType.setHandlingDifficulty(3);
        cargoType.setActive(true);
        cargoType = cargoTypeRepository.save(cargoType);

        assignment.setWorkSite(workSite);
        assignment.setCargoType(cargoType);
        assignment.setValidated(true);
        assignment.setValidationDate(LocalDateTime.now());

        assignmentRepository.save(assignment);

        // Marquer le work shift comme validé
        workShift.setValidated(true);
        workShiftRepository.save(workShift);
    }
} 