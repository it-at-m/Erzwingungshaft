package de.muenchen.eh.infrastructure.integration.efile.operation.eapl.complete;

import de.muenchen.eakte.api.rest.model.CreateOutgoingAntwortDTO;
import de.muenchen.eakte.api.rest.model.DmsObjektResponse;
import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.domain.claim.ClaimContentWrapper;
import de.muenchen.eh.infrastructure.db.entity.ClaimEfile;
import de.muenchen.eh.infrastructure.db.repository.ClaimEfileRepository;
import de.muenchen.eh.infrastructure.integration.efile.DocumentName;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.AbstractEAPLExecute;
import de.muenchen.eh.infrastructure.log.Constants;
import de.muenchen.eh.infrastructure.log.LogServiceClaim;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

/**
 * Base class for EAPL operations that provides common infrastructure and persistence helpers.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Expose a {@code ProducerTemplate} (efileConnector) for making eFile-related Camel
 * requests.</li>
 * <li>Provide shared collaborators needed by concrete EAPL operations: {@code OperationIdFactory},
 * {@code LogServiceClaim} and {@code ClaimEfileRepository}.</li>
 * <li>Provide helper method {@link #persistClaimEfileReference(Exchange, OperationId)} to
 * map response payloads from eFile operations into a {@code ClaimEfile} entity and persist it.</li>
 * <li>Ensure the efileConnector's Camel context is started on initialization.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Subclasses perform specific EAPL steps and may call
 * {@link #persistClaimEfileReference(Exchange, OperationId)}
 * to persist collection/file/outgoing/fine references returned by eFile API calls.
 * </p>
 */

@RequiredArgsConstructor
public abstract class AbstractEAPLOperation extends AbstractEAPLExecute {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    protected ProducerTemplate efileConnector;

    protected final CompleteOperationIdFactory completeOperationIdFactory;

    protected final LogServiceClaim logServiceClaim;

    protected final ClaimEfileRepository claimEfileRepository;

    /**
     * Persist or update the ClaimEfile reference based on the current contents of the provided
     * {@link Exchange}.
     *
     * <p>
     * This method expects the {@code Exchange} message body to contain a {@link ClaimContentWrapper}.
     * It will either create a new {@link ClaimEfile} (if none exists on the wrapper) or update the
     * existing one depending on {@code operationId}. The mapping performed:
     * <ul>
     * <li>{@link OperationId#READ_COLLECTIONS} -> sets {@code collection} from
     * {@link Objektreferenz#getObjaddress()}</li>
     * <li>{@link OperationId#SEARCH_FILE} -> sets {@code file} from last
     * {@link Objektreferenz#getObjaddress()}</li>
     * <li>{@link OperationId#CREATE_FILE} -> sets {@code file} from
     * {@link DmsObjektResponse#getObjid()}</li>
     * <li>{@link OperationId#CREATE_FINE} -> sets {@code fine} from
     * {@link DmsObjektResponse#getObjid()}</li>
     * <li>{@link OperationId#CREATE_OUTGOING} -> sets {@code outgoing} and maps returned documents to
     * specific
     * {@code ClaimEfile} document address fields (antrag, bescheid, kosten, verwerfung, xml)</li>
     * </ul>
     * </p>
     *
     * <p>
     * The method persists the {@link ClaimEfile} via {@link #claimEfileRepository} and returns the
     * saved entity.
     * If an unexpected operationId or document type is encountered, an {@link IllegalArgumentException}
     * is set
     * on the {@code Exchange} via {@code exchange.setException(...)}.
     * </p>
     *
     * @param exchange the Camel {@link Exchange} containing a {@link ClaimContentWrapper} in its
     *            message body
     * @param operationId the eFile operation identifier that determines how to extract and map the
     *            response payload
     * @return the persisted {@link ClaimEfile} entity
     */
    protected ClaimEfile persistClaimEfileReference(final Exchange exchange, final OperationId operationId) {

        ClaimContentWrapper dataWrapper = exchange.getMessage().getBody(ClaimContentWrapper.class);

        ClaimEfile claimEfile;
        if (dataWrapper.getClaimEfile() == null) {
            claimEfile = new ClaimEfile();
            claimEfile.setClaim(dataWrapper.getClaim());
        } else {
            claimEfile = dataWrapper.getClaimEfile();
        }

        switch (operationId) {
        case READ_COLLECTIONS -> {
            claimEfile.setCollection(((Objektreferenz) dataWrapper.getEfile().get(operationId.name())).getObjaddress());
        }
        case SEARCH_FILE -> {
            claimEfile.setFile(((List<Objektreferenz>) dataWrapper.getEfile().get(operationId.name())).getLast().getObjaddress());
        }
        case CREATE_FILE -> {
            claimEfile.setFile(((DmsObjektResponse) dataWrapper.getEfile().get(operationId.name())).getObjid());
        }
        case CREATE_FINE -> {
            claimEfile.setFine(((DmsObjektResponse) dataWrapper.getEfile().get(operationId.name())).getObjid());
        }
        case CREATE_OUTGOING -> {
            CreateOutgoingAntwortDTO outgoing = ((CreateOutgoingAntwortDTO) dataWrapper.getEfile().get(operationId.name()));
            claimEfile.setOutgoing(outgoing.getObjid());
            outgoing.getGiobjecttype().forEach(doc -> {
                if (doc.getObjname().equals(DocumentName.ANTRAG.getDescriptor())) {
                    claimEfile.setAntragDocument(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.BESCHEID.getDescriptor())) {
                    claimEfile.setBescheidDocument(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.KOSTEN.getDescriptor())) {
                    claimEfile.setKostendokument(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.VERWERFUNG.getDescriptor())) {
                    claimEfile.setVerwerfung(doc.getObjaddress());
                } else if (doc.getObjname().equals(DocumentName.VERFAHRENSMITTEILUNG.getDescriptor())) {
                    claimEfile.setXml(doc.getObjaddress());
                } else {
                    exchange.setException(
                            new IllegalArgumentException("Unexpected document type: " + doc.getObjname()));
                }
            });
        }
        default -> {
            exchange.setException(
                    new IllegalArgumentException("Unknown openapi.operationId : ".concat((String) exchange.getMessage().getHeader(Constants.OPERATION_ID))));
        }
        }
        return claimEfileRepository.save(claimEfile);
    }

    /**
     * Ensure that the producer template's CamelContext is started.
     *
     * <p>
     * This method is annotated with {@link PostConstruct} and will attempt to start the
     * {@code efileConnector}
     * Camel context if it is not already running. This ensures that subsequent usage of
     * {@code efileConnector}
     * in subclasses and helper methods can safely send exchanges.
     * </p>
     */
    @PostConstruct
    public void init() {
        if (!efileConnector.getCamelContext().isStarted()) {
            efileConnector.getCamelContext().start();
        }
    }

}
