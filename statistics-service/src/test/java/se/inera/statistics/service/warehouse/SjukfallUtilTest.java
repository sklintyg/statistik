package se.inera.statistics.service.warehouse;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

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
        assertEquals(2, sjukfall.intygCount);
        assertEquals(20, sjukfall.realDays);
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
}
