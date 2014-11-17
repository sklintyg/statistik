package se.inera.statistics.service.warehouse;

import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;

import static org.junit.Assert.*;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        Sjukfall result = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1));

        //Then
        assertEquals(1, result.getAlder());
        assertEquals(1, result.getDiagnosavsnitt());
        assertEquals(1, result.getDiagnoskapitel());
        assertEquals(1, result.getDiagnoskategori());
        assertEquals(1, result.getEnd());
        assertEquals(1, result.getIntygCount());
        assertEquals(1, result.getRealDays());
        assertEquals(1, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.Male, result.getKon());
        assertArrayEquals(new Object[]{1}, result.getLakare().toArray());
        assertEquals("01", result.getLanskod());
        assertEquals(false, result.isExtended());
    }

    @Test
    public void testConstructorExtendSjukfall() throws Exception {
        //Given
        Sjukfall sjukfall = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1));

        //When
        Sjukfall result = new Sjukfall(sjukfall, new Fact(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2));

        //Then
        assertEquals(2, result.getAlder());
        assertEquals(2, result.getDiagnosavsnitt());
        assertEquals(2, result.getDiagnoskapitel());
        assertEquals(2, result.getDiagnoskategori());
        assertEquals(3, result.getEnd());
        assertEquals(2, result.getIntygCount());
        assertEquals(3, result.getRealDays());
        assertEquals(2, result.getSjukskrivningsgrad());
        assertEquals(1, result.getStart());
        assertEquals(Kon.Male, result.getKon());
        assertArrayEquals(new Object[]{1,2}, result.getLakare().toArray());
        assertEquals("02", result.getLanskod());
        assertEquals(true, result.isExtended());
    }

}
