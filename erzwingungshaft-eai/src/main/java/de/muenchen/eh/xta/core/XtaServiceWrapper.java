package de.muenchen.eh.xta.core;

import de.muenchen.eh.xta.dto.XtaIdentifier;
import de.muenchen.eh.xta.dto.XtaMessage;
import de.muenchen.eh.xta.dto.XtaStatusListing;
import de.muenchen.eh.xta.mapper.RequestMapper;
import de.muenchen.eh.xta.mapper.ResponseMapper;
import genv3.eu.osci.ws.x2008.x05.transport.MsgBoxResponseType;
import genv3.eu.osci.ws.x2008.x05.transport.MsgStatusListType;
import genv3.eu.osci.ws.x2008.x05.transport.X509TokenContainerType;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import jakarta.xml.ws.Holder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class XtaServiceWrapper {

    private final ResponseMapper responseMapper;
    private final RequestMapper requestMapper;
    private final XtaServicePorts xtaServicePorts;

    /**
     * Versendet eine Nachricht über die Operation {@code sendMessage} gemäß Spezifikation
     * XTA_2_Version_3.1.1, Abschnitt 5.4.2.2.2.
     *
     * <p>
     * Die SOAP-Nachricht setzt sich aus folgenden Bestandteilen zusammen:
     * </p>
     * <ul>
     * <li><b>Header</b>: {@code MessageMetaData} – Typ: {@code oscimeta:MessageMetaData} (vgl. Seite
     * 123)</li>
     * <li><b>Header</b>: {@code X509TokenContainer} – Typ: {@code osci:X509TokenContainer}</li>
     * <li><b>Body</b>: {@code GenericContentContainer} – Typ: {@code xta:GenericContentContainer} (vgl.
     * Seite 154)</li>
     * </ul>
     *
     * <p>
     * Wesentliche Parameter:
     * </p>
     * <ul>
     * <li>
     * {@code oscimeta:MessageMetaData}:
     * In dieser Struktur werden die Metadaten des Transportauftrags definiert. Das Objekt ist als
     * mandatorischer
     * Parameter zu verwenden. Da die Daten dann als SOAPHeader zur Verfügung stehen, muss für diverse
     * Zwecke
     * nur dieser Header und nicht die ggf. eingebettete Fachnachricht gelesen werden. Die Metadaten
     * beinhalten
     * Zeitstempel, Quittungsanforderungen, das Service Profil, Angaben über den Autoren und den Leser,
     * Informationen zur Identifikation der Fachnachricht und weitere Informationen.
     * </li>
     * <li>
     * {@code osci:X509TokenContainer}:
     * In diesem optionalen SOAP-Header können zu prüfende Zertifikate eingestellt werden. Die Prüfung
     * kann
     * auf dem Transportweg durchgeführt werden und ist eine optionale Serviceleistung der
     * Transportinfrastruktur.
     * </li>
     * <li>
     * {@code xta:GenericContentContainer}:
     * Dieses Objekt beinhaltet die zu übertragende Fachnachricht und eine beliebige Anzahl von Anhängen
     * (Attachments).
     * Die Fachnachricht kann in einem verschlüsselten Container hinterlegt werden. Zu der Fachnachricht
     * kann ein Betreff
     * (Subject) angegeben werden.
     * </li>
     * </ul>
     *
     * @param xtaMessage die zu versendende Nachricht inklusive Metadaten und Nutzdaten
     * @throws SyncAsyncException wenn eine asynchrone Nachricht synchron verarbeitet werden soll oder
     *             umgekehrt
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws MessageVirusDetectionException wenn in der Nachricht ein Virus festgestellt wurde
     * @throws MessageSchemaViolationException wenn die Nachricht nicht dem erwarteten XML-Schema
     *             entspricht
     * @throws PermissionDeniedException wenn keine Berechtigung zum Versand der Nachricht besteht
     * @throws ParameterIsNotValidException wenn einer der übergebenen Parameter ungültig ist
     */
    public void sendMessage(XtaMessage xtaMessage, X509TokenContainerType x509TokenContainerType) throws SyncAsyncException, XTAWSTechnicalProblemException,
            MessageVirusDetectionException, MessageSchemaViolationException, PermissionDeniedException, ParameterIsNotValidException {
        xtaServicePorts.sendPortType().sendMessage(
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
     * <li><b>Header</b>: {@code AuthorIdentifier} – Typ: {@code xta:AuthorIdentifier} (optional)</li>
     * <li><b>Body</b>: leer (bzw. leeres {@code PartyType}-Objekt)</li>
     * <li><b>Antwort</b>: {@code wsa:MessageID} – Rückgabe der erzeugten eindeutigen ID</li>
     * </ul>
     *
     * <p>
     * Die erzeugte MessageID kann anschließend für den Transportauftrag (z. B. in
     * {@code MessageMetaData})
     * verwendet werden. Diese ID dient als Referenz für Folgeoperationen wie Quittung, Statusabfrage
     * oder Transportreport.
     * </p>
     *
     * @return die vom XTA-Dienst erzeugte eindeutige MessageID als {@code String}
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn der Zugriff auf die Operation nicht autorisiert ist
     */
    public String createMessageId(XtaIdentifier xtaIdentifier) throws XTAWSTechnicalProblemException, PermissionDeniedException {
        return xtaServicePorts
                .managementPortType()
                .createMessageId(requestMapper.mapXtaIdentifierToPartyType(xtaIdentifier))
                .getValue();
    }

    /**
     * Holt eine einzelne Nachricht anhand ihrer {@code MessageID} ab ({@code getMessage}).
     *
     * <p>
     * Zweck der Methode ist der Abholvorgang aus der Message-Box: <em>„Mit der Methode getMessage holt
     * der Leser eine Nachricht vom Empfänger ab.“</em>
     * (XTA 2, Version 3.1.1, Abschnitt 5.4.3.2). In der aktuellen Spezifikationsversion erfolgt die
     * Selektion ausschließlich über die {@code MessageID}.
     * </p>
     *
     * <p>
     * <b>SOAP-Struktur</b> (vgl. 5.4.3.2 „Operation getMessage“):
     * </p>
     * <ul>
     * <li><b>Header (Input)</b>: {@code AuthorIdentifier} – Typ {@code oscimeta:PartyType}</li>
     * <li><b>Body (Input)</b>: {@code MsgBoxFetchRequest} – inkl. {@code MsgSelector} mit
     * {@code wsa:MessageID}</li>
     * <li><b>Header (Output)</b>: {@code oscimeta:MessageMetaData} und {@code osci:MsgBoxResponse}</li>
     * <li><b>Body (Output)</b>: {@code xta:GenericContentContainer} (Fachnachricht inkl.
     * Attachments)</li>
     * </ul>
     *
     * <p>
     * Hinweis: Der Status der abgeholten Nachricht wird beim Empfänger erst nach Quittierung via
     * {@code close} auf „abgeholt“ gesetzt
     * (vgl. 5.4.3.2.1: „…der Status auf 'abgeholt' geändert wird, nachdem die Transaktion durch Aufruf
     * der Methode &lt;close&gt; bestätigt worden ist.“).
     * </p>
     *
     * @param messageId die {@code MessageID} des zugehörigen Transportauftrags
     * @param consignorIdentifier fachliche Identität des Lesers (Party/AuthorIdentifier)
     * @return die abgeholte Nachricht samt Metadaten als {@link XtaMessage}
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn kein autorisierter Zugriff vorliegt
     * @throws InvalidMessageIDException wenn die angeforderte Nachricht dem Account nicht bekannt ist
     * @see <a href=
     *      "#close(java.lang.String,de.muenchen.erzwingungshaft.xta.dto.XtaIdentifier)">close</a> zur
     *      Quittierung
     */
    public XtaMessage getMessage(String messageId, XtaIdentifier consignorIdentifier)
            throws XTAWSTechnicalProblemException, PermissionDeniedException, InvalidMessageIDException {
        final Holder<MessageMetaData> messageMetaDataHolder = new Holder<>();
        GenericContentContainer genericContentContainer = xtaServicePorts.msgBoxPortType().getMessage(
                requestMapper.mapMessageIdToMsgBoxFetchRequest(messageId),
                requestMapper.mapXtaIdentifierToPartyType(consignorIdentifier),
                messageMetaDataHolder,
                null);

        // Mapping der OSCI/XTA-Strukturen in das Domänenmodell
        return responseMapper.mapGccMmmToXtaMessage(genericContentContainer, messageMetaDataHolder.value);
    }

    /**
     * Quittiert die Abholung von Nachrichten bzw. (Teil-)Listen und gibt serverseitige Ressourcen frei
     * ({@code close}).
     *
     * <p>
     * Ziel der Methode ist es sicherzustellen, „dass Nachrichten oder Listen nicht mehrfach verarbeitet
     * werden“
     * (XTA 2, Version 3.1.1, Abschnitt 5.4.3.3). Die Empfangsquittierung soll zeitnah erfolgen; mit
     * {@code close}
     * wird die zugehörige Transaktion beendet und z. B. ein Iterator freigegeben.
     * </p>
     *
     * <p>
     * <b>SOAP-Struktur</b> (vgl. 5.4.3.3 „Operation close“):
     * </p>
     * <ul>
     * <li><b>Header (Input)</b>: {@code AuthorIdentifier} – Typ {@code oscimeta:PartyType}</li>
     * <li><b>Body (Input)</b>: {@code MsgBoxCloseRequest} – i. d. R. mit der zugehörigen
     * {@code wsa:MessageID}/Ressourcenkennung</li>
     * <li><b>Output</b>: kein Rückgabewert; Erfolg signalisiert Freigabe/Quittierung</li>
     * </ul>
     *
     * @param messageId die {@code MessageID} des zuvor abgeholten Elements (Nachricht oder Liste)
     * @param consignorIdentifier fachliche Identität des Lesers (Party/AuthorIdentifier)
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn kein autorisierter Zugriff vorliegt
     * @throws InvalidMessageIDException wenn die referenzierte Ressource unbekannt ist
     */
    public void close(String messageId, XtaIdentifier consignorIdentifier)
            throws XTAWSTechnicalProblemException, PermissionDeniedException, InvalidMessageIDException {
        xtaServicePorts.msgBoxPortType().close(
                requestMapper.mapMessageIdToMsgBoxCloseRequestType(messageId),
                requestMapper.mapXtaIdentifierToPartyType(consignorIdentifier));
    }

    /**
     * Ruft Statusinformationen zu eingegangenen Nachrichten ab ({@code getStatusList}) und liefert eine
     * (Teil-)Liste
     * von {@code MessageID}s inkl. Metadaten.
     *
     * <p>
     * Der Leser kann die Ergebnisliste über Selektionskriterien (z. B. Zeitraum, „nur neue“)
     * einschränken und anhand
     * der zurückgelieferten Metadaten (Autor, Leser, Betreff, {@code MessageID} etc.) entscheiden,
     * welche Nachrichten
     * im nächsten Schritt per {@code getMessage} abgeholt werden (XTA 2, Version 3.1.1, Abschnitt
     * 5.4.3.1).
     * </p>
     *
     * <p>
     * <b>SOAP-Struktur</b> (vgl. 5.4.3.1 „Operation getStatusList“):
     * </p>
     * <ul>
     * <li><b>Header (Input)</b>: {@code AuthorIdentifier} – Typ {@code oscimeta:PartyType}</li>
     * <li><b>Body (Input)</b>: {@code MsgBoxStatusListRequest}</li>
     * <li><b>Header (Output)</b>: {@code osci:MsgBoxResponse} (Metainformationen zur Anfrage)</li>
     * <li><b>Body (Output)</b>: {@code osci:MsgStatusListType} (enthält u. a.
     * {@code oscimeta:MessageMetaData})</li>
     * </ul>
     *
     * <p>
     * <b>Implementierungshinweis</b>: Diese Methode kombiniert die OSCI-Antwortbestandteile zu einem
     * Domänenobjekt
     * {@link XtaStatusListing} (Mapping von {@code MsgStatusListType} sowie
     * {@code MsgBoxResponseType}).
     * </p>
     *
     * @param xtaIdentifier fachliche Identität des Lesers (Party/AuthorIdentifier)
     * @param maxListItems optionale Obergrenze für die Anzahl zurückgelieferter Einträge
     * @return kombinierte Statusliste einschließlich Metadaten
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn kein autorisierter Zugriff vorliegt
     * @see #getMessage(String, XtaIdentifier)
     */
    public XtaStatusListing getStatusList(XtaIdentifier xtaIdentifier, Integer maxListItems) throws XTAWSTechnicalProblemException, PermissionDeniedException {
        Holder<MsgBoxResponseType> responseTypeHolder = new Holder<>();
        MsgStatusListType responseListe = xtaServicePorts
                .msgBoxPortType()
                .getStatusList(
                        requestMapper.mapMaxListItemsToMsgBoxStatusListRequestType(maxListItems),
                        requestMapper.mapXtaIdentifierToPartyType(xtaIdentifier),
                        responseTypeHolder);

        // Kombiniertes Mapping von Liste + Response-Header
        return responseMapper.mapMsgStatusListTypeToXtaStatusListing(responseListe, responseTypeHolder);
    }

    /**
     * Prüft die Erreichbarkeit eines Lesers für einen bestimmten Dienst ({@code lookupService}).
     *
     * <p>
     * Die Methode dient dazu festzustellen, „ob ein Leser prinzipiell für einen Dienst elektronisch
     * erreichbar ist“
     * (z. B. via Verzeichnisdienste wie DVDV). Das Ergebnis unterscheidet u. a.
     * {@code ServiceIsAvailable},
     * {@code ServiceIsAvailableUnknown} usw. (XTA 2, Version 3.1.1, Abschnitt 5.4.1.2).
     * </p>
     *
     * @param service fachliche Dienstbezeichnung (ServiceType)
     * @param reader fachliche Identität des Lesers (ReaderIdentifier)
     * @param clientIdentifier fachliche Identität des Autors/Clients (AuthorIdentifier)
     * @return Ergebnisobjekt des Lookup-Vorgangs
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn kein autorisierter Zugriff vorliegt
     * @throws ParameterIsNotValidException wenn {@code ServiceType} oder {@code ReaderIdentifier}
     *             ungültig sind
     */
    public LookupServiceResponse lookupService(String service, XtaIdentifier reader, XtaIdentifier clientIdentifier)
            throws XTAWSTechnicalProblemException, PermissionDeniedException, ParameterIsNotValidException {
        return xtaServicePorts
                .managementPortType()
                .lookupService(
                        requestMapper.mapStringAndXtaIdentifierToLookupServiceRequest(service, reader),
                        requestMapper.mapXtaIdentifierToPartyType(clientIdentifier));
    }

    /**
     * Prüft die Verfügbarkeit des XTA-WS und ob der Account aktiv ist ({@code checkAccountActive}).
     *
     * <p>
     * Der Autor kann damit „prüfen, ob seine Verbindung zum Sender funktioniert“; typischer Aufruf nach
     * Konfigurationsänderungen oder bei technischen Problemen (XTA 2, Version 3.1.1, Abschnitt
     * 5.4.1.1).
     * </p>
     *
     * @param xtaIdentifier fachliche Identität des Autors/Lesers (AuthorIdentifier/PartyType)
     * @throws XTAWSTechnicalProblemException bei technischen Problemen auf Seiten der
     *             XTA-Transportinfrastruktur
     * @throws PermissionDeniedException wenn kein autorisierter Zugriff vorliegt
     */
    public void checkAccountActive(XtaIdentifier xtaIdentifier) throws XTAWSTechnicalProblemException, PermissionDeniedException {
        xtaServicePorts
                .managementPortType()
                .checkAccountActive(requestMapper.mapXtaIdentifierToPartyType(xtaIdentifier));
    }
}
