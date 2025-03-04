package com.cccp13.docker.salary.domain.dto;

public record SalaryCalculationDto(
        Long dockerId,
        Long workShiftId,
        Long workSiteId,
        Long cargoTypeId
) {}