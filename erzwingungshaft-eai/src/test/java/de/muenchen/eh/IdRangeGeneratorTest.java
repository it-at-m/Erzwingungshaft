package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.eh.claim.efile.GpidRangeGenerator;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class IdRangeGeneratorTest {

    @Test
    void test_explicitIntervals() {

        // Lower edge
        assertEquals("-1/999995001-1000000000", GpidRangeGenerator.generateRangeWithAutoCounter(1000000000L));

        // Lower limit
        assertEquals("0/1000000001-1000005000", GpidRangeGenerator.generateRangeWithAutoCounter(1000000001L));
        assertEquals("1/1000005001-1000010000", GpidRangeGenerator.generateRangeWithAutoCounter(1000005001L));
        assertEquals("2/1000010001-1000015000", GpidRangeGenerator.generateRangeWithAutoCounter(1000010001L));
        assertEquals("3/1000015001-1000020000", GpidRangeGenerator.generateRangeWithAutoCounter(1000015001L));

        // Upper limit
        assertEquals("0/1000000001-1000005000", GpidRangeGenerator.generateRangeWithAutoCounter(1000005000L));
        assertEquals("1/1000005001-1000010000", GpidRangeGenerator.generateRangeWithAutoCounter(1000010000L));
        assertEquals("2/1000010001-1000015000", GpidRangeGenerator.generateRangeWithAutoCounter(1000015000L));
        assertEquals("3/1000015001-1000020000", GpidRangeGenerator.generateRangeWithAutoCounter(1000020000L));

        assertEquals("399/1001995001-1002000000", GpidRangeGenerator.generateRangeWithAutoCounter(1002000000));

        // upper edge
        assertEquals("199999/1999995001-2000000000", GpidRangeGenerator.generateRangeWithAutoCounter(2000000000L));
        assertEquals("200000/2000000001-2000005000", GpidRangeGenerator.generateRangeWithAutoCounter(2000000001L));

    }

    @Test
    void test_sequenceUpToTwoBillion() {

        long start = 1_000_000_001L;
        long end = 2_000_000_000L;

        for (long lower = start; lower <= end; lower += 5000L) {
            int i = (int) ((lower - 1_000_000_001L) / 5000);
            String expected = String.format("%d/%d-%d", i, lower, lower + 4999L);

            assertEquals(expected, GpidRangeGenerator.generateRangeWithAutoCounter(lower), "Error lower=" + lower);
            assertEquals(expected, GpidRangeGenerator.generateRangeWithAutoCounter(lower + 4999L), "Error upper=" + (lower + 4999L));
        }
    }

    @Test
    void test_tenRandomSamples() {

        // Fixed seed for reproducible "random" values
        Random rnd = new Random(123456);
        long start = 1_000_000_001L;
        long end = 2_000_000_000L;
        int range = (int) (end - start + 1);

        for (int j = 0; j < 10; j++) {
            long id = start + rnd.nextInt(range);
            String result = GpidRangeGenerator.generateRangeWithAutoCounter(id);

            // Parse result "[i]/lower-upper"
            String[] parts = result.split("/|-");
            int i = Integer.parseInt(parts[0]);
            long lower = Long.parseLong(parts[1]);
            long upper = Long.parseLong(parts[2]);

            // Calculate expected values
            long expectedLower = ((id - 1) / 5000) * 5000 + 1;
            long expectedUpper = expectedLower + 4999;
            int expectedI = (int) ((expectedLower - 1_000_000_001L) / 5000);
            String expected = String.format("%d/%d-%d", expectedI, expectedLower, expectedUpper);

            assertEquals(expected, result, "Errors in random id=" + id);
            assertTrue(id >= lower && id <= upper, "The ID is not within the generated area: id=" + id + ", range=" + result);
        }
    }

}
