package com.coppel.sla.controllers;

import com.coppel.sla.dto.ApiResponseDto;
import com.coppel.sla.services.ApiCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/apis")
public class ApiController {

    private final ApiCatalogService service;


    @GetMapping
    public List<ApiResponseDto> getApis() {
        return service.getApis();
    }
}
