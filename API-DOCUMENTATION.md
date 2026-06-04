# API SLA — Documentación de Endpoints

**Base URL:** `http://localhost:8080/api/v1`  
**Autenticación:** Basic Auth → usuario: `admin` / contraseña: `enoc`

Todos los endpoints regresan el mismo wrapper:

```json
{
  "success": true,
  "message": "string",
  "data": { ... },
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

---

## 1. GET /apis

Consulta las APIs registradas en **Anypoint API Manager**.

**Request**

```
GET /api/v1/apis
Authorization: Basic admin:enoc
```

**Response 200 OK**

```json
{
  "success": true,
  "message": "Apis retrieved successfully",
  "data": [
    {
      "id": "payments-api.v1",
      "name": "payments-api",
      "country": "ac584c40-1bf7-4c08-96a3-c9013a01d871",
      "status": "active",
      "monitored": false,
      "lastChecked": "2026-06-02T22:18:15.090Z"
    }
  ],
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | String | `assetId.productVersion` |
| `name` | String | Nombre del asset en Anypoint |
| `country` | String | Group ID (org ID de Anypoint) |
| `status` | String | `active` o `deprecated` |
| `monitored` | Boolean | Si tiene monitoreo activo |
| `lastChecked` | Instant | Momento de la consulta |

---

## 2. GET /apis/{apiId}/metrics

Obtiene métricas de las **últimas 24 horas** de una API desde Anypoint Analytics.

**Request**

```
GET /api/v1/apis/payments-api/metrics
Authorization: Basic admin:enoc
```

**Response 200 OK**

```json
{
  "success": true,
  "message": "Metrics retrieved successfully",
  "data": {
    "apiId": "payments-api",
    "totalRequests": 8500,
    "avgResponseTimeMs": 42.5,
    "totalErrors": 12,
    "errorRatePercent": 0.14
  },
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `apiId` | String | ID de la API consultada |
| `totalRequests` | Long | Total de requests en 24h |
| `avgResponseTimeMs` | Double | Tiempo de respuesta promedio en ms |
| `totalErrors` | Long | Total de errores en 24h |
| `errorRatePercent` | Double | Porcentaje de error `(errors/total)*100` |

---

## 3. GET /applications

Lista las apps desplegadas en **CloudHub** (Runtime Manager).

**Request**

```
GET /api/v1/applications
Authorization: Basic admin:enoc
```

**Response 200 OK**

```json
{
  "success": true,
  "message": "Applications retrieved successfully",
  "data": [
    {
      "domain": "payments-svc",
      "fullDomain": "payments-svc.cloudhub.io",
      "status": "STARTED",
      "muleVersion": "4.6.0",
      "region": "us-east-1",
      "workerCount": 2
    }
  ],
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `domain` | String | Nombre corto de la app |
| `fullDomain` | String | URL completa en CloudHub |
| `status` | String | `STARTED` o `STOPPED` |
| `muleVersion` | String | Versión de Mule Runtime |
| `region` | String | Región de despliegue |
| `workerCount` | Integer | Número de workers activos |

---

## 4. GET /exchange/assets

Lista los assets publicados en **Anypoint Exchange**.

**Request**

```
GET /api/v1/exchange/assets
Authorization: Basic admin:enoc
```

**Response 200 OK**

```json
{
  "success": true,
  "message": "Exchange assets retrieved successfully",
  "data": [
    {
      "groupId": "ac584c40-1bf7-4c08-96a3-c9013a01d871",
      "assetId": "caso2-ws-delphi-rest-imp",
      "version": "1.0.4",
      "name": "caso2-ws-delphi-rest-imp",
      "type": "app",
      "status": "published",
      "description": ""
    }
  ],
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `groupId` | String | Org ID de Anypoint |
| `assetId` | String | Identificador único del asset |
| `version` | String | Versión publicada |
| `name` | String | Nombre del asset |
| `type` | String | Tipo: `app`, `rest-api`, etc. |
| `status` | String | `published` o `draft` |
| `description` | String | Descripción del asset |

---

## 5. POST /monitoring

Crea una configuración de monitoreo SLA en la **base de datos local**.

**Request**

```
POST /api/v1/monitoring
Authorization: Basic admin:enoc
Content-Type: application/json
```

```json
{
  "apis": [
    {
      "apiId": "payments-api",
      "tier": "gold"
    },
    {
      "apiId": "orders-api",
      "tier": "silver"
    }
  ],
  "tiers": {
    "gold": {
      "availability": { "minPercentage": 99.9 },
      "latency":      { "p95Ms": 200 },
      "errorRate":    { "maxPercentage": 0.1 },
      "throughput":   { "minRps": 100 }
    },
    "silver": {
      "availability": { "minPercentage": 99.0 },
      "latency":      { "p95Ms": 500 },
      "errorRate":    { "maxPercentage": 1.0 },
      "throughput":   { "minRps": 50 }
    }
  },
  "schedule": {
    "type": "cron",
    "expression": "0 */5 * * * *"
  }
}
```

**Response 201 Created**

```json
{
  "success": true,
  "message": "Monitoring created successfully",
  "data": {
    "monitoringId": "uuid-generado",
    "status": "active",
    "apisConfigured": 2,
    "schedule": {
      "type": "cron",
      "expression": "0 */5 * * * *"
    },
    "createdAt": "2026-06-02T22:18:15.090Z",
    "details": [
      {
        "apiId": "payments-api",
        "tier": "gold",
        "status": "configured",
        "message": ""
      }
    ]
  },
  "timestamp": "2026-06-02T22:18:15.090Z"
}
```

**Campos del body**

| Campo | Tipo | Descripción |
|---|---|---|
| `apis` | Array | Lista de APIs a monitorear con su tier |
| `apis[].apiId` | String | ID de la API |
| `apis[].tier` | String | Nombre del tier asignado |
| `tiers` | Map | Reglas SLA por nombre de tier |
| `tiers.*.availability.minPercentage` | Double | Disponibilidad mínima requerida en % |
| `tiers.*.latency.p95Ms` | Integer | Latencia máxima en percentil 95 (ms) |
| `tiers.*.errorRate.maxPercentage` | Double | Tasa de error máxima permitida en % |
| `tiers.*.throughput.minRps` | Integer | Requests por segundo mínimos |
| `schedule.type` | String | Tipo de schedule: `cron` o `interval` |
| `schedule.expression` | String | Expresión cron o valor de intervalo |

---

## Resumen

| Endpoint | Método | Fuente de datos |
|---|---|---|
| `/apis` | GET | MuleSoft API Manager |
| `/apis/{apiId}/metrics` | GET | MuleSoft Analytics (24h) |
| `/applications` | GET | MuleSoft CloudHub |
| `/exchange/assets` | GET | MuleSoft Exchange |
| `/monitoring` | POST | Base de datos local H2 |
