package de.muenchen.eh;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import de.muenchen.eh.domain.identifier.IdentifierCreator;
import de.muenchen.eh.infrastructure.db.entity.EfileIdentifier;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class IdentifierMessageTest {

    @Test
    void test_createIdentifier() {

        assertEquals("Identifier creation failed", "600002-SKA9512.13-2-0001", IdentifierCreator
                .createIdentifier(Optional.of("600002"), Optional.of("SKA"), Optional.of("Bußgeldverfahren (9512.13-2-0001)"), new EfileIdentifier()).get());
        assertEquals("Identifier creation failed", "1000332600-SKA9512.66-10-0001", IdentifierCreator
                .createIdentifier(Optional.of("1000332600"), Optional.of("SKA"), Optional.of("Bußgeldverfahren (9512.66-10-0001)"), new EfileIdentifier())
                .get());

    }

    @Test
    void test_createIdentifierMessage() {

        EfileIdentifier messageComment = new EfileIdentifier();
        assertTrue("Identifier message creation failed", IdentifierCreator
                .createIdentifier(Optional.empty(), Optional.of("SKA"), Optional.of("Bußgeldverfahren (9512.13-2-0001)"), messageComment).isEmpty());
        assertEquals("Identifier message creation failed", "Kassenzeichen is null.", messageComment.getMessage());

        messageComment = new EfileIdentifier();
        assertTrue("Identifier creation failed", IdentifierCreator
                .createIdentifier(Optional.of("1000332600"), Optional.empty(), Optional.of("Bußgeldverfahren (9512.66-10-0001)"), messageComment).isEmpty());
        assertEquals("Identifier message creation failed", "Check efile.case-file.basenr", messageComment.getMessage());

        messageComment = new EfileIdentifier();
        assertTrue("Identifier creation failed",
                IdentifierCreator.createIdentifier(Optional.of("1000332600"), Optional.of("SKA"), Optional.of("Bußgeldverfahren "), messageComment).isEmpty());
        assertEquals("Identifier message creation failed", "Efile fine content extraction failed.", messageComment.getMessage());

        messageComment = new EfileIdentifier();
        assertTrue("Identifier creation failed",
                IdentifierCreator.createIdentifier(Optional.of("1000332600"), Optional.of("SKA"), Optional.empty(), messageComment).isEmpty());
        assertEquals("Identifier message creation failed", "Efile fine objectname is null.", messageComment.getMessage());

    }

}
