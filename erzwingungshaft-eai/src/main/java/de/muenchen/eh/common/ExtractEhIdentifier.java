package de.muenchen.eh.common;

public class ExtractEhIdentifier {

        public static String getIdentifier(String input) {
            int lastUnderscoreIndex = input.lastIndexOf('_');
            return input.substring(0, lastUnderscoreIndex);
        }

}
