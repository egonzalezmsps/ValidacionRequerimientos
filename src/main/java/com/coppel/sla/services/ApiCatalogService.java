package com.coppel.sla.services;

import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;

import java.util.List;

public interface ApiCatalogService {
    List<ApiResponseDto> getApis();
    MonitoringResponseDto createMonitoring(
            MonitoringRequestDto request);

}

