package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

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
  *   <li>AddCollection  – Suche/Ermittlung der Ziel-Collection</li>
  *   <li>AddFile         – Hinzufügen einer Datei zur Collection</li>
  *   <li>AddFine         – Hinzufügen einer Geldbuße/Strafe (falls relevant)</li>
  *   <li>AddOutgoing     – Versand/Weiterleitung der Outgoing-Information</li>
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
     * Legt eine Ziel-Collection an wenn sie nicht vorhanden ist.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addCollection(Exchange exchange) {
        collectionFinder.execute(exchange);
    }

    /**
     * Fügt eine Akte zur ermittelten Collection hinzu wenn sie nicht vorhanden ist.
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
     *  Fügt Outgoing Dokumente an.
     *
     * @param exchange Camel-Exchange mit Kontext/Message
     */
    @Override
    protected void addOutgoing(Exchange exchange) {
        addOutgoing.execute(exchange);
    }
}




