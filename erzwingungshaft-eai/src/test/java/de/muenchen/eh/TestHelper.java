package de.muenchen.eh;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

public class TestHelper {

    protected boolean testXmlCompare(String expectedXml, String actualXml) {

        Diff diff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                .build();

        if (diff.hasDifferences()) {
            diff.getDifferences().forEach(difference -> System.out.println(difference.toString()));
        }
        return diff.hasDifferences();
    }

}
