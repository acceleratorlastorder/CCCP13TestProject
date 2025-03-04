package com.cccp13.docker.salary.domain.mapper;

import com.cccp13.docker.salary.domain.dto.SalaryCalculationDto;
import com.cccp13.docker.salary.domain.model.Assignment;
import com.cccp13.docker.salary.domain.model.SalaryCalculation;
import com.cccp13.docker.salary.domain.repository.CargoTypeRepository;
import com.cccp13.docker.salary.domain.repository.DockerRepository;
import com.cccp13.docker.salary.domain.repository.WorkShiftRepository;
import com.cccp13.docker.salary.domain.repository.WorkSiteRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SalaryCalculationMapper {

    @Autowired
    protected DockerRepository dockerRepository;

    @Autowired
    protected WorkShiftRepository workShiftRepository;

    @Autowired
    protected WorkSiteRepository workSiteRepository;

    @Autowired
    protected CargoTypeRepository cargoTypeRepository;

    // Mapping from DTO to SalaryCalculation.
    // We build the assignment and set it on SalaryCalculation.
    @Mapping(target = "assignment", expression = "java(toAssignment(request))")
    public abstract SalaryCalculation toSalaryCalculation(SalaryCalculationDto request);

    // Mapping from SalaryCalculation to DTO (using the assignment)
    @Mapping(target = "dockerId", source = "assignment.docker.id")
    @Mapping(target = "workShiftId", source = "assignment.workShift.id")
    @Mapping(target = "workSiteId", source = "assignment.workSite.id")
    @Mapping(target = "cargoTypeId", source = "assignment.cargoType.id")
    public abstract SalaryCalculationDto toSalaryCalculationDto(SalaryCalculation salaryCalculation);

    // Helper method to build an Assignment from the DTO.
    protected Assignment toAssignment(SalaryCalculationDto request) {
        Assignment assignment = new Assignment();
        if (request.dockerId() != null) {
            assignment.setDocker(dockerRepository.findById(request.dockerId()).orElse(null));
        }
        if (request.workShiftId() != null) {
            assignment.setWorkShift(workShiftRepository.findById(request.workShiftId()).orElse(null));
        }
        if (request.workSiteId() != null) {
            assignment.setWorkSite(workSiteRepository.findById(request.workSiteId()).orElse(null));
        }
        if (request.cargoTypeId() != null) {
            assignment.setCargoType(cargoTypeRepository.findById(request.cargoTypeId()).orElse(null));
        }
        return assignment;
    }
}
