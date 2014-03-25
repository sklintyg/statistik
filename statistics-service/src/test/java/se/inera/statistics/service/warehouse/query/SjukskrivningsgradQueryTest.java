package se.inera.statistics.service.warehouse.query;

import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SjukskrivningsgradQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 100);
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Integer>> count = SjukskrivningsgradQuery.count(sjukfall);
        assertEquals(1, count.get(3).getCount());
    }

    @Test
    public void useMax() {
        fact(4010, 25);
        fact(4010, 25);
        fact(4010, 25);
        fact(4010, 50);
        fact(4010, 50);
        fact(4010, 75);
        fact(4010, 75);
        fact(4010, 100);
        fact(4010, 100);
        fact(4010, 100);
        fact(4010, 100);
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Integer>> count = SjukskrivningsgradQuery.count(sjukfall);
        System.err.println(count);
        assertEquals(4, count.size());
        assertEquals(3, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(4, count.get(3).getCount());
    }

    private void fact(int startday, int grad) {
        Fact fact = new Fact(3, 380, 38002, 1, intyg++, patient++, startday, 0, 45, 0, 14, 16, grad, 10, 0, 32, 201010);
        warehouse.accept(fact, VARDGIVARE);
    }
}
