package de.muenchen.eh;

import de.muenchen.eh.claim.efile.IdRangeGenerator;
import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdRangeGeneratorTest {

    @Test
    void test_explicitIntervals() {

        // Lower edge
        assertEquals("999995001-1000000000", IdRangeGenerator.generateRange(1000000000L));

        // Lower limit
        assertEquals("1000000001-1000005000", IdRangeGenerator.generateRange(1000000001L));
        assertEquals("1000005001-1000010000", IdRangeGenerator.generateRange(1000005001L));
        assertEquals("1000010001-1000015000", IdRangeGenerator.generateRange(1000010001L));
        assertEquals("1000015001-1000020000", IdRangeGenerator.generateRange(1000015001L));

        // Upper limit
        assertEquals("1000000001-1000005000", IdRangeGenerator.generateRange(1000005000L));
        assertEquals("1000005001-1000010000", IdRangeGenerator.generateRange(1000010000L));
        assertEquals("1000010001-1000015000", IdRangeGenerator.generateRange(1000015000L));
        assertEquals("1000015001-1000020000", IdRangeGenerator.generateRange(1000020000L));

        // upper edge
        assertEquals("1999995001-2000000000", IdRangeGenerator.generateRange(2000000000L));
        assertEquals("2000000001-2000005000", IdRangeGenerator.generateRange(2000000001L));

    }

    @Test
    void test_sequenceUpToTwoBillion() {

        long start = 1_000_000_001L;
        long end = 2_000_000_000L;

        for (long lower = start; lower <= end; lower += 5000L) {
            String expected = String.format("%d-%d", lower, lower + 4999L);

            assertEquals(expected, IdRangeGenerator.generateRange(lower), "Error lower=" + lower);
            assertEquals(expected, IdRangeGenerator.generateRange(lower + 4999L), "Error upper=" + (lower + 4999L));
        }
    }

    @Test
    void test_tenRandomSamples() {

        // Fixed seed for reproducible "random" values
        Random rnd = new Random(123456);
        long start = 1_000_000_001L;
        long end = 2_000_000_000L;
        int range = (int)(end - start + 1);

        for (int i = 0; i < 10; i++) {
            long id = start + rnd.nextInt(range);

            String result = IdRangeGenerator.generateRange(id);
            // Parse result "lower-upper"
            String[] parts = result.split("-");
            long lower = Long.parseLong(parts[0]);
            long upper = Long.parseLong(parts[1]);

            long expectedLower = ((id - 1) / 5000) * 5000 + 1;
            long expectedUpper = expectedLower + 4999;
            String expected = String.format("%d-%d", expectedLower, expectedUpper);

            assertEquals(expected, result,
                    "Errors in random id=" + id);

            assertTrue(id >= lower && id <= upper,
                    "The ID is not within the generated area: id=" + id + ", range=" + result);

        }
    }

}
