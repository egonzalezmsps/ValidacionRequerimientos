# SLA Monitoring POC - Framework de Trabajo

## Descripción General

Este proyecto es una POC (Proof of Concept) para un sistema de monitoreo de APIs basado en métricas de DataDog.
Permite registrar APIs, configurarlas con SLAs por niveles (tiers), ejecutar evaluaciones automáticas mediante un scheduler y consultar reportes de cumplimiento o brechas (breaches).

---

## Arquitectura General

- API Management (consulta de APIs disponibles)
- Monitoring Configuration (definición de SLAs por API)
- Scheduler Engine (ejecución periódica de validaciones)
- Rules Engine (evaluación de SLAs)
- Reporting Module (consulta de resultados)
- Persistence Layer (PostgreSQL / H2 local)

---

## Modelo de Datos

```sql
-- SLA Monitoring POC - PostgreSQL Schema

CREATE TABLE api_config (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(10),
    status VARCHAR(20),
    monitored BOOLEAN DEFAULT false,
    last_checked TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(50)
);

CREATE TABLE monitoring_config (
    id VARCHAR(50) PRIMARY KEY,
    status VARCHAR(30),
    schedule_type VARCHAR(20),
    schedule_expression VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(50)
);

CREATE TABLE monitoring_api (
    id BIGSERIAL PRIMARY KEY,
    monitoring_id VARCHAR(50) NOT NULL,
    api_id VARCHAR(50) NOT NULL,
    tier VARCHAR(20),
    status VARCHAR(20),
    message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(50),
    FOREIGN KEY (monitoring_id) REFERENCES monitoring_config(id),
    FOREIGN KEY (api_id) REFERENCES api_config(id)
);

CREATE TABLE sla_report (
    id VARCHAR(50) PRIMARY KEY,
    api_id VARCHAR(50) NOT NULL,
    api_name VARCHAR(100),
    tier VARCHAR(20),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20),
    total_checks INTEGER,
    passed INTEGER,
    failed INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(50),
    FOREIGN KEY (api_id) REFERENCES api_config(id)
);

CREATE TABLE sla_metric (
    id BIGSERIAL PRIMARY KEY,
    report_id VARCHAR(50) NOT NULL,
    type VARCHAR(30),
    value DOUBLE PRECISION,
    threshold DOUBLE PRECISION,
    status VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(50),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(50),
    FOREIGN KEY (report_id) REFERENCES sla_report(id)
);
```

---

## Auditoría Base

Todas las entidades heredan de `Auditable`:

| Campo | Tipo |
|-------|------|
| createdAt | Timestamp |
| createdBy | String |
| updatedAt | Timestamp |
| updatedBy | String |

---

## Entidades

### ApiConfig
`id`, `name`, `country`, `status`, `monitored`, `lastChecked`

### MonitoringConfig
`id`, `status`, `scheduleType`, `scheduleExpression`

### MonitoringApi
`id`, `monitoringId`, `apiId`, `tier`, `status`, `message`

### SlaReport
`id`, `apiId`, `apiName`, `tier`, `timestamp`, `status`, `totalChecks`, `passed`, `failed`

### SlaMetric
`id`, `reportId`, `type`, `value`, `threshold`, `status`

---

## Base de Datos

| Ambiente | Motor |
|----------|-------|
| Local | H2 In-Memory (EN DEFINICION) |
| GCP | PostgreSQL |

---

## Flujo del Sistema

### 1. `GET /apis`
Consulta APIs disponibles.

### 2. `POST /monitoring`
Configura APIs + tiers.

### 3. Scheduler
Ejecuta cada X minutos:
- Consulta métricas
- Evalúa SLAs
- Guarda reportes

### 4. Rules Engine
Evalúa:
- Latencia
- Disponibilidad
- Error rate
- Throughput

### 5. `GET /reports`
Consulta reportes por API o status.

---

## Estados

### MonitoringApi
| Estado | Descripción |
|--------|-------------|
| `configured` | API configurada correctamente |
| `error` | Error en la configuración |

### SLA
| Estado | Descripción |
|--------|-------------|
| `ok` | SLA cumplido |
| `breach` | SLA incumplido |

### MonitoringConfig
| Estado | Descripción |
|--------|-------------|
| `scheduled` | Programado correctamente |
| `partial_success` | Éxito parcial |
| `failed` | Falló la ejecución |

---

## Decisiones de Diseño

- Auditoría centralizada con clase base `Auditable`
- Flyway para migraciones
- H2 para desarrollo local
- PostgreSQL para GCP
- Separación `MonitoringApi` para relación N-N

---

## Objetivo

Demostrar arquitectura escalable de monitoreo de APIs basada en SLAs con integración tipo DataDog.
