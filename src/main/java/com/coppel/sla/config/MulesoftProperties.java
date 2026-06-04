package com.coppel.sla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mulesoft")
public record MulesoftProperties(
        boolean enabled,
        String baseUrl,
        String clientId,
        String clientSecret,
        String organizationId,
        String environmentId
) {}
