package de.muenchen.eh.xta.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

/**
 * @param code Domain qualifier, e.g. Meldewesen, XVergabe
 *            <p>
 *            This field is required.
 *            </p>
 * @param listUri Code list URI of the business scenario.
 *            <p>
 *            If {@code null}, {@link XtaBusinessScenario#code} refers to an undefined business
 *            scenario. Otherwise, it is a defined scenario which additionally requires
 *            {@link XtaBusinessScenario#listVersionId}.
 *            </p>
 * @param listVersionId code list version ID of the business scenario.
 *            <p>
 *            Required if {@link XtaBusinessScenario#listUri} is not {@code null}.
 *            </p>
 */
public record XtaBusinessScenario(
        @Nullable String listUri,
        @Nullable String listVersionId,
        @NotBlank String code) {
}
