package com.coppel.sla.dto.mulesoft;

public record MulesoftAssetDto(
        String groupId,
        String assetId,
        String version,
        String name,
        String type,
        String status,
        String description
) {}
