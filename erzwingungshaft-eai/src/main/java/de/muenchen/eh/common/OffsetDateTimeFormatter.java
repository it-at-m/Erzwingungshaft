package de.muenchen.eh.common;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OffsetDateTimeFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static String formatNow() {

        OffsetDateTime currentTime = OffsetDateTime.now();
        return currentTime.format(DATE_TIME_FORMATTER);
    }
}
