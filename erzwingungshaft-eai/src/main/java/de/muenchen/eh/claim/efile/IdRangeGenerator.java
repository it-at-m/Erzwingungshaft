package de.muenchen.eh.claim.efile;

/**
 * Utility class for generating a range of values in 5000 steps based on a given ID.
 */
public class IdRangeGenerator {

    public static String formatCollectionName (String prefix, Long id){
        return prefix.concat(generateRange(id));
    }

    /**
     * Generates a range string in the format "FROM-TO" based on the given ID.
     * The range is calculated in 5000 steps.
     *
     * @param id The input ID for which the range is to be generated.
     * @return A string representing the range in the format "FROM-TO".
     */
    public static String generateRange(long id) {
        // Calculate the lower bound of the range
        long lowerBound = ((id - 1) / 5000) * 5000 + 1;
        // Calculate the upper bound of the range
        long upperBound = lowerBound + 4999;

        return String.format("%d-%d", lowerBound, upperBound);
    }

}
