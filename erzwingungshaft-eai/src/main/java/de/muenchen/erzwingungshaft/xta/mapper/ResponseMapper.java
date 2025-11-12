package de.muenchen.erzwingungshaft.xta.mapper;

import genv3.de.xoev.transport.xta.x211.GenericContentContainer;
import genv3.de.xoev.transport.xta.x211.IsServiceAvailableValueType;
import genv3.de.xoev.transport.xta.x211.LookupServiceResponse;
import genv3.de.xoev.transport.xta.x211.LookupServiceResultType;
import genv3.eu.osci.ws.x2008.x05.transport.MsgBoxResponseType;
import genv3.eu.osci.ws.x2008.x05.transport.MsgStatusListType;
import genv3.eu.osci.ws.x2014.x10.transport.MessageMetaData;
import genv3.eu.osci.ws.x2014.x10.transport.QualifierType;
import jakarta.xml.ws.Holder;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ResponseMapper {

    default Boolean isServiceAvailable(LookupServiceResponse lookupServiceResponse) {
        return !lookupServiceResponse
                .getLookupServiceResultList()
                .getLookupServiceResult().isEmpty()
                && lookupServiceResponse
                        .getLookupServiceResultList()
                        .getLookupServiceResult()
                        .stream()
                        .map(LookupServiceResultType::getIsServiceAvailableValue)
                        .map(IsServiceAvailableValueType::isServiceIsAvailable)
                        .map(Optional::ofNullable)
                        .allMatch(serviceAvailable -> serviceAvailable.orElse(false));
    }

    @Mapping(target = "itemsPending", source = "responseTypeHolder.itemsPending", defaultValue = "0")
    @Mapping(target = "messages", source = "msgStatusListType.messageMetaData")
    XtaStatusListing mapMsgStatusListTypeToXtaStatusListing(MsgStatusListType msgStatusListType, Holder<MsgBoxResponseType> responseTypeHolder);

    @Mapping(target = "messageFile", source = "genericContentContainer.contentContainer.message")
    @Mapping(target = "attachmentFiles", source = "genericContentContainer.contentContainer.attachment")
    XtaMessage mapGccMmmToXtaMessage(GenericContentContainer genericContentContainer, MessageMetaData metaData);

    @Mapping(target = "service", source = "qualifier.service")
    @Mapping(target = "businessScenario", source = "qualifier.businessScenario")
    @Mapping(target = "messageType", source = "qualifier.messageType")
    @Mapping(target = "messageId", source = "msgIdentification.messageID.value")
    @Mapping(target = "authorIdentifier", source = "originators.author.identifier")
    @Mapping(target = "readerIdentifier", source = "destinations.reader.identifier")
    @Mapping(target = "messageSize", source = "msgSize")
    @Mapping(target = "deliveryAttributesOrigin", source = "deliveryAttributes.origin")
    @Mapping(target = "deliveryAttributesDelivery", source = "deliveryAttributes.delivery")
    XtaMessageMetaData mapMessageMetaDataToXtaMessageMetaData(MessageMetaData source);

    @Mapping(target = "listUri", source = "defined.listURI")
    @Mapping(target = "listVersionId", source = "defined.listVersionID")
    @Mapping(target = "code", source = ".", qualifiedByName = "mapBusinessScenarioCode")
    XtaBusinessScenario mapMessageMetadataToXtaBusinessScenario(QualifierType.BusinessScenario source);

    @Named("mapBusinessScenarioCode")
    String mapBusinessScenarioCode(QualifierType.BusinessScenario source);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "payloadSchema", source = "payloadSchema")
    XtaMessageType mapXtaMessageType(QualifierType.MessageType source);

}
