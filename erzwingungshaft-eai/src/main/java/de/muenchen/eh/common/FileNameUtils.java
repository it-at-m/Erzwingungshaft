package de.muenchen.eh.common;

import de.muenchen.eh.log.DocumentType;
import de.muenchen.eh.log.StatusProcessingType;
import de.muenchen.xjustiz.xoev.codelisten.XoevCodeGDSDokumentklasse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtils {

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

    private static final Pattern EH_PDF_PATTERN = Pattern.compile(".*(?i)eh.*\\.pdf$");

    public static boolean isEHFile(String fileName) {
        Matcher matcher = EH_PDF_PATTERN.matcher(fileName);
        return matcher.matches();
    }

    private static final Pattern URB_PDF_PATTERN = Pattern.compile(".*(?i)urb.*\\.pdf$");

    public static boolean isURBFile(String fileName) {
        Matcher matcher = URB_PDF_PATTERN.matcher(fileName);
        return matcher.matches();
    }

    private static final Pattern URK_PDF_PATTERN = Pattern.compile(".*(?i)urk.*\\.pdf$");

    public static boolean isURKFile(String fileName) {
        Matcher matcher = URK_PDF_PATTERN.matcher(fileName);
        return matcher.matches();
    }

    private static final Pattern VW_PDF_PATTERN = Pattern.compile(".*(?i)vw.*\\.pdf$");

    public static boolean isVWFile(String fileName) {
        Matcher matcher = VW_PDF_PATTERN.matcher(fileName);
        return matcher.matches();
    }

    public static DocumentType getDocumentType(String fileName) {

        if (isEHFile(fileName))
            return DocumentType.ANTRAG;
        if (isURBFile(fileName))
            return DocumentType.BESCHEID;
        if (isURKFile(fileName))
            return DocumentType.KOSTEN;
        if (isVWFile(fileName))
            return DocumentType.VERWERFUNG;

        throw new IllegalArgumentException("File not found: " + fileName);
    }

    public static StatusProcessingType getProcessingType(String fileName) {

        if (isEHFile(fileName))
            return StatusProcessingType.IMPORT_ANTRAG_IMPORT_DB;
        if (isURBFile(fileName))
            return StatusProcessingType.IMPORT_BESCHEID_IMPORT_DB;
        if (isURKFile(fileName))
            return StatusProcessingType.IMPORT_KOSTEN_IMPORT_DB;
        if (isVWFile(fileName))
            return StatusProcessingType.IMPORT_VERWERFUNG_IMPORT_DB;

        throw new IllegalArgumentException("File not found: " + fileName);
    }

    public static XoevCodeGDSDokumentklasse getGdsDokumentenklasse(DocumentType type) {

        switch (type) {
            case ANTRAG:
                return XoevCodeGDSDokumentklasse.ANTRAG;
            case BESCHEID:
                return XoevCodeGDSDokumentklasse.BESCHEID;
//            case KOSTEN: return XoevCodeGDSDokumentklasse.KOSTEN;
//            case VERWERFUNG: return XoevCodeGDSDokumentklasse.VERWERFUNG;
            default:
                throw new IllegalArgumentException("DocumentType not found: " + type);
        }

    }

}
