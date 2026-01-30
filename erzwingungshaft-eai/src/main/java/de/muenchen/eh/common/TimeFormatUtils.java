package de.muenchen.eh.common;

import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.TimeValidator;

@Log4j2
public class TimeFormatUtils {

    public static String formatTime(String hour, String minute, Locale locale) {

        log.debug("TimeFormatUtils.formatTime : hour '{}' minute '{}'", hour, minute);
        if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
            return null;
        }

        TimeValidator timeValidator = TimeValidator.getInstance();
        String timeString = hour + ":" + minute;

        if (!timeValidator.isValid(timeString, locale)) {
            return null;
        }
        String formatted = String.format("%02d:%02d", Integer.parseInt(hour), Integer.parseInt(minute));
        log.debug("TimeFormatUtils.formatTime : formatted '{}'", formatted);

        return formatted;
    }

}
