package de.muenchen.eh.log;

import lombok.Getter;

@Getter
public enum StatusProcessingType {

    DATA_READ("Data line read from file."),
    DATA_UNMARSHALLED("Data unmarshalled from file line."),
    CONTENT_CREATED("Content created for xJustiz message generation."),
    XJUSTIZ_MESSAGE_CREATED("xJustiz message created."),
    EH_UUID_UPDATED("xJustiz message uuid updated in database."),
    EH_KASSENZEICHEN_GESCHAEFTSPARTNERID_UPDATED("'Kassenzeichen' and 'GeschaeftsparterId' updated in database.");

    private final String descriptor;

    StatusProcessingType(String descriptor) {
        this.descriptor = descriptor;
    }
}
