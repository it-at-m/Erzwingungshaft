package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * EAPL-Komponente (Einheitaktenplan).
 *
 * <p>
 * Implementiert die konkreten Verarbeitungsschritte für den Einheitaktenplan-Workflow
 * und delegiert die eigentliche Arbeit an die injizierten Executor-Komponenten:
 * <ul>
 * <li>AddCollection – Hinzufügen einer Ziel-Collection (falls erforderlich)</li>
 * <li>AddFile – Hinzufügen einer Datei zur Collection (falls erforderlich)</li>
 * <li>AddFine – Hinzufügen einer Geldbuße/Strafe</li>
 * <li>AddOutgoing – Hinzufügen von Outgoing Dokumenten</li>
 * </ul>
 * </p>
 *
 * <p>
 * Die Klasse erweitert {@link EAPLTemplate} und überschreibt die Template-Methoden,
 * wobei die konkrete Logik an die jeweiligen Helfer/Handler delegiert wird.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class EAPL extends EAPLTemplate {

    private final AddCollection collectionFinder;
    private final AddFile addFile;
    private final AddFine addFine;
    private final AddOutgoing addOutgoing;

    /**
     * Legt eine Ziel-Collection an wenn nicht vorhanden.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addCollection(Exchange exchange) {
        collectionFinder.execute(exchange);
    }

    /**
     * Legt eine Akte in der ermittelten Collection an wenn nicht vorhanden.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addFile(Exchange exchange) {
        addFile.execute(exchange);
    }

    /**
     * Fügt einen neuen zum Kassenzeichen gehörenden Vorgang (Geldbuße/Strafe (fine)) in die Akte ein.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addFine(Exchange exchange) {
        addFine.execute(exchange);
    }

    /**
     * Fügt Outgoing Dokumente an.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addOutgoing(Exchange exchange) {
        addOutgoing.execute(exchange);
    }
}
