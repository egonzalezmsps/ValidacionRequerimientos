package com.coppel.sla.services;

import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
import com.coppel.sla.dto.mulesoft.MulesoftApplicationDto;
import com.coppel.sla.dto.mulesoft.MulesoftAssetDto;
import com.coppel.sla.dto.mulesoft.MulesoftMetricsDto;

import java.util.List;

public interface ApiCatalogService {
    List<ApiResponseDto> getApis();
    MonitoringResponseDto createMonitoring(MonitoringRequestDto request);
    MulesoftMetricsDto getApiMetrics(String apiId);
    List<MulesoftApplicationDto> getMulesoftApplications();
    List<MulesoftAssetDto> getMulesoftAssets();
}

