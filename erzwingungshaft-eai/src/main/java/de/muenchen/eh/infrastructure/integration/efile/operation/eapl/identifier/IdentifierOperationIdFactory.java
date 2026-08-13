package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.identifier;

import de.muenchen.eakte.api.rest.model.CreateProcedureDTO;
import de.muenchen.eakte.api.rest.model.SearchFileRequestDTO;
import de.muenchen.eh.domain.identifier.IdentifierContentWrapper;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationIdFactory;
import de.muenchen.eh.infrastructure.integration.efile.properties.ConnectionProperties;
import de.muenchen.eh.infrastructure.integration.efile.properties.FineProperties;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.Getter;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class IdentifierOperationIdFactory extends OperationIdFactory {

    @Getter
    private final FineProperties fineProperties;

    public IdentifierOperationIdFactory(Environment environment, CamelContext camelContext, ConnectionProperties connectionProperties,
            FineProperties fineProperties) {
        super(environment, camelContext, connectionProperties);
        this.fineProperties = fineProperties;
    }

    @PostConstruct
    public void init() {

        operationIdHandlers = Map.of(
                OperationId.READ_COLLECTIONS, wrapper -> this.createExchangeForReadCollections(),
                OperationId.SEARCH_FILE, this::createExchangeSearchFile,
                OperationId.CREATE_FINE, this::createExchangeFine);
    }

    private Exchange createExchangeSearchFile(Object dataWrapper) {

        IdentifierContentWrapper identifierContentWrapper = (IdentifierContentWrapper) dataWrapper;

        Exchange exchange = createExchange(OperationId.SEARCH_FILE.getDescriptor());
        SearchFileRequestDTO searchFileRequestDTO = new SearchFileRequestDTO();
        searchFileRequestDTO.setApentry(identifierContentWrapper.getEfileIdentifier().getFileCollectionCooAddress());
        exchange.getMessage().setBody(searchFileRequestDTO);
        return exchange;
    }

    private Exchange createExchangeFine(Object dataWrapper) {

        IdentifierContentWrapper identifierContentWrapper = (IdentifierContentWrapper) dataWrapper;
        Exchange exchange = createExchange(OperationId.CREATE_FINE.getDescriptor());

        CreateProcedureDTO createProcedureDTO = new CreateProcedureDTO();
        createProcedureDTO.setShortname(fineProperties.getShortname());
        createProcedureDTO.setDefinition(fineProperties.getEhVorgangDefinition());
        createProcedureDTO.setReferrednumber(identifierContentWrapper.getEfileIdentifier().getFileCooAddress());
        createProcedureDTO.setFilesubj(identifierContentWrapper.getEfileIdentifier().getKassenzeichenEfile());
        exchange.getMessage().setBody(createProcedureDTO);
        return exchange;
    }

}
