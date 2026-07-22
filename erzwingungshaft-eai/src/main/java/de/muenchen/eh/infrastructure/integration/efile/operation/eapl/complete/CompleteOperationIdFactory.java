package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.ClaimDocument;
import de.muenchen.eh.infrastructure.db.repository.ClaimDocumentRepository;
import de.muenchen.eh.infrastructure.integration.efile.DocumentName;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationIdFactory;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.FileDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.OutgoingRequestBodyDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.OutgoingRequestDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.ProcedureDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.SearchFileDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.contentbuilder.SubjectAreaUnitRequestDTOBuilder;
import de.muenchen.eh.infrastructure.integration.efile.properties.ConnectionProperties;
import de.muenchen.eh.infrastructure.integration.efile.properties.FileProperties;
import de.muenchen.eh.infrastructure.integration.efile.properties.FineProperties;
import jakarta.activation.DataHandler;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompleteOperationIdFactory extends OperationIdFactory {

    private static final String HEADER_OBJADDRESS = "objaddress";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    private static final String GIATTACHMENTTYPE = "giattachmenttype";

    private final FineProperties fineProperties;
    private final FileProperties fileProperties;
    private final ClaimDocumentRepository claimDocumentRepository;

    public CompleteOperationIdFactory(Environment environment, CamelContext camelContext, ConnectionProperties connectionProperties,
            FineProperties fineProperties, FileProperties fileProperties, ClaimDocumentRepository claimDocumentRepository) {
        super(environment, camelContext, connectionProperties);
        this.fineProperties = fineProperties;
        this.fileProperties = fileProperties;
        this.claimDocumentRepository = claimDocumentRepository;
    }

    @PostConstruct
    public void init() {

        operationIdHandlers = Map.of(
                OperationId.READ_COLLECTIONS, wrapper -> this.createExchangeForReadCollections(),
                OperationId.SEARCH_FILE, this::createExchangeSearchFile,
                OperationId.CREATE_FILE, this::createExchangeCaseFile,
                OperationId.UPDATE_USER_FORMS_DATA, wrapper -> createExchangeSubject(wrapper, OperationId.UPDATE_USER_FORMS_DATA),
                OperationId.CREATE_FINE, this::createExchangeFine,
                OperationId.CREATE_OUTGOING, this::createExchangeOutgoing,
                OperationId.CREATE_CONTENT_OBJECT, wrapper -> this.createExchangeContentObject(),
                OperationId.SUBJECT_AREA_UNITS, this::createSubjectAreaUnit);
    }

    private Exchange createSubjectAreaUnit(Object claimContentWrapper) {

        Exchange exchange = createExchange(OperationId.SUBJECT_AREA_UNITS.getDescriptor());
        exchange.getMessage().setBody(SubjectAreaUnitRequestDTOBuilder.create((ClaimContentWrapper) claimContentWrapper, fileProperties).build());

        return exchange;

    }

    private Exchange createExchangeSearchFile(Object dataWrapper) {
        ClaimContentWrapper claimContentWrapper = (ClaimContentWrapper) dataWrapper;
        Exchange exchange = createExchange(OperationId.SEARCH_FILE.getDescriptor());
        exchange.getMessage().setBody(SearchFileDTOBuilder.create(claimContentWrapper.getClaimEfile().getCollection()).build());
        return exchange;
    }

    private Exchange createExchangeSubject(Object claimContentWrapper, OperationId operationId) {
        Exchange exchange = createExchange(operationId.getDescriptor());
        exchange.getMessage().setHeader(HEADER_OBJADDRESS, getObjAddress((ClaimContentWrapper) claimContentWrapper, operationId));
        return exchange;
    }

    private String getObjAddress(Object dataWrapper, OperationId operationId) {
        ClaimContentWrapper claimContentWrapper = (ClaimContentWrapper) dataWrapper;
        return operationId == OperationId.UPDATE_USER_FORMS_DATA
                ? claimContentWrapper.getClaimEfile().getFile()
                : claimContentWrapper.getClaimEfile().getFine();
    }

    private Exchange createExchangeContentObject() {
        Exchange exchange = createExchange(OperationId.CREATE_CONTENT_OBJECT.getDescriptor());
        exchange.getMessage().setBody(null);

        exchange.setException(new NotImplementedException("BePo receipt filing not implemented yet."));

        return exchange;
    }

    private Exchange createExchangeOutgoing(Object dataWrapper) {
        ClaimContentWrapper claimContentWrapper = (ClaimContentWrapper) dataWrapper;
        Exchange exchange = createExchange(OperationId.CREATE_OUTGOING.getDescriptor());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, CONTENT_TYPE_MULTIPART);
        List<ClaimDocument> documents = claimDocumentRepository.findByClaimImportIdOrderByDocumentType(claimContentWrapper.getClaimImport().getId());
        log.debug("Process outgoing geschaeftspartnerId : {} ", claimContentWrapper.getClaimImport().getGeschaeftspartnerId());

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("params", OutgoingRequestDTOBuilder.create(fineProperties, claimContentWrapper).buildAsJson(), ContentType.APPLICATION_JSON);

            var docs = OutgoingRequestBodyDTOBuilder.create(documents).build();
            for (Map.Entry<String, DataHandler> entry : docs.entrySet()) {
                builder.addBinaryBody(GIATTACHMENTTYPE, entry.getValue().getInputStream(), ContentType.APPLICATION_OCTET_STREAM, entry.getKey());
            }

            DataHandler xmlDataHandler = new DataHandler(claimContentWrapper.getXjustizXml(), ContentType.TEXT_XML.getMimeType());
            builder.addBinaryBody(GIATTACHMENTTYPE, xmlDataHandler.getInputStream(), ContentType.TEXT_XML,
                    DocumentName.VERFAHRENSMITTEILUNG.getFullName());

            exchange.getMessage().setBody(builder.build());

        } catch (IOException e) {
            exchange.setException(e);
        }

        return exchange;
    }

    private Exchange createExchangeFine(Object claimContentWrapper) {
        Exchange exchange = createExchange(OperationId.CREATE_FINE.getDescriptor());
        exchange.getMessage().setBody(ProcedureDTOBuilder.create(fineProperties, (ClaimContentWrapper) claimContentWrapper).build());
        return exchange;
    }

    private Exchange createExchangeCaseFile(Object claimContentWrapper) {

        Exchange exchange = createExchange(OperationId.CREATE_FILE.getDescriptor());
        exchange.getMessage().setBody(FileDTOBuilder.create(fileProperties, (ClaimContentWrapper) claimContentWrapper).build());
        return exchange;
    }

}
