package de.muenchen.eh.xta.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @param code Code of the message type.
 *            <p>
 *            For instance, <i>Geschaeftsgang.Geschaeftsgang.0201</i> with business scenario
 *            <i>XDOMEAGAD_DATA</i>.
 *            </p>
 *            <p>
 *            This field is required.
 *            </p>
 * @param payloadSchema Payload schema of the message type.
 *            <p>
 *            For instance, <i>urn:xoev-de:xdomea:schema:2.4.0</i> with code
 *            <i>Geschaeftsgang.Geschaeftsgang.0201</i> and business scenario <i>XDOMEAGAD_DATA</i>.
 *            </p>
 *            <p>
 *            This field is required.
 *            </p>
 */
public record XtaMessageType(
        @NotBlank String code,
        @NotBlank String payloadSchema) {
}
