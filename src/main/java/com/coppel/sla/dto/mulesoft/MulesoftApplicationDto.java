package com.coppel.sla.dto.mulesoft;

public record MulesoftApplicationDto(
        String domain,
        String fullDomain,
        String status,
        String muleVersion,
        String region,
        Integer workerCount
) {}
