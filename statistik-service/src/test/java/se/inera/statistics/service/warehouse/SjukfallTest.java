package se.inera.statistics.service.warehouse;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SjukfallTest {

    @Test
    public void testConstructorNewSjukfall() throws Exception {
        //When
        Sjukfall result = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

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
        assertArrayEquals(new Object[]{1}, Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        }).toArray());

        assertEquals("01", result.getLanskod());
        assertEquals(false, result.isExtended());
    }

    @Test
    public void testConstructorExtendSjukfall() throws Exception {
        //Given
        Sjukfall sjukfall = new Sjukfall(new Fact(1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,new int[]{1},1));

        //When
        Sjukfall result = new Sjukfall(sjukfall, new Fact(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, new int[]{2}, 2));

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
        assertEquals(Kon.byNumberRepresentation(2), result.getKon());
        final List<Integer> lakare = Lists.transform(new ArrayList<>(result.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        });
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(1));
        assertTrue(lakare.contains(2));
        assertEquals("02", result.getLanskod());
        assertEquals(true, result.isExtended());
    }

}
