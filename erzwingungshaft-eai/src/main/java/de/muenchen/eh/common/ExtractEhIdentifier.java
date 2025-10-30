package de.muenchen.eh.common;

public class ExtractEhIdentifier {

    /**
     *
     * @param input Expected Format identifier_suffix
     * @return identifier
     */
    public static String getIdentifier(String input) {
        int lastUnderscoreIndex = input.lastIndexOf('_');
        return input.substring(0, lastUnderscoreIndex);
    }

    /**
     *
     * @param input Expected Format prefix/identifier_suffix
     * @return identifier
     */
    public static String getFileName(String input) {
        return input.substring(input.indexOf('/') + 1);
    }

}
