package com.coppel.sla.config;

import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class RestTemplateConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}