package de.muenchen.eh.kvue.file;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@Getter
@Setter
@FixedLengthRecord(length = 3542, paddingChar = ' ')
public class ImportClaimIdentifierData {

    @DataField(pos = 1169, length = 20, trim = true, align = "B")
    private String ehkassz;

    @DataField(pos = 3533, length = 10, trim = true, align = "B")
    private String ehgpid;

    private String printDate;

    private String identifier = null;

    private String getIdentifier() {
        if (identifier == null) {
            if (ehkassz != null && ! ehkassz.isBlank() && ehgpid != null && ! ehgpid.isBlank() && printDate != null && ! printDate.isBlank())
                identifier = getEhgpid().concat("_").concat(getEhkassz()).concat("_").concat(getPrintDate());
            else
                throw new IllegalArgumentException("Identifier could not be generated. An attribute that makes up the Identifier is invalid.");
        }
        return identifier;
    }

    public String getPathName() {
        return getIdentifier();
    }

    public String getFileName() {
        return getIdentifier().concat(".fix");
    }
}
