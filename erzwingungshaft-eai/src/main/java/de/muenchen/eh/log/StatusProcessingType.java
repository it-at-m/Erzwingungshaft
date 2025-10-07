package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum StatusProcessingType {

    DATA_FILE_CREATED("Raw data line read from original multi-line import file. Claim file (*.fix) generated in newly created 'claim-directory'."),
    DATA_FILE_IMPORT_FINISHED("Generate claim import files finished. Start import-pdfs process."),
    ANTRAG_IMPORT_DIRECTORY("Antrag PDF is imported and assigned to directory."),
    BESCHEID_IMPORT_DIRECTORY("Bescheid PDF is imported and assigned to directory."),
    ANTRAG_IMPORT_DB("Antrag PDF is imported and assigned to database."),
    BESCHEID_IMPORT_DB("Bescheid PDF is imported and assigned to database."),
    DATA_READ("Raw data line read from generated claim file."),
    DATA_UNMARSHALLED("Data unmarshalled from claim file line."),
    CONTENT_CREATED("Content created for xJustiz message generation."),
    XJUSTIZ_MESSAGE_CREATED("xJustiz xml message created."),
    EH_UUID_UPDATED("xJustiz message uuid updated in database."),
    EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED("'Kassenzeichen' and 'GeschaeftsparterId' updated in database."),
    GESCHAEFTSPARTNERID_EINZELKAKTE_NOT_FOUND("'Einzelakte' for 'GeschaeftspartnerId' in eAKte not found."),
    GESCHAEFTSPARTNERID_EINZELKAKTE_FOUND("'Einzelakte' for 'GeschaeftspartnerId' in eAKte found.");

    private final String descriptor;

    StatusProcessingType(String descriptor) {
        this.descriptor = descriptor;
    }
}
