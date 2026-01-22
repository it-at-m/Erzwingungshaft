package de.muenchen.eh.common;


import de.muenchen.xjustiz.generated.xjustiz0500straf35.NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XmlUnmarshaller {

    private static final JAXBContext CONTEXT;
    static {
        try {
            CONTEXT = JAXBContext.newInstance(NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010.class);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 unmarshalNachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010(String xml)
            throws JAXBException {
        Objects.requireNonNull(xml, "xml");
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            XMLReader xr = spf.newSAXParser().getXMLReader();
            SAXSource source = new SAXSource(xr, new InputSource(new StringReader(xml)));
            return (NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010) CONTEXT.createUnmarshaller().unmarshal(source);
        } catch (JAXBException e) {
            throw e;
        } catch (Exception e) { // parser config
            throw new JAXBException("Failed to securely unmarshal XML", e);
        }
    }
}
