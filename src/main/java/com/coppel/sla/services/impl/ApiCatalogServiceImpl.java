package com.coppel.sla.services.impl;

import com.coppel.sla.component.DatadogClient;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.entities.ApiConfigEntity;
import com.coppel.sla.mappers.ApiMapper;
import com.coppel.sla.repositories.ApiConfigRepository;
import com.coppel.sla.services.ApiCatalogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ApiCatalogServiceImpl implements ApiCatalogService {

    private final DatadogClient datadogClient;
    private final ApiConfigRepository repository;
    private final ApiMapper mapper;

    public ApiCatalogServiceImpl(
            DatadogClient datadogClient,
            ApiConfigRepository repository,
            ApiMapper mapper) {
        this.datadogClient = datadogClient;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public List<ApiResponseDto> getApis() {
        Instant now = Instant.now();
        List<ApiConfigEntity> apis = datadogClient.listApis()
                .stream()
                .map(api -> enrich(api, now))
                .toList();
        repository.saveAll(apis);
        return apis.stream()
                .map(mapper::toDto)
                .toList();
    }

    private ApiConfigEntity enrich(ApiConfigEntity api, Instant now) {
        api.setUpdatedAt(now);
        api.setUpdatedBy("system");

        if (api.getCreatedAt() == null) {
            api.setCreatedAt(now);
            api.setCreatedBy("system");
        }

        return api;
    }
}