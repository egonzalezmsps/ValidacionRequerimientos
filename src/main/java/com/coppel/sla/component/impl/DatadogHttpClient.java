package com.coppel.sla.component.impl;

import com.coppel.sla.component.DatadogClient;
import com.coppel.sla.entities.ApiConfigEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.micrometer.metrics.autoconfigure.export.datadog.DatadogProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@ConditionalOnProperty(name = "datadog.enabled", havingValue = "true")
public class DatadogHttpClient implements DatadogClient {

    private final DatadogProperties properties;
    private final RestTemplate restTemplate;

    public DatadogHttpClient(RestTemplate restTemplate,
                             DatadogProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public List<ApiConfigEntity> listApis() {
        throw new UnsupportedOperationException("Implement DataDog API call");
    }
}