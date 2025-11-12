package de.muenchen.erzwingungshaft.xta.mapper;

import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import genv3.de.xoev.transport.xta.x211.LookupServiceRequest;
import genv3.de.xoev.transport.xta.x211.LookupServiceType;
import genv3.eu.osci.ws.x2008.x05.transport.MsgBoxCloseRequestType;
import genv3.eu.osci.ws.x2008.x05.transport.MsgBoxFetchRequest;
import genv3.eu.osci.ws.x2008.x05.transport.MsgBoxStatusListRequestType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface RequestMapper {

    String IDENTIFIER_TYPE = "xoev";
    String MESSAGE_TYPE_LIST_URI = "urn:de:payloadSchema:elementName";
    String MESSAGE_TYPE_LIST_VERSION_ID = "1.0";
    String CLOSE_REQUEST_ID = "1";

    @Mapping(target = "encryptedData", expression = "java(null)")
    @Mapping(target = "contentContainer.message", source = "messageFile")
    @Mapping(target = "contentContainer.attachment", source = "attachmentFiles")
    GenericContentContainer mapXtaMessageToGenericContentContainer(XtaMessage xtaMessage);

    @Mapping(target = "maxListItems", source = "maxListItems")
    @Mapping(target = "listForm", constant = "MessageMetaData")
    @Mapping(target = "msgSelector", ignore = true)
    MsgBoxStatusListRequestType mapMaxListItemsToMsgBoxStatusListRequestType(Integer maxListItems);

    @Mapping(target = "qualifier", source = ".")
    @Mapping(target = "msgIdentification.messageID.value", source = "messageId")
    @Mapping(target = "originators.author.identifier", source = "authorIdentifier")
    @Mapping(target = "destinations.reader.identifier", source = "readerIdentifier")
    @Mapping(target = "msgSize", source = "messageSize")
    @Mapping(target = "testMsg", ignore = true)
    @Mapping(target = "deliveryAttributes", ignore = true)
    @Mapping(target = "messageProperties", ignore = true)
    MessageMetaData mapXtaMessageMetaDataToMessageMetaData(XtaMessageMetaData xtaMessageMetaData);

    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "messageType", source = ".")
    @Mapping(target = "businessScenario", source = "businessScenario", qualifiedByName = "mapBusinessScenario")
    QualifierType mapXtaMessageMetaDataToQualifierType(XtaMessageMetaData messageMetaData);

    @Mapping(target = "code", source = "messageType.code")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "payloadSchema", source = "messageType.payloadSchema")
    @Mapping(target = "listURI", constant = MESSAGE_TYPE_LIST_URI)
    @Mapping(target = "listVersionID", constant = MESSAGE_TYPE_LIST_VERSION_ID)
    QualifierType.MessageType mapXtaMessageMetaDataToQualifierTypeMessageType(XtaMessageMetaData messageMetaData);

    @Named("mapBusinessScenario")
    default QualifierType.BusinessScenario mapXtaMessageMetaDataToQualifierTypeBusinessScenario(XtaMessageMetaData messageMetaData) {
        final var bs = messageMetaData.businessScenario();
        return (bs != null && bs.listUri() != null)
                ? mapDefinedBusinessScenario(messageMetaData)
                : mapUndefinedBusinessScenario(messageMetaData);
    }

    @Mapping(target = "defined", expression = "java(null)")
    @Mapping(target = "undefined", source = "businessScenario.code")
    QualifierType.BusinessScenario mapUndefinedBusinessScenario(XtaMessageMetaData messageMetaData);

    @Mapping(target = "defined", source = "businessScenario", qualifiedByName = "mapDefinedBusinessScenarioCode")
    @Mapping(target = "undefined", expression = "java(null)")
    QualifierType.BusinessScenario mapDefinedBusinessScenario(XtaMessageMetaData messageMetaData);

    @Named("mapDefinedBusinessScenarioCode")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "listURI", source = "listUri")
    @Mapping(target = "listVersionID", source = "listVersionId")
    KeyCodeType mapDefinedBusinessScenarioCode(XtaBusinessScenario businessScenario);

    @Mapping(target = "securityToken", ignore = true)
    PartyType mapXtaIdentifierToPartyType(XtaIdentifier identifier);

    @Mapping(target = "type", constant = IDENTIFIER_TYPE)
    PartyIdentifierType mapXtaIdentifierToPartyIdentifierType(XtaIdentifier value);

    @Mapping(target = "lookupServiceRequestList", expression = "java( List.of( mapLookupServiceRequestList(service, reader )) )")
    LookupServiceRequest mapStringAndXtaIdentifierToLookupServiceRequest(String service, XtaIdentifier reader);

    @Mapping(target = "lookupService", expression = "java( mapLookupServiceType(service, reader) )")
    LookupServiceRequest.LookupServiceRequestList mapStringAndXtaIdentifierToLookupServiceRequestLookupServiceRequestList(String service, XtaIdentifier reader);

    LookupServiceType mapStringAndXtaIdentifierToLookupServiceRequestLookupServiceType(String serviceType, XtaIdentifier reader);

    @Mapping(target = "msgSelector.messageID", expression = "java( List.of( mapAttributedURIType(string) ) )")
    @Mapping(target = "msgPart", ignore = true)
    MsgBoxFetchRequest mapMessageIdToMsgBoxFetchRequest(String messageId);

    @Mapping(target = "lastMsgReceived", expression = "java( List.of(mapAttributedURIType(messageId)) )")
    @Mapping(target = "msgBoxRequestID", constant = CLOSE_REQUEST_ID)
    MsgBoxCloseRequestType mapMessageIdToMsgBoxCloseRequestType(String messageId);

}
