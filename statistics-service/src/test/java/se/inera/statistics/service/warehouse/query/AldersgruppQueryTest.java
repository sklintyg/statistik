package se.inera.statistics.service.warehouse.query;

import org.junit.Test;
import se.inera.statistics.service.report.util.AldersgroupUtil;
import se.inera.statistics.service.report.util.Ranges;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AldersgruppQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 10, 45);
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<Ranges.Range,Counter<Ranges.Range>> count = AldersgruppQuery.count(sjukfall);
        assertEquals(1, count.get(AldersgroupUtil.RANGES.rangeFor("41-45")).getCount());
    }

    @Test
    public void useMax() {
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 1);
        fact(4010, 10, 30);
        fact(4010, 10, 30);
        fact(4010, 10, 45);
        fact(4010, 10, 45);
        fact(4010, 10, 50);
        fact(4010, 10, 50);
        fact(4010, 10, 50);
        fact(4010, 10, 100);
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Ranges.Range>> count = AldersgruppQuery.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(3, count.get(3).getCount());
    }

    private void fact(int startday, int length, int alder) {
        Fact fact = new Fact(3, 380, 38002, 1, intyg++, patient++, startday, 0, alder, 0, 14, 16, 100, length, 0, 32, 201010);
        warehouse.accept(fact, VARDGIVARE);
    }
}
