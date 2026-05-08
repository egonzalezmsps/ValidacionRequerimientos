package com.coppel.sla.mappers.impl;

import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.entities.ApiConfigEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public ApiResponseDto toDto(ApiConfigEntity entity) {
        return new ApiResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getCountry(),
                entity.getStatus(),
                Boolean.TRUE.equals(entity.getMonitored()),
                entity.getLastChecked()
        );
    }
}
