package de.muenchen.erzwingungshaft.xta.exception;

public class XtaClientInitializationException extends XtaClientException {
    public XtaClientInitializationException(String message) {
        super(message);
    }

    public XtaClientInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
