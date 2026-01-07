package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum StatusProcessingType {

    IMPORT_DATA_FILE_CREATED("Raw data line read from original multi-line import file. Claim file (*.fix) generated in newly created 'claim-directory'."),
    IMPORT_DATA_FILE_IMPORT_FINISHED("Generate claim import files finished. Start import-pdfs process."),
    IMPORT_ANTRAG_IMPORT_DIRECTORY("Antrag PDF is imported and assigned to directory."),
    IMPORT_BESCHEID_IMPORT_DIRECTORY("Bescheid PDF is imported and assigned to directory."),
    IMPORT_VERWERFUNG_BESCHEID_IMPORT_DIRECTORY("Verwerfungbescheid PDF is imported and assigned to directory."),
    IMPORT_KOSTEN_BESCHEID_IMPORT_DIRECTORY("Kostenbescheid PDF is imported and assigned to directory."),
    IMPORT_ANTRAG_IMPORT_DB("Antrag PDF is imported and assigned to database."),
    IMPORT_BESCHEID_IMPORT_DB("Bescheid PDF is imported and assigned to database."),
    IMPORT_KOSTEN_IMPORT_DB("Kostenbescheid PDF is imported and assigned to database."),
    IMPORT_VERWERFUNG_IMPORT_DB("Verwerfungsbescheid PDF is imported and assigned to database."),
    CLAIM_RAW_DATA_READ("Raw data line read from generated claim file."),
    CLAIM_RAW_DATA_UNMARSHALLED("Data unmarshalled from claim file line."),
    CLAIM_CONTENT_DATA_CREATED("Content created for xJustiz message generation."),
    CLAIM_XJUSTIZ_MESSAGE_CREATED("xJustiz xml message created."),
    CLAIM_EH_UUID_UPDATED("xJustiz message uuid updated in database."),
    CLAIM_EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED("'Kassenzeichen' and 'GeschaeftsparterId' updated in database."),
    EFILE_GPID_COLLECTION_READ_FROM_DB("'GeschaeftspartnerId' known in efile."),
    EFILE_GESCHAEFTSPARTNERID_COLLECTION_NOT_FOUND("Collection file for 'GeschaeftspartnerId' not found in efile."),
    EFILE_GESCHAEFTSPARTNERID_COLLECTION_FOUND("Collection file for 'GeschaeftspartnerId' found in efile."),
    EFILE_GESCHAEFTSPARTNERID_COLLECTION_AMBIGUOUS("More than one collection file for 'GeschaeftspartnerId' found in efile."),
    EFILE_FILE_ADDED_TO_COLLECTION("Case file added to efile collection."),
    EFILE_FILE_ALREADY_EXISTS_IN_COLLECTION("Case file already exists in efile collection."),
    EFILE_FINE_ADDED_TO_CASE_FILE("Fine file added to efile case file."),
    EFILE_OUTGOING_ADDED_TO_FINE("Outgoing file added to efile fine file."),
    EFILE_CONTENT_OBJECT_ANTRAG_ADDED_TO_OUTGOING("Content object 'Antrag' added to efile outgoing."),
    EFILE_CONTENT_OBJECT_URBESCHEID_ADDED_TO_OUTGOING("Content object 'URBESCHEID' added to efile outgoing."),
    EFILE_CONTENT_OBJECT_XML_ADDED_TO_OUTGOING("Content object 'Verfahrensmitteilung.xml' added to efile outgoing."),
    EFILE_OBJECTADDRESSES_SAVED("Collection, case file, fine and documents efile objectaddresses saved in database."),
    EFILE_SUBJECT_FILE_DATA_SAVED("GP-Name, GP-Firstname, GP-Birthdate updated in efile file."),
    EFILE_SUBJECT_OWI_DATA_SAVED("'Ordnungswidrigkeitnummer (OWI)' updated in efile fine."),
    EFILE_SUBJECT_DATA_SKIPPED("Subject data attributes (efile.case-file, efile.fine) not defined in properties."),
    XTA_MESSAGE_ID("XTA message id received.");

    private final String descriptor;

    StatusProcessingType(String descriptor) {
        this.descriptor = descriptor;
    }
}
