package com.coppel.sla.controllers;

import com.coppel.sla.beans.ApiResponse;
import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
import com.coppel.sla.dto.mulesoft.MulesoftApplicationDto;
import com.coppel.sla.dto.mulesoft.MulesoftAssetDto;
import com.coppel.sla.dto.mulesoft.MulesoftMetricsDto;
import com.coppel.sla.services.ApiCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ApiCatalogService service;

    @GetMapping("/apis")
    public ResponseEntity<ApiResponse<List<ApiResponseDto>>> getApis() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Apis retrieved successfully", service.getApis(), Instant.now())
        );
    }

    @PostMapping("/monitoring")
    public ResponseEntity<ApiResponse<MonitoringResponseDto>> createMonitoring(
            @RequestBody MonitoringRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Monitoring created successfully", service.createMonitoring(request), Instant.now())
        );
    }

    @GetMapping("/apis/{apiId}/metrics")
    public ResponseEntity<ApiResponse<MulesoftMetricsDto>> getApiMetrics(
            @PathVariable String apiId) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Metrics retrieved successfully", service.getApiMetrics(apiId), Instant.now())
        );
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<MulesoftApplicationDto>>> getApplications() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Applications retrieved successfully", service.getMulesoftApplications(), Instant.now())
        );
    }

    @GetMapping("/exchange/assets")
    public ResponseEntity<ApiResponse<List<MulesoftAssetDto>>> getExchangeAssets() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Exchange assets retrieved successfully", service.getMulesoftAssets(), Instant.now())
        );
    }
}
