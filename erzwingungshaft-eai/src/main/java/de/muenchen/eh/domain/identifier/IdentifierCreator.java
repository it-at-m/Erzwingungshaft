package de.muenchen.eh.domain.identifier;

import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import de.muenchen.eh.infrastructure.db.entity.MessageType;
import de.muenchen.eh.infrastructure.db.repository.EfileIdentifierRepository;
import de.muenchen.eh.infrastructure.log.Constants;
import de.muenchen.eh.infrastructure.log.StatusProcessingType;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentifierCreator implements Processor {

    @Value("${efile.case-file.department}")
    @Getter
    private String department;

    @Produce(value = IdentifierRouteBuilder.IDENTIFIER_FINE)
    private ProducerTemplate addFineIdentifier;

    private final EfileIdentifierRepository efileIdentifierRepository;

    private static final Pattern PAREN_CONTENT = Pattern.compile("\\(([^)]*)\\)");

    @Override
    public void process(Exchange exchange) throws Exception {

        IdentifierContentWrapper identifierContentWrapper = exchange.getMessage().getBody(IdentifierContentWrapper.class);

        // Insert a "comes from" context marker for correct error handling in BaseRouteBuilder
        exchange.setProperty(Constants.IDENTIFIER_CREATOR, identifierContentWrapper);

        // Determine filing location in efile/DMS
        addFineIdentifier.send(exchange);

        EfileIdentifier efileIdentifierEntity = identifierContentWrapper.getEfileIdentifier();

        // Generate identifier
        Optional<String> uniqueIdentifier = createIdentifier(Optional.ofNullable(efileIdentifierEntity.getKassenzeichenEfile()),
                Optional.ofNullable(getDepartment()), Optional.ofNullable(efileIdentifierEntity.getFineNameCooAddress()), efileIdentifierEntity);

        // Persist result
        uniqueIdentifier.ifPresentOrElse(ident -> {
            efileIdentifierEntity.setIdentifier(ident);
            efileIdentifierEntity.setOutputFileName(efileIdentifierEntity.getSourceFileName().concat("_mitAZ"));
            identifierContentWrapper.getPscdDataExport().setGeschaeftszeicheneakte(ident);
        },
                () -> {
                    efileIdentifierEntity.setIdentifier(null);
                    efileIdentifierEntity.setMessageType(MessageType.ERROR);
                    isEmptySetMessage(efileIdentifierEntity, StatusProcessingType.FINE_IDENTIFIER_FAILED.getDescriptor());
                });

        efileIdentifierRepository.save(efileIdentifierEntity);
    }

    public static Optional<String> createIdentifier(Optional<String> kassenzeichen, Optional<String> department, Optional<String> efileObjectName,
            EfileIdentifier efileIdentifierEntity) {

        if (efileObjectName.isEmpty()) {
            isEmptySetMessage(efileIdentifierEntity, "Efile fine objectname is null.");
            return Optional.empty();
        }

        Optional<String> efileIdentifier = extractFirstParenthesesContent(efileObjectName.get());
        if (efileIdentifier.isPresent() && kassenzeichen.isPresent() && department.isPresent())
            return Optional.of(kassenzeichen.get() + "-" + department.get() + efileIdentifier.get());
        else {
            if (efileIdentifier.isEmpty())
                isEmptySetMessage(efileIdentifierEntity, "Efile fine content extraction failed.");
            if (kassenzeichen.isEmpty())
                isEmptySetMessage(efileIdentifierEntity, "Kassenzeichen is null.");
            if (department.isEmpty())
                isEmptySetMessage(efileIdentifierEntity, "Check efile.case-file.basenr");
            return Optional.empty();
        }
    }

    private static void isEmptySetMessage(EfileIdentifier entity, String message) {
        if (entity.getMessage().isEmpty())
            entity.setMessage(message);
    }

    /**
     * Extracts the content of the first set of parentheses from the given text.
     * <p>
     * Example: "Bußgeldverfahren (9512.13-2-0001)" -> "9512.13-2-0001"
     *
     * @param text Input text (nullable)
     * @return Optionally containing the content of the first parenthesis, or Optional.empty() if no
     *         parenthesis was found.
     */
    private static Optional<String> extractFirstParenthesesContent(String text) {
        if (text == null) {
            return Optional.empty();
        }
        Matcher m = PAREN_CONTENT.matcher(text);
        if (m.find()) {
            return Optional.ofNullable(m.group(1));
        }
        return Optional.empty();
    }

}
