package com.coppel.sla.dto.monitory;

public record MonitoringDetailDto(

        String apiId,

        String tier,

        String status,

        String message
) {
}