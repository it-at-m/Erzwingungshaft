package de.muenchen.eh.domain.claim;

import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.EfileIdentifierRepository;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FineIdentifier {

    private final LogServiceClaim logServiceClaim;
    private final EfileIdentifierRepository efileIdentifierRepository;

    public Optional<EfileIdentifier> exists(Exchange exchange) {

        ClaimContentWrapper claimContentWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        // Combination 'geschaeftsparterId' + 'kassenzeichen' has valid identifier
        List<EfileIdentifier> efileIdentifiers = efileIdentifierRepository.findByGeschaeftspartnerIdAndKassenzeichenEfile(
                claimContentWrapper.getClaimImport().getGeschaeftspartnerId(), claimContentWrapper.getClaimImport().getKassenzeichen());

        if (efileIdentifiers.isEmpty()) {
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.IDENTIFIER_GESCHAEFTSPARTNERID_KASSENZEICHEN_ENTITY_NOT_EXISTS, MessageType.ERROR,
                    exchange);
            return Optional.empty();
        } else if (efileIdentifiers.size() > 1) {
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.IDENTIFIER_GESCHAEFTSPARTNERID_KASSENZEICHEN_AMBIGUOUS, MessageType.ERROR,
                    exchange);
            return Optional.empty();
        }

        EfileIdentifier efileIdentifier = efileIdentifiers.getFirst();

        if (efileIdentifier.getIdentifier().isBlank()) {
            logServiceClaim.writeGenericClaimLogMessage(StatusProcessingType.IDENTIFIER_GESCHAEFTSPARTNERID_KASSENZEICHEN_BLANK, MessageType.ERROR, exchange);
            return Optional.empty();
        } else
            return Optional.of(efileIdentifier);

    }

}
