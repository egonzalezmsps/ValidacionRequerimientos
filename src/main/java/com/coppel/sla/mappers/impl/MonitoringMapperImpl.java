package com.coppel.sla.mappers.impl;

import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.monitory.MonitoringDetailDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
import com.coppel.sla.dto.monitory.ScheduleDto;
import com.coppel.sla.entities.MonitoringApiEntity;
import com.coppel.sla.entities.MonitoringConfigEntity;
import com.coppel.sla.mappers.MonitoringMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class MonitoringMapperImpl implements MonitoringMapper {


    @Override
    public MonitoringConfigEntity toEntity(
            MonitoringRequestDto request
    ) {

        MonitoringConfigEntity entity =
                new MonitoringConfigEntity();

        entity.setId(UUID.randomUUID().toString());
        entity.setStatus("scheduled");
        entity.setCreatedAt(Instant.now());

        entity.setScheduleType(
                request.schedule().type()
        );

        entity.setCronExpression(
                request.schedule().expression()
        );

        List<MonitoringApiEntity> apis =
                request.apis()
                        .stream()
                        .map(api -> {

                            MonitoringApiEntity apiEntity =
                                    new MonitoringApiEntity();

                            apiEntity.setApiId(api.apiId());
                            apiEntity.setTier(api.tier());
                            apiEntity.setMonitoring(entity);

                            return apiEntity;
                        })
                        .toList();

        entity.setApis(apis);

        return entity;
    }


    @Override
    public MonitoringResponseDto toDto(
            MonitoringConfigEntity entity
    ) {

        List<MonitoringDetailDto> details =
                entity.getApis()
                        .stream()
                        .map(api -> new MonitoringDetailDto(
                                api.getApiId(),
                                api.getTier(),
                                "configured",
                                null
                        ))
                        .toList();

        return new MonitoringResponseDto(
                entity.getId(),
                entity.getStatus(),
                details.size(),
                new ScheduleDto(
                        entity.getScheduleType(),
                        entity.getCronExpression()
                ),
                entity.getCreatedAt(),
                details
        );
    }
}
