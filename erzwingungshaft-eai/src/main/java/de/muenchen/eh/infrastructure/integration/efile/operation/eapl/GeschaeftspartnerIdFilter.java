package de.muenchen.eh.infrastructure.integration.efile.operation.eapl;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that filters eFile object references (Objektreferenz) based on a
 * business-partner id (GeschaeftspartnerId, {@code gpid}).
 *
 * <p>
 * Each {@link Objektreferenz} is expected to expose an object name ({@code objname})
 * that contains a trailing range in the format ".../<from>-<to>" (for example
 * "9512.0/1000000001-1000005000"). The filter returns only those references where the given
 * {@code gpid} lies between the parsed {@code from} and {@code to} (inclusive).
 * </p>
 *
 * <p>
 * The filtering method is declared {@code synchronized} to allow simple external
 * synchronization for callers that may access a shared list/cache concurrently.
 * The method is tolerant to malformed names: any entry with a missing or unparsable
 * range is simply excluded from the result (no exception is propagated).
 * </p>
 */
public class GeschaeftspartnerIdFilter {

    /**
     * Filters the provided list of {@link Objektreferenz} objects and returns those
     * whose object name contains a range specifying the business-partner id interval
     * that includes the supplied {@code gpid}.
     *
     * <p>
     * Behavioural details:
     * <ul>
     * <li>Entries with a null or blank {@code objname} are excluded.</li>
     * <li>The object name is split by '/' and the last segment is expected to be a range
     * in the form "&lt;from&gt;-&lt;to&gt;". If the last segment does not contain exactly
     * two dash-separated parts, the entry is excluded.</li>
     * <li>If parsing of the numbers fails (NumberFormatException), the entry is excluded.</li>
     * <li>The comparison is inclusive: {@code gpid >= from && gpid <= to}.</li>
     * </ul>
     * </p>
     *
     * @param objektList the list of object references to filter; must not be {@code null}
     * @param gpid the business-partner id to test for inclusion in the ranges
     * @return a list with those {@link Objektreferenz} entries whose trailing range contains
     *         {@code gpid};
     *         returns an empty list if none match
     */
    public static synchronized List<Objektreferenz> gpIdFilter(List<Objektreferenz> objektList, long gpid) {

        return objektList.stream()
                .filter(obj -> {

                    if (obj.getObjname() == null || obj.getObjname().isBlank())
                        return false;

                    String objname = obj.getObjname();
                    String[] parts = objname.split("/");

                    String rangePart = parts[parts.length - 1];
                    String[] range = rangePart.split("-");
                    if (range.length != 2) {
                        return false;
                    }
                    try {
                        long gpidVon = Long.parseLong(range[0].trim());
                        long gpidBis = Long.parseLong(range[1].trim());
                        return gpid >= gpidVon && gpid <= gpidBis;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

}
