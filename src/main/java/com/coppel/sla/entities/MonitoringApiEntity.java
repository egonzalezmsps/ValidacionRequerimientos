package com.coppel.sla.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Table(name = "monitoring_api")
public class MonitoringApiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String apiId;
    private String tier;
    @ManyToOne
    @JoinColumn(name = "monitoring_id")
    private MonitoringConfigEntity monitoring;


}