package se.inera.statistics.service.warehouse;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class AisleTest {

    private Aisle aisle = new Aisle();

    @Test
    public void outOfOrderFactsGetsSorted() {
        Fact fact = new Fact(3, 380, 38002, 1, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 47, 0, 32, 201010, 0);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 2, 1, 4000, 0, 45, 0, 14, 16, 100, 47, 0, 32, 201010, 0);
        aisle.addLine(fact);
        aisle.sort();
        Iterator<Fact> iterator = aisle.iterator();
        assertEquals(2, iterator.next().getLakarintyg());
        assertEquals(1, iterator.next().getLakarintyg());
    }
}
