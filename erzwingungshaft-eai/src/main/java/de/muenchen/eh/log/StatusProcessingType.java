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
    GPID_COLLECTION_READ_FROM_DB("'GeschaeftspartnerId' known in efile."),
    GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND("Collection file for 'GeschaeftspartnerId' not found in efile."),
    GESCHAEFTSPARTNERID_COLLECTION_FOUND("Collection file for 'GeschaeftspartnerId' found in efile."),
    GESCHAEFTSPARTNERID_COLLECTION_AMBIGUOUS("More than one collection file for 'GeschaeftspartnerId' found in efile."),
    FILE_ADDED_TO_COLLECTION("Case file added to efile collection."),
    FILE_ALREADY_EXISTS_IN_COLLECTION("Case file already exists in efile collection."),
    FINE_ADDED_TO_CASE_FILE("Fine file added to efile case file."),
    OUTGOING_ADDED_TO_FINE("Outgoing file added to efile fine file."),
    CONTENT_OBJECT_ANTRAG_ADDED_TO_OUTGOING("Content object 'Antrag' added to efile outgoing."),
    CONTENT_OBJECT_URBESCHEID_ADDED_TO_OUTGOING("Content object 'URBESCHEID' added to efile outgoing."),
    EFILE_OBJECTADDRESSES_SAVED("Collection, case file, fine and documents efile objectaddresses saved in database.");

    private final String descriptor;

    StatusProcessingType(String descriptor) {
        this.descriptor = descriptor;
    }
}
