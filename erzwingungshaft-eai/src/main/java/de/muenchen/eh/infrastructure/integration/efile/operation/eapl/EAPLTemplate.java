package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

import org.apache.camel.Exchange;

/**
 * Template-Implementierung des EAPL-Workflows (Einheitaktenplan).
 *
 * <p>
 * Diese abstrakte Klasse implementiert das Template‑Methoden‑Pattern für den
 * EAPL‑Ablauf. Die {@link #execute(Exchange)}-Methode definiert die feste
 * Abfolge der Schritte:
 * <ol>
 *   <li>addCollection</li>
 *   <li>addFile</li>
 *   <li>addFine</li>
 *   <li>addOutgoing</li>
 * </ol>
 * Zwischen den Schritten wird geprüft, ob {@link Exchange#isRouteStop()} gesetzt
 * wurde; dann wird die Ausführung frühzeitig abgebrochen.
 * </p>
 *
 * <p>
 * Konkrete Implementierungen (z. B. {@link EAPL}) müssen die vier
 * abstrakten Hook‑Methoden überschreiben und die jeweilige Geschäftslogik
 * delegieren.
 * </p>
 */
abstract class EAPLTemplate {

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

        addCollection(exchange);
        if (exchange.isRouteStop())
            return;

        addFile(exchange);
        if (exchange.isRouteStop())
            return;

        addFine(exchange);
        if (exchange.isRouteStop())
            return;

        addOutgoing(exchange);

    }

    /**
     * Fügt eine Ziel‑Collection für die weitere Verarbeitung, wenn die Collection nicht schon vorhanden ist.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void addCollection(Exchange exchange);

    /**
     * Fügt eine Akte in die zuvor gefundene Collection ein, wenn die Akte nicht schon vorhanden ist.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void addFile(Exchange exchange);

    /**
     * Fügt einen Vorgang (Bußgeld/Strafe) in die Akte ein.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void addFine(Exchange exchange);

    /**
     * Fügt ausgehende Dokumente in den Vorgang ein.
     *
     * @param exchange Camel Exchange mit Kontext/Message
     */
    protected abstract void addOutgoing(Exchange exchange);

}




