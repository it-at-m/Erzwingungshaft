package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.DocumentName;
import de.muenchen.eh.claim.efile.ExchangeBuilder;
import de.muenchen.eh.claim.efile.OpenApiParameterExtractor;
import de.muenchen.eh.claim.efile.operation.contentbuilder.FileDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.OutgoingRequestBodyDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.OutgoingRequestDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.ProcedureDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.SearchFileDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.SubjectAreaUnitRequestDTOBuilder;
import de.muenchen.eh.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.claim.efile.properties.FileProperties;
import de.muenchen.eh.claim.efile.properties.FineProperties;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.log.Constants;
import jakarta.activation.DataHandler;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationIdFactory {

    private static final String EFILE_CASE_FILE_PROPERTIES = "efile.case-file.";
    private static final String HEADER_OBJADDRESS = "objaddress";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    private static final String GIATTACHMENTTYPE = "giattachmenttype";

    private final Environment environment;
    private final CamelContext camelContext;
    private final ConnectionProperties connectionProperties;
    private final FineProperties fineProperties;
    private final FileProperties fileProperties;
    private final ClaimDocumentRepository claimDocumentRepository;

    private Map<OperationId, Function<ClaimContentWrapper, Exchange>> operationIdHandlers;

    @PostConstruct
    public void init() {

        operationIdHandlers = Map.of(
                OperationId.READ_COLLECTIONS, this::createExchangeForReadCollections,
                OperationId.SEARCH_FILE, this::createExchangeSearchFile,
                OperationId.CREATE_FILE, this::createExchangeCaseFile,
                OperationId.UPDATE_BUSINESS_DATA_FILE, wrapper -> createExchangeSubject(wrapper, OperationId.UPDATE_BUSINESS_DATA_FILE),
                OperationId.UPDATE_BUSINESS_DATA_FINE, wrapper -> createExchangeSubject(wrapper, OperationId.UPDATE_BUSINESS_DATA_FINE),
                OperationId.CREATE_FINE, this::createExchangeFine,
                OperationId.CREATE_OUTGOING, this::createExchangeOutgoing,
                OperationId.CREATE_CONTENT_OBJECT, wrapper -> createExchangeContentObject(),
                OperationId.SUBJECT_AREA_UNITS, this::createSubjectAreaUnit);
    }

    public Exchange createExchange(OperationId operationId, Exchange exchange) {

        Exchange efileExchange = operationIdHandlers.get(operationId)
                .apply(exchange.getMessage().getBody(ClaimContentWrapper.class));

        efileExchange.getMessage().setHeader(Constants.OPERATION_ID, operationId.getDescriptor());
        efileExchange.setProperty(Constants.CLAIM, exchange.getProperty(Constants.CLAIM));

        return ExchangeBuilder.create(efileExchange, operationId.getDescriptor())
                .withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword())
                .withRequestValidation(true)
                .build();
    }

    private Exchange createSubjectAreaUnit(ClaimContentWrapper claimContentWrapper) {

        Exchange exchange = createExchange(OperationId.SUBJECT_AREA_UNITS.getDescriptor());
        exchange.getMessage().setBody(SubjectAreaUnitRequestDTOBuilder.create(claimContentWrapper, fileProperties).build());

        return exchange;
    }

    private Exchange createExchangeForReadCollections(ClaimContentWrapper dataWrapper) {
        return createExchange(OperationId.READ_COLLECTIONS.getDescriptor());
    }

    private Exchange createExchangeSearchFile(ClaimContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.SEARCH_FILE.getDescriptor());
        exchange.getMessage().setBody(SearchFileDTOBuilder.create(dataWrapper).build());
        return exchange;
    }

    private Exchange createExchangeSubject(ClaimContentWrapper dataWrapper, OperationId operationId) {
        Exchange exchange = createExchange(operationId.getDescriptor());
        exchange.getMessage().setHeader(HEADER_OBJADDRESS, getObjAddress(dataWrapper, operationId));
        return exchange;
    }

    private String getObjAddress(ClaimContentWrapper dataWrapper, OperationId operationId) {
        return operationId == OperationId.UPDATE_BUSINESS_DATA_FILE
                ? dataWrapper.getClaimEfile().getFile()
                : dataWrapper.getClaimEfile().getFine();
    }

    private Exchange createExchangeContentObject() {
        Exchange exchange = createExchange(OperationId.CREATE_CONTENT_OBJECT.getDescriptor());
        exchange.getMessage().setBody(null);

        exchange.setException(new NotImplementedException("BePo receipt filing not implemented yet."));

        return exchange;
    }

    private Exchange createExchangeOutgoing(ClaimContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_OUTGOING.getDescriptor());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, CONTENT_TYPE_MULTIPART);
        List<ClaimDocument> documents = claimDocumentRepository.findByClaimImportIdOrderByDocumentType(dataWrapper.getClaimImport().getId());
        log.debug("Process outgoing geschaeftspartnerId : {} ", dataWrapper.getClaimImport().getGeschaeftspartnerId());

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("params", OutgoingRequestDTOBuilder.create(fineProperties, dataWrapper).buildAsJson(), ContentType.APPLICATION_JSON);

            var docs = OutgoingRequestBodyDTOBuilder.create(documents).build();
            for (Map.Entry<String, DataHandler> entry : docs.entrySet()) {
                builder.addBinaryBody(GIATTACHMENTTYPE, entry.getValue().getInputStream(), ContentType.APPLICATION_OCTET_STREAM, entry.getKey());
            }

            DataHandler xmlDataHandler = new DataHandler(dataWrapper.getXjustizXml(), ContentType.TEXT_XML.getMimeType());
            builder.addBinaryBody(GIATTACHMENTTYPE, xmlDataHandler.getInputStream(), ContentType.TEXT_XML,
                    DocumentName.VERFAHRENSMITTEILUNG.getFullName());

            exchange.getMessage().setBody(builder.build());

        } catch (IOException e) {
            exchange.setException(e);
        }

        return exchange;
    }

    private Exchange createExchangeFine(ClaimContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_FINE.getDescriptor());
        exchange.getMessage().setBody(ProcedureDTOBuilder.create(fineProperties, dataWrapper).build());
        return exchange;
    }

    private Exchange createExchangeCaseFile(ClaimContentWrapper dataWrapper) {

        Exchange exchange = createExchange(OperationId.CREATE_FILE.getDescriptor());
        exchange.getMessage().setBody(FileDTOBuilder.create(dataWrapper).build());
        return exchange;
    }

    private Exchange createExchange(String operationId) {

        Map<String, Object> params = enrichParameterValues(operationId);

        Exchange exchange = new DefaultExchange(camelContext);
        params.forEach((key, value) -> exchange.getMessage().setHeader(key, value));
        return exchange;
    }

    private Map<String, Object> enrichParameterValues(String operationId) {
        Map<String, Object> params = new HashMap<>();

        OpenApiParameterExtractor extractor = new OpenApiParameterExtractor(connectionProperties.getEakteApiVersion());
        var readApentryParameters = extractor.getParameterNamesForOperation(operationId);

        readApentryParameters.forEach(param -> {
            Optional<String> paramValue = Optional.ofNullable(environment.getProperty(EFILE_CASE_FILE_PROPERTIES.concat(param.replace("-", ""))));
            paramValue.ifPresent(value -> params.put(param, value));
        });
        return params;
    }

}
