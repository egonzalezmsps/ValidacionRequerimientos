package com.coppel.sla.mappers;

import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
import com.coppel.sla.entities.MonitoringConfigEntity;

public interface MonitoringMapper {
     MonitoringConfigEntity toEntity(
            MonitoringRequestDto request
    );
     MonitoringResponseDto toDto(
            MonitoringConfigEntity entity
    );
}
