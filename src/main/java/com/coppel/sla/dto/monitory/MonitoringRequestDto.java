package com.coppel.sla.dto.monitory;

import java.util.List;
import java.util.Map;

public record MonitoringRequestDto(

        List<ApiMonitoringDto> apis,

        Map<String, TierRuleDto> tiers,

        ScheduleDto schedule
) {
}