package de.muenchen.eh;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Log4j2
public class ProcessXmlDocumentCompare {

    private static List<String> excludeElements = List.of(
    		
        "tns:aktenzeichen.absender",
        "tns:erstellungszeitpunkt",
        "tns:eigeneNachrichtenID",
        "tns:aktenzeichen.freitext",
        "tns:id",
        "tns:anzeigename"
    );

    public static String process(String xml) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true); 
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new org.xml.sax.InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();

  
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(new NamespaceContextMap("tns", "http://www.xjustiz.de"));

     
        for (String element : excludeElements) {
            NodeList nodeList = (NodeList) xpath.evaluate("//" + element, doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }
        }
       
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        log.debug(writer.toString());
        
        return writer.toString();
    }

  
    private static class NamespaceContextMap implements javax.xml.namespace.NamespaceContext {
        private final String prefix;
        private final String namespaceURI;

        public NamespaceContextMap(String prefix, String namespaceURI) {
            this.prefix = prefix;
            this.namespaceURI = namespaceURI;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix.equals(this.prefix)) {
                return namespaceURI;
            }
            return null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return null;
        }
    }
}
