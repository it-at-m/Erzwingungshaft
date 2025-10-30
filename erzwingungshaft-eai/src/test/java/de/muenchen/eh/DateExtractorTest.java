package de.muenchen.eh;

import de.muenchen.eh.kvue.file.DateExtractor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DateExtractorTest {

    // @Test
    public void test_extractDates() {

        assertEquals("20240807", DateExtractor.extractDate("D.KVU.EUDG0P0.20240807.EZH"));
        assertEquals("20240807", DateExtractor.extractDate("DKVUEUDG0P020240807EZH"));
        assertNull(DateExtractor.extractDate("D.KVU.EUDG0P0.2024087.EZH"), "Wrong format yyyyMMd");
        assertNull(DateExtractor.extractDate("D.KVU.EUDG0P0.07082024.EZH"), "Wrong format ddMMyyyy");
    }
}
