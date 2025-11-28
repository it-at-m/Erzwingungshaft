package de.muenchen.eh;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.eh.common.FileNameUtils;
import org.junit.jupiter.api.Test;

public class FileNameUtilsTest {

    @Test
    public void test_EHFileNames() {

        assertTrue(FileNameUtils.isEHFile("GPID_kassenzeichen_jjjjmmtt_EH-Antrag.pdf"));
        assertTrue(FileNameUtils.isEHFile("GPID_kassenzeichen_jjjjmmtt_eH-antrag.pDf"));
        assertTrue(FileNameUtils.isEHFile("GPID_kassenzeichen_jjjjmmtt_Eh.pdf"));

        assertFalse(FileNameUtils.isEHFile("GPID_kassenzeichen_jjjjmmtt_XY.pdf"));
        assertFalse(FileNameUtils.isEHFile("GPID_kassenzeichen_jjjjmmtt_EH.txt"));

    }

    @Test
    public void test_URBFileNames() {

        assertTrue(FileNameUtils.isURBFile("GPID_kassenzeichen_jjjjmmtt_URB.pdf"));
        assertTrue(FileNameUtils.isURBFile("GPID_kassenzeichen_jjjjmmtt_urboW.pDf"));

        assertFalse(FileNameUtils.isURBFile("GPID_kassenzeichen_jjjjmmtt_XY.pdf"));
        assertFalse(FileNameUtils.isURBFile("GPID_kassenzeichen_jjjjmmtt_urb.txt"));

    }

    @Test
    public void test_URKFileNames() {

        assertTrue(FileNameUtils.isURKFile("GPID_kassenzeichen_jjjjmmtt_URK.pdf"));
        assertTrue(FileNameUtils.isURKFile("GPID_kassenzeichen_jjjjmmtt_urkoW.pDf"));

        assertFalse(FileNameUtils.isURKFile("GPID_kassenzeichen_jjjjmmtt_XY.pdf"));
        assertFalse(FileNameUtils.isURKFile("GPID_kassenzeichen_jjjjmmtt_urk.txt"));

    }

    @Test
    public void test_VWFileNames() {

        assertTrue(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_VW.pdf"));
        assertTrue(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_VW1.pdf"));
        assertTrue(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_vw2.pDf"));
        assertTrue(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_VW3.PDF"));

        assertFalse(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_XY.pdf"));
        assertFalse(FileNameUtils.isVWFile("GPID_kassenzeichen_jjjjmmtt_vw.txt"));

    }

}
