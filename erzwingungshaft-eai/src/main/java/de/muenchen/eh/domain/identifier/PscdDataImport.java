package de.muenchen.eh.domain.identifier;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@Data
@FixedLengthRecord(length = 444, paddingChar = ' ', ignoreTrailingChars = true)
public class PscdDataImport {

    @DataField(pos = 1, length = 30, trim = true, align = "L")
    private String content_holder_1;

    @DataField(pos = 31, length = 12, trim = true, align = "L")
    private String kassenzeichen;

    @DataField(pos = 43, length = 2, trim = true, align = "L")
    private String content_holder_2;

    @DataField(pos = 45, length = 10, trim = true, align = "L")
    private String geschaeftspartnerid;

    @DataField(pos = 55, length = 390, trim = true, align = "L")
    private String content_holder_3;

}
