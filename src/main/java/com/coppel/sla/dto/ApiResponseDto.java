package com.coppel.sla.dto;

import java.time.Instant;

public record ApiResponseDto(
        String id,
        String name,
        String country,
        String status,
        boolean monitored,
        Instant lastChecked
) {}