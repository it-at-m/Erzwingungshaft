package de.muenchen.eh.xta.exception;

/**
 * A generic exception during xta client method execution.
 */
public class XtaClientException extends Exception {

    public XtaClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public XtaClientException(final String message) {
        super(message);
    }
}
