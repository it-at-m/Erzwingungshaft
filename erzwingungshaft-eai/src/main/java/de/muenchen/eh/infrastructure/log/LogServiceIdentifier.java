package de.muenchen.eh.infrastructure.log;

import de.muenchen.eh.domain.identifier.IdentifierContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.EfileIdentifierRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LogServiceIdentifier {

    private final EfileIdentifierRepository efileIdentifierRepository;

    public void logError(Exchange exchange) {

        try {

            IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

            EfileIdentifier efileIdentifier = (identifierContentWrapper != null && identifierContentWrapper.getEfileIdentifier() != null)
                    ? identifierContentWrapper.getEfileIdentifier()
                    : new EfileIdentifier("", "", "", "", "", "", "", "", -1, "", "", MessageType.ERROR, "", "");
            efileIdentifier.setMessageType(MessageType.ERROR);
            efileIdentifier.setMessage(LogServiceError.getMessage(exchange));
            var stack = LogServiceError.getStack(exchange);
            efileIdentifier.setComment(stack.length > 0 ? Arrays.toString(stack) : "No stack trace available.");

            efileIdentifierRepository.save(efileIdentifier);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }

    }

    public void writeError(StatusProcessingType processingType, Exchange exchange) {

        try {

            IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

            EfileIdentifier efileIdentifier = (identifierContentWrapper != null && identifierContentWrapper.getEfileIdentifier() != null)
                    ? identifierContentWrapper.getEfileIdentifier()
                    : new EfileIdentifier("", "", "", "", "", "", "", "", -1, "", "", MessageType.ERROR, "", "");
            efileIdentifier.setMessage(processingType.name());
            efileIdentifier.setComment(processingType.getDescriptor());

            efileIdentifierRepository.save(efileIdentifier);

        } catch (Exception e) {
            exchange.setException(e);
            log.error(e);
        }

    }

}
