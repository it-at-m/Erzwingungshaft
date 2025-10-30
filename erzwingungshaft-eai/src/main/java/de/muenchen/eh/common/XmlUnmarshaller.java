package de.muenchen.eh.common;

import de.muenchen.xjustiz.generated.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import jakarta.xml.bind.JAXBContext;
import java.io.StringReader;

public class XmlUnmarshaller {

    public static NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(String xml)
            throws Exception {
        JAXBContext context = JAXBContext.newInstance(NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010.class);
        return (NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010) context.createUnmarshaller().unmarshal(new StringReader(xml));
    }

}
