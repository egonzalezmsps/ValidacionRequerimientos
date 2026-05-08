package com.coppel.sla.dto.monitory;

public record TierRuleDto(

        AvailabilityDto availability,

        LatencyDto latency,

        ErrorRateDto errorRate,

        ThroughputDto throughput
) {
}