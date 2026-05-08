package com.coppel.sla.controllers;

import com.coppel.sla.beans.ApiResponse;
import com.coppel.sla.beans.response.MonitoringResponseDto;
import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.dto.monitory.MonitoringRequestDto;
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

        List<ApiResponseDto> response =
                service.getApis();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Apis retrieved successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @PostMapping("/monitoring")
    public ResponseEntity<ApiResponse<MonitoringResponseDto>>
    createMonitoring(
            @RequestBody MonitoringRequestDto request
    ) {

        MonitoringResponseDto response =
                service.createMonitoring(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Monitoring created successfully",
                                response,
                                Instant.now()
                        )
                );
    }
}
