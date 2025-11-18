package de.muenchen.eh.xta.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum XtaMessageStatus {
    /**
     * Status for a message that is open, i.e., has not been closed.
     */
    OPEN(0),
    /**
     * Status for a message that has been closed and has no errors or warnings.
     */
    GREEN(1),
    /**
     * Status for a message that has been closed and has warnings but no errors.
     */
    YELLOW(2),
    /**
     * Status for a message that has been closed and has errors.
     */
    RED(3);

    private final Integer code;

}
