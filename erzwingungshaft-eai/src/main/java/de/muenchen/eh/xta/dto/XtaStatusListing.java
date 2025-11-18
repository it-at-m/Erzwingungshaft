package de.muenchen.eh.xta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigInteger;
import java.util.List;

public record XtaStatusListing(
        @PositiveOrZero BigInteger itemsPending,
        @NotNull List<XtaMessageMetaData> messages) {
}
