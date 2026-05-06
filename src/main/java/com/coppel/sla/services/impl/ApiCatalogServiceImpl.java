package com.coppel.sla.services.impl;

import com.coppel.sla.component.DatadogClient;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.mappers.ApiMapper;
import com.coppel.sla.repositories.ApiConfigRepository;
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


    @Override
    public List<ApiResponseDto> getApis() {

        return datadogClient.listApis()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

}