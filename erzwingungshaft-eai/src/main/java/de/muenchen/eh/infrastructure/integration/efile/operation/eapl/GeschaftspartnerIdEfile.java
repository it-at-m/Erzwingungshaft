package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.SearchFileResponseDTO;
import de.muenchen.eh.DataWrapper;
import de.muenchen.eh.infrastructure.integration.efile.EfileRouteBuilder;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationId;
import de.muenchen.eh.infrastructure.integration.efile.operation.OperationIdFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

/**
 * Component that checks the eFile system for files related to a specific
 * business partner (Geschaeftspartner) ID.
 *
 * <p>
 * This component creates and sends a {@link OperationId#SEARCH_FILE} request via
 * the injected {@link ProducerTemplate} ({@code efileConnector}) and filters the returned
 * file references for those whose object name contains the business partner id
 * in the expected pattern ("-&lt;gpid&gt;-"). The filtered results are stored in the
 * exchange's DataWrapper under the {@link OperationId#SEARCH_FILE} key.
 * </p>
 *
 * <p>
 * The main public method returns an {@link Optional} containing a sorted list of
 * {@link Objektreferenz} entries or {@link Optional#empty()} when no matching files were found
 * or when the route was stopped (exchange.routeStop()).
 * </p>
 */
@Component
public class GeschaftspartnerIdEfile {

    @Produce(value = EfileRouteBuilder.MARSHAL_JSON_DMS_CONNECTION)
    private ProducerTemplate efileConnector;

    /**
     * Checks whether the eFile contains files for the business partner ID.
     *
     * <p>
     * Behavior:
     * <ul>
     * <li>Creates and sends a SEARCH_FILE request using {@link OperationIdFactory} and
     * {@code efileConnector}.</li>
     * <li>If the response exchange has {@link Exchange#isRouteStop()} set, this method
     * marks the provided exchange as stopped and returns {@link Optional#empty()}.</li>
     * <li>Filters returned {@link Objektreferenz} entries whose object name contains
     * "-&lt;gpid&gt;".</li>
     * <li>Sorts matching entries by the trailing numeric suffix (ascending) and stores the
     * filtered list in the exchange's DataWrapper under {@link OperationId#SEARCH_FILE}.</li>
     * </ul>
     * </p>
     *
     * @param exchange the Camel exchange containing a DataWrapper in the message body
     * @param searchFileExchange the exchange to send as a SEARCH_FILE request
     * @param geschaeftspartnerId the business partner id to match in object names
     * @return an Optional with the filtered and sorted list of {@link Objektreferenz} or
     *         {@link Optional#empty()}
     *         if no matches were found or if the route was stopped
     */
    public Optional<List<Objektreferenz>> checkIfEfileFileWithGpidExists(Exchange exchange, Exchange searchFileExchange, String geschaeftspartnerId) {

        // Check if efile apentry contains file with gpid
        Exchange createSearchFileResponse = efileConnector.send(searchFileExchange);

        if (createSearchFileResponse.isRouteStop()) {
            exchange.setRouteStop(true);
            return Optional.empty();
        }

        SearchFileResponseDTO files = createSearchFileResponse.getMessage().getBody(SearchFileResponseDTO.class);

        List<Objektreferenz> filteredFiles = files.getGiobjecttype().stream()
                .filter(objref -> objref.getObjname().contains("-" + geschaeftspartnerId + "-"))
                .sorted(Comparator.comparingInt(this::extractTrailingNumber))
                .toList();

        // Cache searchFile result
        DataWrapper dataWrapper = exchange.getMessage().getBody(DataWrapper.class);
        dataWrapper.getEfile().put(OperationId.SEARCH_FILE.name(), filteredFiles);

        return filteredFiles.isEmpty() ? Optional.empty() : Optional.of(filteredFiles);
    }

    /**
     * Extracts the trailing numeric suffix from an {@link Objektreferenz}'s object name.
     *
     * <p>
     * Example: for an object name "prefix-123-5" this method will return {@code 5}.
     * It expects that the last '-' character precedes a parseable integer.
     * </p>
     *
     * @param ref the object reference whose name contains a trailing number
     * @return the integer parsed from the substring after the last '-'
     * @throws NumberFormatException if the substring after the last '-' is not a valid integer
     */
    private int extractTrailingNumber(Objektreferenz ref) {
        String s = ref.getObjname();
        return Integer.parseInt(s.substring(s.lastIndexOf('-') + 1));
    }

}
