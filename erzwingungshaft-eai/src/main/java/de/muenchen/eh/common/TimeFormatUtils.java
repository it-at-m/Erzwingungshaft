package de.muenchen.eh.common;

import org.apache.commons.validator.routines.TimeValidator;

public class TimeFormatUtils {

    public static String formatTime(String hour, String minute) {
        if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
            return null;
        }

        TimeValidator timeValidator = TimeValidator.getInstance();
        String timeString = hour + ":" + minute;

        if (!timeValidator.isValid(timeString)) {
            return null;
        }

        return String.format("%02d:%02d", Integer.parseInt(hour), Integer.parseInt(minute));
    }

}
