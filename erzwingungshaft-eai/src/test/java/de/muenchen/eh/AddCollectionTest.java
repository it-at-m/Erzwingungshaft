package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eh.infrastructure.integration.efile.operation.eapl.GeschaeftspartnerIdFilter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class AddCollectionTest {

    @Test
    void test_gpIdFilter_matchesBothFormats() throws Exception {

        Objektreferenz o1 = new Objektreferenz();
        o1.setObjname("9512.3/SKA-3-2/1000015001-1000020000");
        Objektreferenz o2 = new Objektreferenz();
        o2.setObjname("9512.3/1000015001-1000020000");
        Objektreferenz o3 = new Objektreferenz();
        o3.setObjname("1000015001-1000020000");
        Objektreferenz o4 = new Objektreferenz();
        o4.setObjname("  ");
        Objektreferenz o5 = new Objektreferenz();
        Objektreferenz o6 = new Objektreferenz();
        o6.setObjname("9512.3 / 1000015001 - 1000020000 ");

        List<Objektreferenz> objektList = Arrays.asList(o1, o2, o3, o4, o5, o6);

        List<Objektreferenz> result = (List<Objektreferenz>) GeschaeftspartnerIdFilter.gpIdFilter(objektList, 1000016000L);
        assertEquals(4, result.size());
    }
}
