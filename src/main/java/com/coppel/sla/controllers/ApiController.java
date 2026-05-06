package com.coppel.sla.controllers;

import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.services.ApiCatalogService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private final ApiCatalogService service;

    public ApiController(ApiCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<ApiResponseDto> getApis() {
        return service.getApis();
    }
}
