package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.eakte.api.rest.model.Objektreferenz;
import de.muenchen.eakte.api.rest.model.ReadApentryAntwortDTO;
import de.muenchen.eh.claim.efile.operation.FindCollection;
import de.muenchen.eh.claim.efile.operation.OperationIdFactory;
import de.muenchen.eh.db.repository.ClaimEfileRepository;
import de.muenchen.eh.db.service.ClaimService;
import de.muenchen.eh.log.LogServiceClaim;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FindCollectionTest {

    @Test
    void test_gpIdFilter_matchesBothFormats() throws Exception {

        OperationIdFactory opIdFactory = Mockito.mock(OperationIdFactory.class);
        LogServiceClaim logServiceClaim = Mockito.mock(LogServiceClaim.class);
        ClaimEfileRepository claimEfileRepository = Mockito.mock(ClaimEfileRepository.class);
        ClaimService claimService = Mockito.mock(ClaimService.class);

        FindCollection findCollection = new FindCollection(opIdFactory, logServiceClaim, claimEfileRepository, claimService);

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

        ReadApentryAntwortDTO dto = new ReadApentryAntwortDTO();
        dto.setGiobjecttype(objektList);
        findCollection.setCollectionCache(Optional.of(dto));

        // private gpIdFilter call via reflection
        Method gpFilter = FindCollection.class.getDeclaredMethod("gpIdFilter", List.class, long.class);
        gpFilter.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Objektreferenz> result = (List<Objektreferenz>) gpFilter.invoke(findCollection, objektList, 1000016000L);
        assertEquals(4, result.size());
    }
}
