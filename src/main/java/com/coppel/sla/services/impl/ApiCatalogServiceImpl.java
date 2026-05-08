package com.coppel.sla.services.impl;

import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.component.DatadogClient;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
import com.coppel.sla.entities.MonitoringConfigEntity;
import com.coppel.sla.mappers.MonitoringMapper;
import com.coppel.sla.mappers.impl.ApiMapper;
import com.coppel.sla.repositories.ApiConfigRepository;
import com.coppel.sla.repositories.MonitoringRepository;
import com.coppel.sla.services.ApiCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ApiCatalogServiceImpl implements ApiCatalogService {

    private final DatadogClient datadogClient;
    private final ApiConfigRepository repository;
    private final ApiMapper mapper;
    private final MonitoringRepository monitoringRepository;
    private final MonitoringMapper monitoringMapper;


    @Override
    public List<ApiResponseDto> getApis() {

        return datadogClient.listApis()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public MonitoringResponseDto createMonitoring(
            MonitoringRequestDto request
    ) {

        MonitoringConfigEntity entity =
                monitoringMapper.toEntity(request);

        MonitoringConfigEntity saved =
                monitoringRepository.save(entity);

        return monitoringMapper.toDto(saved);
}
}