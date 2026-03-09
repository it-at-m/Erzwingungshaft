package de.muenchen.eh.claim.efile;

/**
 * Utility class for generating a range of values in 5000 steps based on a given ID.
 */
public class GpidRangeGenerator {

    public static String[] counterAndRangeSplitted (Long id){
        return generateRangeWithAutoCounter(id).split("/");
    }

    /**
     * Generates a range string in the format "[i]/FROM-TO" based on the given ID.
     * The range is calculated in 5000 steps, and the counter is derived from the range.
     *
     * @param id The input ID for which the range is to be generated.
     * @return A string representing the range in the format "[i]/FROM-TO".
     */
    public static String generateRangeWithAutoCounter(long id) {

        long lowerBound = ((id - 1) / 5000) * 5000 + 1;
        long upperBound = lowerBound + 4999;
        int i = (int) ((lowerBound - 1) / 5000);

        int firstBlockLowerBound = 1000000001;
        int firstBlockCounter = (firstBlockLowerBound - 1) / 5000;
        i -= firstBlockCounter;

        return String.format("%d/%d-%d", i, lowerBound, upperBound);
    }

}
