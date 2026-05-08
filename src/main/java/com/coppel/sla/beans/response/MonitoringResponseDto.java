package com.coppel.sla.beans.response;

import com.coppel.sla.dto.monitory.MonitoringDetailDto;
import com.coppel.sla.dto.monitory.ScheduleDto;

import java.time.Instant;
import java.util.List;

public record MonitoringResponseDto(
        String monitoringId,
        String status,
        Integer apisConfigured,
        ScheduleDto schedule,
        Instant createdAt,
        List<MonitoringDetailDto> details
) {
}
