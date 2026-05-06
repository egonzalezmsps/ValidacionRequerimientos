package com.coppel.sla.component;

import com.coppel.sla.entities.ApiConfigEntity;
import java.util.List;

public interface DatadogClient {
    List<ApiConfigEntity> listApis();
}