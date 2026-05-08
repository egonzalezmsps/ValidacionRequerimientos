package com.coppel.sla.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "monitoring_config")
public class MonitoringConfigEntity {

    @Id
    private String id;
    private String status;
    private String scheduleType;
    private String cronExpression;
    private Instant createdAt;

    @OneToMany(
            mappedBy = "monitoring",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MonitoringApiEntity> apis;

}
