package com.coppel.sla.component.impl;

import com.coppel.sla.component.MulesoftClient;
import com.coppel.sla.config.MulesoftProperties;
import com.coppel.sla.dto.mulesoft.MulesoftApplicationDto;
import com.coppel.sla.dto.mulesoft.MulesoftAssetDto;
import com.coppel.sla.dto.mulesoft.MulesoftMetricsDto;
import com.coppel.sla.entities.ApiConfigEntity;
import com.coppel.sla.execeptions.MulesoftIntegrationException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
@ConditionalOnProperty(name = "mulesoft.enabled", havingValue = "true")
public class MulesoftHttpClient implements MulesoftClient {

    private static final Logger log = LoggerFactory.getLogger(MulesoftHttpClient.class);

    private static final String TOKEN_PATH = "/accounts/api/v2/oauth2/token";
    private static final String ME_PATH    = "/accounts/api/me";
    private static final String ENV_PATH   = "/accounts/api/organizations/{orgId}/environments";

    private final MulesoftProperties properties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private volatile TokenCache tokenCache;
    private volatile String resolvedOrgId;
    private volatile String resolvedEnvId;

    private record TokenCache(String accessToken, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt.minusSeconds(60));
        }
    }

    public MulesoftHttpClient(MulesoftProperties properties, ObjectMapper objectMapper) {
        this.properties   = properties;
        this.objectMapper = objectMapper;
        this.webClient    = WebClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    // ── JSON helper ──────────────────────────────────────────────────────────

    private JsonNode parseJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new MulesoftIntegrationException("Failed to parse MuleSoft API response", e);
        }
    }

    // ── Token ────────────────────────────────────────────────────────────────

    private String getValidToken() {
        TokenCache cache = this.tokenCache;
        if (cache == null || cache.isExpired()) {
            synchronized (this) {
                cache = this.tokenCache;
                if (cache == null || cache.isExpired()) {
                    this.tokenCache = fetchNewToken();
                    cache = this.tokenCache;
                }
            }
        }
        return cache.accessToken();
    }

    private TokenCache fetchNewToken() {
        log.info("Fetching new MuleSoft OAuth2 token");
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type",    "client_credentials");
            form.add("client_id",     properties.clientId());
            form.add("client_secret", properties.clientSecret());
            form.add("scope",         "full");

            String raw = webClient.post()
                    .uri(TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode response = parseJson(raw);
            if (response == null || !response.has("access_token")) {
                throw new MulesoftIntegrationException("OAuth2 token response missing access_token");
            }

            String token     = response.get("access_token").asText();
            long   expiresIn = response.path("expires_in").asLong(3600);
            log.info("MuleSoft token acquired, expires in {}s", expiresIn);
            return new TokenCache(token, Instant.now().plusSeconds(expiresIn));

        } catch (WebClientResponseException e) {
            throw new MulesoftIntegrationException(
                    "Failed to obtain MuleSoft OAuth2 token: HTTP " + e.getStatusCode(), e);
        }
    }

    // ── Org ID ───────────────────────────────────────────────────────────────

    private String resolvedOrgId() {
        String configured = properties.organizationId();
        if (configured != null && !configured.isBlank()) return configured;
        if (this.resolvedOrgId == null) {
            synchronized (this) {
                if (this.resolvedOrgId == null) this.resolvedOrgId = fetchOrgId();
            }
        }
        return this.resolvedOrgId;
    }

    private String fetchOrgId() {
        log.info("Organization ID not configured — fetching from /accounts/api/me");
        try {
            String raw = webClient.get()
                    .uri(ME_PATH)
                    .header("Authorization", "Bearer " + getValidToken())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode me    = parseJson(raw);
            String   orgId = me == null ? null
                    : me.path("user").path("organization").path("id").asText(null);

            if (orgId == null || orgId.isBlank()) {
                throw new MulesoftIntegrationException(
                        "Could not determine organization ID from /accounts/api/me");
            }
            log.info("Resolved organization ID: {}", orgId);
            return orgId;

        } catch (WebClientResponseException e) {
            throw new MulesoftIntegrationException("Failed to fetch organization ID from Anypoint", e);
        }
    }

    // ── Environment ID ───────────────────────────────────────────────────────

    private String resolvedEnvId() {
        String configured = properties.environmentId();
        if (configured != null && !configured.isBlank()) return configured;
        if (this.resolvedEnvId == null) {
            synchronized (this) {
                if (this.resolvedEnvId == null) this.resolvedEnvId = fetchEnvId();
            }
        }
        return this.resolvedEnvId;
    }

    private String fetchEnvId() {
        log.info("Environment ID not configured — fetching sandbox from Anypoint");
        try {
            String raw = webClient.get()
                    .uri(ENV_PATH, resolvedOrgId())
                    .header("Authorization", "Bearer " + getValidToken())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode response = parseJson(raw);
            if (response == null || !response.has("data") || !response.get("data").isArray()) {
                throw new MulesoftIntegrationException("Unexpected response from environments endpoint");
            }

            String sandboxId    = null;
            String fallbackId   = null;
            String fallbackName = null;

            for (JsonNode env : response.get("data")) {
                String id   = env.path("id").asText(null);
                String type = env.path("type").asText("");
                String name = env.path("name").asText("");
                if ("sandbox".equalsIgnoreCase(type) && sandboxId == null) {
                    sandboxId = id;
                    log.info("Resolved environment ID: {} (sandbox: {})", id, name);
                }
                if (fallbackId == null) {
                    fallbackId   = id;
                    fallbackName = name;
                }
            }

            if (sandboxId != null)  return sandboxId;
            if (fallbackId != null) {
                log.warn("No sandbox environment found — using: {} ({})", fallbackId, fallbackName);
                return fallbackId;
            }

            throw new MulesoftIntegrationException(
                    "No environments found for organization " + resolvedOrgId());

        } catch (WebClientResponseException e) {
            throw new MulesoftIntegrationException("Failed to fetch environments from Anypoint", e);
        }
    }

    // ── Retry helper ─────────────────────────────────────────────────────────

    private String getWithRetry(Supplier<String> call) {
        try {
            return call.get();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                log.warn("Received 401 — invalidating token and retrying once");
                synchronized (this) { this.tokenCache = null; }
                try {
                    return call.get();
                } catch (WebClientResponseException retryEx) {
                    throw new MulesoftIntegrationException(
                            "MuleSoft request failed after token refresh: HTTP " + retryEx.getStatusCode(), retryEx);
                }
            }
            throw new MulesoftIntegrationException(
                    "MuleSoft API call failed: HTTP " + e.getStatusCode(), e);
        }
    }

    // ── MulesoftClient ───────────────────────────────────────────────────────

    @Override
    public List<ApiConfigEntity> listApis() {
        String raw = getWithRetry(() -> webClient.get()
                .uri("/apimanager/api/v1/organizations/{orgId}/environments/{envId}/apis",
                        resolvedOrgId(), resolvedEnvId())
                .header("Authorization", "Bearer " + getValidToken())
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode response = parseJson(raw);
        if (response == null || !response.has("apis")) return List.of();

        Instant now = Instant.now();
        List<ApiConfigEntity> result = new ArrayList<>();
        for (JsonNode api : response.get("apis")) {
            result.add(toApiEntity(api, now));
        }
        return result;
    }

    @Override
    public MulesoftMetricsDto getApiMetrics(String apiId) {
        Instant to   = Instant.now();
        Instant from = to.minus(24, ChronoUnit.HOURS);

        String raw = getWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/analytics/1.0/{orgId}/environments/{envId}/apis/{apiId}/summary")
                        .queryParam("from", from.toString())
                        .queryParam("to", to.toString())
                        .build(resolvedOrgId(), resolvedEnvId(), apiId))
                .header("Authorization", "Bearer " + getValidToken())
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode response = parseJson(raw);
        if (response == null) return new MulesoftMetricsDto(apiId, 0L, 0.0, 0L, 0.0);

        long   totalRequests   = response.path("totalRequests").asLong(0);
        double avgResponseTime = response.path("avgResponseTime").asDouble(0.0);
        long   totalErrors     = response.path("totalErrors").asLong(0);
        double errorRate       = totalRequests > 0 ? (totalErrors * 100.0 / totalRequests) : 0.0;

        return new MulesoftMetricsDto(apiId, totalRequests, avgResponseTime, totalErrors, errorRate);
    }

    @Override
    public List<MulesoftApplicationDto> listApplications() {
        String raw = getWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cloudhub/api/v2/applications")
                        .queryParam("organizationId", resolvedOrgId())
                        .queryParam("environmentId", resolvedEnvId())
                        .build())
                .header("Authorization", "Bearer " + getValidToken())
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode response = parseJson(raw);
        if (response == null || !response.isArray()) return List.of();

        List<MulesoftApplicationDto> apps = new ArrayList<>();
        for (JsonNode app : response) {
            apps.add(new MulesoftApplicationDto(
                    app.path("domain").asText(null),
                    app.path("fullDomain").asText(null),
                    app.path("status").asText(null),
                    app.path("muleVersion").path("version").asText(null),
                    app.path("region").asText(null),
                    app.path("workers").path("amount").asInt(0)
            ));
        }
        return apps;
    }

    @Override
    public List<MulesoftAssetDto> listExchangeAssets() {
        String raw = getWithRetry(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exchange/api/v2/assets")
                        .queryParam("organizationId", resolvedOrgId())
                        .build())
                .header("Authorization", "Bearer " + getValidToken())
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode response = parseJson(raw);
        if (response == null || !response.isArray()) return List.of();

        List<MulesoftAssetDto> assets = new ArrayList<>();
        for (JsonNode asset : response) {
            assets.add(new MulesoftAssetDto(
                    asset.path("groupId").asText(null),
                    asset.path("assetId").asText(null),
                    asset.path("version").asText(null),
                    asset.path("name").asText(null),
                    asset.path("type").asText(null),
                    asset.path("status").asText(null),
                    asset.path("description").asText(null)
            ));
        }
        return assets;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ApiConfigEntity toApiEntity(JsonNode api, Instant now) {
        ApiConfigEntity entity = new ApiConfigEntity();
        entity.setId(api.path("assetId").asText() + "." + api.path("productVersion").asText());
        entity.setName(api.path("assetId").asText());
        entity.setCountry(api.path("groupId").asText(null));
        entity.setStatus(api.path("deprecated").asBoolean(false) ? "deprecated" : "active");
        entity.setMonitored(false);
        entity.setLastChecked(now);
        entity.setCreatedAt(now);
        entity.setCreatedBy("mulesoft");
        entity.setUpdatedAt(now);
        entity.setUpdatedBy("mulesoft");
        return entity;
    }
}
