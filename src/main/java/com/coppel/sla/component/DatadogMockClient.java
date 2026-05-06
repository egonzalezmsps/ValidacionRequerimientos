package com.coppel.sla.component;

import com.coppel.sla.entities.ApiConfigEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(name = "datadog.enabled", havingValue = "false", matchIfMissing = true)
public class DatadogMockClient implements DatadogClient {

    private static final String STATUS_ACTIVE = "active";

    @Override
    public List<ApiConfigEntity> listApis() {
        Instant now = Instant.now();

        return List.of(
                mock("api-001", "payments-service", "MX", true, now),
                mock("api-002", "user-auth-service", "US", false, now),
                mock("api-003", "orders-service", "BR", false, now)
        );
    }

    private ApiConfigEntity mock(String id,
                                 String name,
                                 String country,
                                 boolean monitored,
                                 Instant now) {

        ApiConfigEntity api = new ApiConfigEntity();
        api.setId(id);
        api.setName(name);
        api.setCountry(country);
        api.setStatus(STATUS_ACTIVE);
        api.setMonitored(monitored);
        api.setLastChecked(now);
        api.setCreatedAt(now);
        api.setCreatedBy("mock");
        api.setUpdatedAt(now);
        api.setUpdatedBy("mock");

        return api;
    }
}
