package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.addfine;

import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.EAPL;
import org.apache.camel.Exchange;

/**
 * Template-Implementierung des EAPLAddFine-Workflows (Einheitaktenplan).
 *
 * <p>
 * Diese abstrakte Klasse implementiert das Template‑Methoden‑Pattern für den
 * EAPLAddFine‑Ablauf. Die {@link #execute(Exchange)}-Methode definiert die feste
 * Abfolge der Schritte:
 * <ol>
 *   <li>findCollection</li>
 *   <li>findFile</li>
 *   <li>addFine</li>
 * </ol>
 * Zwischen den Schritten wird geprüft, ob {@link Exchange#isRouteStop()} gesetzt
 * wurde; dann wird die Ausführung frühzeitig abgebrochen.
 * </p>
 *
 * <p>
 * Konkrete Implementierungen (z. B. {@link EAPL}) müssen die drei
 * abstrakten Hook‑Methoden überschreiben und die jeweilige Geschäftslogik
 * delegieren.
 * </p>
 */
public abstract class EAPLAddFineTemplate {


    /**
     * Führt den kompletten EAPL‑Workflow in der festgelegten Reihenfolge aus.
     * <p>
     * Prüfungen auf {@link Exchange#isRouteStop()} verhindern die weitere
     * Ausführung des Workflows, sobald ein Schritt die Route gestoppt hat.
     * </p>
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    public final void execute(Exchange exchange) {

        findCollection(exchange);
        if (exchange.isRouteStop())
            return;

        findFile(exchange);
        if (exchange.isRouteStop())
            return;

        addFine(exchange);

    }

    /**
     * Findet die Ziel‑Collection für die weitere Verarbeitung oder endet mit einem Fehler.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void findCollection(Exchange exchange);

    /**
     * Findet eine Akte in der zuvor gefundene Collection für die weitere Verarbeitung oder endet mit einem Fehler.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void findFile(Exchange exchange);

    /**
     * Fügt einen Vorgang (Bußgeld/Strafe) in die Akte ein.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void addFine(Exchange exchange);


}
