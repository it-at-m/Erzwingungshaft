package de.muenchen.erzwingungshaft.xta.core;

import de.muenchen.erzwingungshaft.xta.dto.XtaMessage;
import de.muenchen.erzwingungshaft.xta.mapper.RequestMapper;
import genv3.de.xoev.transport.xta.x211.*;
import genv3.eu.osci.ws.x2008.x05.transport.X509TokenContainerType;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import genv3.eu.osci.ws.x2014.x10.transport.PartyType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.springframework.web.bind.annotation.RequestMapping;

@Getter
@RequiredArgsConstructor
public class XtaServiceWrapper {

    private final RequestMapper requestMapper;
    private final XtaPorts xtaPorts;

    /**
     * Versendet eine Nachricht über die Operation {@code sendMessage} gemäß Spezifikation XTA_2_Version_3.1.1, Abschnitt 5.4.2.2.2.
     *
     * <p>
     * Die SOAP-Nachricht setzt sich aus folgenden Bestandteilen zusammen:
     * </p>
     * <ul>
     *   <li><b>Header</b>: {@code MessageMetaData} – Typ: {@code oscimeta:MessageMetaData} (vgl. Seite 123)</li>
     *   <li><b>Header</b>: {@code X509TokenContainer} – Typ: {@code osci:X509TokenContainer}</li>
     *   <li><b>Body</b>: {@code GenericContentContainer} – Typ: {@code xta:GenericContentContainer} (vgl. Seite 154)</li>
     * </ul>
     *
     * <p>
     * Wesentliche Parameter:
     * </p>
     * <ul>
     *   <li>
     *     {@code oscimeta:MessageMetaData}:
     *     In dieser Struktur werden die Metadaten des Transportauftrags definiert. Das Objekt ist als mandatorischer
     *     Parameter zu verwenden. Da die Daten dann als SOAPHeader zur Verfügung stehen, muss für diverse Zwecke
     *     nur dieser Header und nicht die ggf. eingebettete Fachnachricht gelesen werden. Die Metadaten beinhalten
     *     Zeitstempel, Quittungsanforderungen, das Service Profil, Angaben über den Autoren und den Leser,
     *     Informationen zur Identifikation der Fachnachricht und weitere Informationen.
     *   </li>
     *   <li>
     *     {@code osci:X509TokenContainer}:
     *     In diesem optionalen SOAP-Header können zu prüfende Zertifikate eingestellt werden. Die Prüfung kann
     *     auf dem Transportweg durchgeführt werden und ist eine optionale Serviceleistung der Transportinfrastruktur.
     *   </li>
     *   <li>
     *     {@code xta:GenericContentContainer}:
     *     Dieses Objekt beinhaltet die zu übertragende Fachnachricht und eine beliebige Anzahl von Anhängen (Attachments).
     *     Die Fachnachricht kann in einem verschlüsselten Container hinterlegt werden. Zu der Fachnachricht kann ein Betreff
     *     (Subject) angegeben werden.
     *   </li>
     * </ul>
     *
     * @param xtaMessage die zu versendende Nachricht inklusive Metadaten und Nutzdaten
     * @throws SyncAsyncException wenn eine asynchrone Nachricht synchron verarbeitet werden soll oder umgekehrt
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der XTA-Transportinfrastruktur
     * @throws MessageVirusDetectionException wenn in der Nachricht ein Virus festgestellt wurde
     * @throws MessageSchemaViolationException wenn die Nachricht nicht dem erwarteten XML-Schema entspricht
     * @throws PermissionDeniedException wenn keine Berechtigung zum Versand der Nachricht besteht
     * @throws ParameterIsNotValidException wenn einer der übergebenen Parameter ungültig ist
     */
    public void sendMessage(XtaMessage xtaMessage, X509TokenContainerType x509TokenContainerType) throws SyncAsyncException, XTAWSTechnicalProblemException, MessageVirusDetectionException, MessageSchemaViolationException, PermissionDeniedException, ParameterIsNotValidException {
        xtaPorts.sendPortType().sendMessage(
                        requestMapper.mapXtaMessageToGenericContentContainer(xtaMessage),
                        requestMapper.mapXtaMessageMetaDataToMessageMetaData(xtaMessage.metaData()),
                        x509TokenContainerType);
    }

    /**
     * Ruft über die Operation {@code createMessageId} eine neue eindeutige MessageID ab.
     * <p>
     * Die Operation wird gemäß XTA_2_Version_3.1.1, Abschnitt 5.4.2.1.6 verwendet, um
     * vor dem Versand eine gültige, systemweit eindeutige MessageID vom XTA-Dienst zu erhalten.
     * </p>
     *
     * <p>
     * Die SOAP-Nachricht setzt sich aus folgenden Bestandteilen zusammen:
     * </p>
     * <ul>
     *   <li><b>Header</b>: {@code AuthorIdentifier} – Typ: {@code xta:AuthorIdentifier} (optional)</li>
     *   <li><b>Body</b>: leer (bzw. leeres {@code PartyType}-Objekt)</li>
     *   <li><b>Antwort</b>: {@code wsa:MessageID} – Rückgabe der erzeugten eindeutigen ID</li>
     * </ul>
     *
     * <p>
     * Die erzeugte MessageID kann anschließend für den Transportauftrag (z. B. in {@code MessageMetaData})
     * verwendet werden. Diese ID dient als Referenz für Folgeoperationen wie Quittung, Statusabfrage
     * oder Transportreport.
     * </p>
     *
     * @return die vom XTA-Dienst erzeugte eindeutige MessageID als {@code String}
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn der Zugriff auf die Operation nicht autorisiert ist
     */
    public String createMessageId() throws XTAWSTechnicalProblemException, PermissionDeniedException {
        PartyType partyType = new PartyType();
        return xtaPorts.managementPortType().createMessageId(partyType).getValue();
    }
}
