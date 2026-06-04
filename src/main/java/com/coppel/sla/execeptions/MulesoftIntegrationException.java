package com.coppel.sla.execeptions;

public class MulesoftIntegrationException extends RuntimeException {

    public MulesoftIntegrationException(String message) {
        super(message);
    }

    public MulesoftIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
