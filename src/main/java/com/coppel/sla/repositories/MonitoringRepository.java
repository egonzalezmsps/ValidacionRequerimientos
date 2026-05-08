package com.coppel.sla.repositories;

import com.coppel.sla.entities.MonitoringConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringRepository
        extends JpaRepository<MonitoringConfigEntity, String> {
}