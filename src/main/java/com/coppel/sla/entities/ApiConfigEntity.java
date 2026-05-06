package com.coppel.sla.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.Id;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_config")
public class ApiConfigEntity {

    @Id
    private String id;
    private String name;
    private String country;
    private String status;
    private Boolean monitored;
    private Instant lastChecked;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}