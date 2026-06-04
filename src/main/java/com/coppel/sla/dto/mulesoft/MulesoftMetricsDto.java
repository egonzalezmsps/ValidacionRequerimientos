package com.coppel.sla.dto.mulesoft;

public record MulesoftMetricsDto(
        String apiId,
        Long totalRequests,
        Double avgResponseTimeMs,
        Long totalErrors,
        Double errorRatePercent
) {}
