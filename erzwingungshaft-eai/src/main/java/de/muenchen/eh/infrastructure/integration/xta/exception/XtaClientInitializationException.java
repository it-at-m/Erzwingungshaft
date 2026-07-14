package de.muenchen.eh.infrastructure.integration.xta.exception;

public class XtaClientInitializationException extends XtaClientException {
    public XtaClientInitializationException(String message) {
        super(message);
    }

    public XtaClientInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}



