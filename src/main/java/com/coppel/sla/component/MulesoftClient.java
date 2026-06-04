package com.coppel.sla.component;

import com.coppel.sla.dto.mulesoft.MulesoftApplicationDto;
import com.coppel.sla.dto.mulesoft.MulesoftAssetDto;
import com.coppel.sla.dto.mulesoft.MulesoftMetricsDto;
import com.coppel.sla.entities.ApiConfigEntity;

import java.util.List;

public interface MulesoftClient {
    List<ApiConfigEntity> listApis();
    MulesoftMetricsDto getApiMetrics(String apiId);
    List<MulesoftApplicationDto> listApplications();
    List<MulesoftAssetDto> listExchangeAssets();
}
