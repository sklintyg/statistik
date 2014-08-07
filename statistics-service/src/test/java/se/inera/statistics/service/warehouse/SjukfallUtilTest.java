package se.inera.statistics.service.warehouse;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SjukfallUtilTest {
    private Aisle aisle = new Aisle();

    @Test
    public void oneIntygIsOneSjukfall() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 1, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 47, 0, 32, 201010);
        aisle.addLine(fact);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(1, sjukfalls.size());
    }

    @Test
    public void twoCloseIntygForSamePersonIsOneSjukfall() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 1, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 1, 1, 4025, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(1, sjukfalls.size());

        Sjukfall sjukfall = sjukfalls.iterator().next();
        assertEquals(2, sjukfall.getIntygCount());
        assertEquals(20, sjukfall.getRealDays());
    }

    @Test
    public void twoFarSeparatedIntygForSamePersonAreTwoSjukfall() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 1, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 1, 1, 4026, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void twoIntygForTwoPersonsAreTwoSjukfall() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 1, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 47, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 1, 2, 4010, 0, 45, 0, 14, 16, 100, 47, 0, 32, 201010);
        aisle.addLine(fact);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void sjukfallStartOnlyOnSelectedEnhetsButContinuesOnAnyEnhet() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 2, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 1, 1, 4020, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 2, 1, 1, 4030, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);

        fact = new Fact(3, 380, 38002, 2, 1, 2, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 3, 1, 2, 4020, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 2, 1, 2, 4030, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);

        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle, 1, 3);
        assertEquals(2, sjukfalls.size());
        assertEquals(2, sjukfalls.iterator().next().getIntygCount());
        assertEquals(4020, sjukfalls.iterator().next().getStart());
        assertEquals(20, sjukfalls.iterator().next().getRealDays());
    }

    @Test
    public void iterator() throws Exception {
        Fact fact = new Fact(3, 380, 38002, 2, 1, 1, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 1, 1, 1, 4020, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 2, 1, 1, 4030, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);

        fact = new Fact(3, 380, 38002, 2, 1, 2, 4010, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 3, 1, 2, 4020, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);
        fact = new Fact(3, 380, 38002, 2, 1, 2, 4030, 0, 45, 0, 14, 16, 100, 10, 0, 32, 201010);
        aisle.addLine(fact);

        aisle.sort();

        Iterator<SjukfallUtil.SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 3, 1, aisle, 2);
        assertTrue(actives.next().getSjukfall().isEmpty());

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(2, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());

        actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 2, 12, aisle, 2);

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(0, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());
    }
}
