package de.muenchen.eh.common;

public class ExtractEhIdentifier {

    /**
     *
     * @param input Expected Format identifier_suffix
     * @return identifier
     */
    public static String getIdentifier(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        int lastUnderscoreIndex = input.lastIndexOf('_');
        if (lastUnderscoreIndex == -1) {
            throw new IllegalArgumentException("Input must contain an underscore: " + input);
        }
        return input.substring(0, lastUnderscoreIndex);
    }

    /**
     *
     * @param input Expected Format prefix/identifier_suffix
     * @return identifier
     */
    public static String getFileName(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        int slashIndex = input.indexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException("Input must contain a slash: " + input);
        }
        return input.substring(slashIndex + 1);
    }

}
