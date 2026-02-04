package de.muenchen.eh.claim.efile.operation;

import de.muenchen.eh.claim.ClaimContentWrapper;
import de.muenchen.eh.claim.efile.DocumentName;
import de.muenchen.eh.claim.efile.ExchangeBuilder;
import de.muenchen.eh.claim.efile.OpenApiParameterExtractor;
import de.muenchen.eh.claim.efile.operation.contentbuilder.FileDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.OutgoingAnfrageDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.OutgoingRequestBodyDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.ProcedureDTOBuilder;
import de.muenchen.eh.claim.efile.operation.contentbuilder.SearchFileDTOBuilder;
import de.muenchen.eh.claim.efile.properties.ConnectionProperties;
import de.muenchen.eh.claim.efile.properties.FineProperties;
import de.muenchen.eh.db.entity.ClaimDocument;
import de.muenchen.eh.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.log.Constants;
import jakarta.activation.DataHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private final Environment environment;
    private final CamelContext camelContext;
    private final ConnectionProperties connectionProperties;
    private final FineProperties fineProperties;

    private final ClaimDocumentRepository claimDocumentRepository;

    public Exchange createExchange(OperationId operationId, Exchange exchange) {

        Exchange efileExchange;
        switch (operationId) {
        case READ_COLLECTIONS -> {
            efileExchange = createExchange(OperationId.READ_COLLECTIONS.getDescriptor());
        }
        case SEARCH_FILE -> {
            efileExchange = createExchangeSearchFile(exchange.getMessage().getBody(ClaimContentWrapper.class));
        }
        case CREATE_FILE -> {
            efileExchange = createExchangeCaseFile(exchange.getMessage().getBody(ClaimContentWrapper.class));
        }
        case UPDATE_SUBJECT_DATA_FILE -> {
            efileExchange = createExchangeSubject(exchange.getMessage().getBody(ClaimContentWrapper.class), OperationId.UPDATE_SUBJECT_DATA_FILE);
        }
        case UPDATE_SUBJECT_DATA_FINE -> {
            efileExchange = createExchangeSubject(exchange.getMessage().getBody(ClaimContentWrapper.class), OperationId.UPDATE_SUBJECT_DATA_FINE);
        }
        case CREATE_FINE -> {
            efileExchange = createExchangeFine(exchange.getMessage().getBody(ClaimContentWrapper.class));
        }
        case CREATE_OUTGOING -> {
            efileExchange = createExchangeOutgoing(exchange.getMessage().getBody(ClaimContentWrapper.class));
        }
        case CREATE_CONTENT_OBJECT -> {
            efileExchange = createExchangeContentObject();
        }
        default -> {
            efileExchange = new DefaultExchange(camelContext);
        }
        }

        efileExchange.getMessage().setHeader(Constants.OPERATION_ID, operationId.getDescriptor());
        efileExchange.setProperty(Constants.CLAIM, exchange.getProperty(Constants.CLAIM));

        return ExchangeBuilder.create(efileExchange, operationId.getDescriptor())
                .withBasicAuth(connectionProperties.getUsername(), connectionProperties.getPassword()).withRequestValidation(true).build();
    }

    private Exchange createExchangeSearchFile(ClaimContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.SEARCH_FILE.getDescriptor());
        exchange.getMessage().setBody(SearchFileDTOBuilder.create(dataWrapper).build());
        return exchange;
    }

    private Exchange createExchangeSubject(ClaimContentWrapper dataWrapper, OperationId operationId) {
        Exchange exchange = createExchange(operationId.getDescriptor());
        exchange.getMessage().setHeader("objaddress", operationId.compareTo(OperationId.UPDATE_SUBJECT_DATA_FILE) == 0 ? dataWrapper.getClaimEfile().getFile()
                : dataWrapper.getClaimEfile().getFine());

        return exchange;
    }

    private Exchange createExchangeContentObject() {
        Exchange exchange = createExchange(OperationId.CREATE_CONTENT_OBJECT.getDescriptor());
        exchange.getMessage().setBody(null);

        exchange.setException(new NotImplementedException("BePo receipt filing not implemented yet."));

        return exchange;
    }

    private Exchange createExchangeOutgoing(ClaimContentWrapper dataWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_OUTGOING.getDescriptor());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "multipart/form-data");
        List<ClaimDocument> documents = claimDocumentRepository.findByClaimImportIdOrderByDocumentType(dataWrapper.getClaimImport().getId());
        log.debug("Process outgoing geschaeftspartnerId : {} ", dataWrapper.getClaimImport().getGeschaeftspartnerId());

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("params", OutgoingAnfrageDTOBuilder.create(fineProperties, dataWrapper).buildAsJson(), ContentType.APPLICATION_JSON);

            var docs = OutgoingRequestBodyDTOBuilder.create(documents).build();
            for (Map.Entry<String, DataHandler> entry : docs.entrySet()) {
                builder.addBinaryBody("giattachmenttype", entry.getValue().getInputStream(), ContentType.APPLICATION_OCTET_STREAM, entry.getKey());
            }

            DataHandler xmlDataHandler = new DataHandler(dataWrapper.getXjustizXml(), ContentType.TEXT_XML.getMimeType());
            builder.addBinaryBody("giattachmenttype", xmlDataHandler.getInputStream(), ContentType.TEXT_XML,
                    DocumentName.VERFAHRENSMITTEILUNG.getDescriptor().concat(".xml"));

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
