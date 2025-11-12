package de.muenchen.eh.kvue.file;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {

    /**
     *
     * @param input String containing a date in the format yyyyMMdd .
     * @return Found date string or NULL.
     */
    public static String extractDate(String input) {
        // Regular expression for the date in the format yyyyMMdd
        Pattern pattern = Pattern.compile("\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

}
