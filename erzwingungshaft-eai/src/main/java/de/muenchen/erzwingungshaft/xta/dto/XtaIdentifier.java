package de.muenchen.erzwingungshaft.xta.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

/**
 * XTA-Party-Identifier für Clients oder Server.
 *
 * @param name Name of the identifier.
 *            <p>
 *            This field is optional.
 *            </p>
 * @param category Category of the identifier.
 *            <p>
 *            This field is optional.
 *            </p>
 * @param value Value of the identifier.
 *            <p>
 *            This field is required.
 *            </p>
 */
public record XtaIdentifier(
        @Nullable String name,
        @Nullable String category,
        @NotBlank String value) {
}
