package com.coppel.sla.execeptions;

public class DatadogIntegrationException extends RuntimeException {

    public DatadogIntegrationException(String message) {
        super(message);
    }

    public DatadogIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}