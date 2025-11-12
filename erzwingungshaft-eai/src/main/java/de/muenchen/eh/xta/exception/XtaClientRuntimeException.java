package de.muenchen.eh.xta.exception;

import java.io.Serial;

public class XtaClientRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @see RuntimeException#RuntimeException(String)
     * @param message – the detail message.
     */
    public XtaClientRuntimeException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     * @param message – the detail message
     * @param cause – the cause. (A null value is permitted, and indicates that the cause is nonexistent
     *            or unknown.)
     */
    public XtaClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
