package com.coppel.sla.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datadog")
public record DatadogProperties(boolean enabled) {}