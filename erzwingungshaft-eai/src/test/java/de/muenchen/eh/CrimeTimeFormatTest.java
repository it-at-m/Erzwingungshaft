package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.muenchen.eh.claim.content.ContentContainerFactoryHelper;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class CrimeTimeFormatTest {

    @Test
    void test_formatHour() {

        assertNull(ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat(null, null, null, Locale.GERMANY));

        assertEquals("14", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("14", null, "", Locale.GERMANY));
        assertEquals("14", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("14", "  ", "", Locale.GERMANY));
        assertEquals("14", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("14", "  ", "01", Locale.GERMANY));
        assertEquals("04", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("04", null, null, Locale.GERMANY));
        assertEquals("04", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("4", null, null, Locale.GERMANY));
        assertEquals("20", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("20", "  ", "", Locale.GERMANY));

        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("25", "0", "30", Locale.GERMANY));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("14", null, null, Locale.US));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("-1", null, null, Locale.US));
    }

    @Test
    void test_formatHourMinute() {
        assertEquals("01:01", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("1", "01", "", Locale.GERMANY));
        assertEquals("12:01", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("12", "1", null, Locale.GERMANY));
        assertEquals("14:14", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("14", "14  ", "  ", Locale.GERMANY));

        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("07", "60", "30", Locale.GERMANY));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("15", "00", null, Locale.US));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("8", "-1", null, Locale.US));
    }

    @Test
    void test_formatHourMinuteSecond() {
        assertEquals("12:13:14", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("12", "13", "14", Locale.GERMANY));
        assertEquals("20:20:20", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("20", " 20  ", "20  ", Locale.GERMANY));
        assertEquals("10:30:59", ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("10", "30", "59", Locale.US));

        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("20", "30", "60", Locale.GERMANY));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("16", "60", "30", Locale.GERMANY));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("15", "15", "15", Locale.US));
        assertThrows(IllegalArgumentException.class, () -> ContentContainerFactoryHelper.xJustizTypeGDSZeitangabeFormat("11", "15", "-15", Locale.US));
    }

}
