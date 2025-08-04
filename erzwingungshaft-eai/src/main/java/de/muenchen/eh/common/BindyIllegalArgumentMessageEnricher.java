package de.muenchen.eh.common;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BindyIllegalArgumentMessageEnricher {

    /**
     * Searches for the first integer in the message and inserts the bracketed field name directly after it:
     *
     * For example : The mandatory field defined at the position 31 is empty for the line: 1
     *        -->    The mandatory field defined at the position 31 (de.muenchen.eh.kvue.EhCase.ehtatstdb) is empty for the line: 1
     *
     * Attention :
     *    Camel Bindy index base is 1 (not 0).
     *    In Java, the order is not guaranteed to be stable across compiler/VM versions, but usually corresponds to the declaration order in the code.
     *
     * @param message The original message
     * @param clazz Class with the searched field name
     * @return The modified message with the inserted field name in brackets
     */
    public static String enrich(String message, Class clazz) {

        StringBuilder sb = new StringBuilder();
        Optional<String> fieldName = getFieldNameAtIndex(clazz, extractFirstNumber(message) -1); // Camel Bindy index base is 1 (not 0)
        fieldName.ifPresentOrElse(value -> {sb.append(insertAfterFirstNumber(message, clazz.getName().concat( ".").concat(value)));}, () ->{ sb.append(message);});
        return sb.toString();
    }

    /**
     * Searches for the first integer in the text and inserts the bracketed text directly after it.
     *
     * @param inputText The original text
     * @param textToInsert The text to be inserted in brackets (passed without brackets!)
     * @return The modified text with the inserted text in brackets
     */
    private static String insertAfterFirstNumber(String inputText, String textToInsert) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(inputText);

        if (matcher.find()) {
            int end = matcher.end();
            return inputText.substring(0, end) + " (" + textToInsert + ")" + inputText.substring(end);
        }
     return inputText;
    }

    /**
     * Returns the name as Optional<String> of the attribute (field name) at the specified position.
     *
     * @param index Position (starts with 0).
     * @return The name of as Optional<String> the field or Optional.empty() if the index is invalid.
     */
    private static Optional<String> getFieldNameAtIndex(Class clazz, int index) {

        Field[] fields = clazz.getDeclaredFields();

        if (index < 0 || index >= fields.length) {
            return Optional.empty();
        }
        return Optional.of(fields[index ].getName());
    }

    /**
     * Returns the first number found in the text.
     *
     * @param text The input text.
     * @return The first number as an Optional<Integer></Integer>, or Optional.empty() if no number is found.
     */
    private static Integer extractFirstNumber(String text) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

}
