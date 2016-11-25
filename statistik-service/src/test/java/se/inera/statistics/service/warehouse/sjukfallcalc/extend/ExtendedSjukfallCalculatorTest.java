package se.inera.statistics.service.warehouse.sjukfallcalc.extend;

import org.junit.Test;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.SjukfallExtended;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExtendedSjukfallCalculatorTest {

    @Test
    public void testGetExtendedSjukfallStartEmptyAisleWillReturnUnextendedSjukfall() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(1, 2, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertSame(sjukfall, result);
    }

    @Test
    public void testGetExtendedSjukfallStartOneIntygInAisleCanAndWillExtendStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(50, 150, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(50, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartOneIntygInAisleWillNotExtendEndDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(150, 250, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(100, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartSeveralOverlappingIntygWillExtendToEarliestStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(40, 85, 1));
        aisle.add(createFact(80, 150, 1));
        aisle.add(createFact(10, 55, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(10, result.getStart());
        assertEquals(200, result.getEnd());
    }

    @Test
    public void testGetExtendedSjukfallStartIntygOnOtherPatientWillNotExtendStartDate() throws Exception {
        //Given
        final ArrayList<Fact> aisle = new ArrayList<>();
        aisle.add(createFact(40, 85, 2));
        aisle.add(createFact(80, 150, 1));
        aisle.add(createFact(10, 55, 1));
        final long patient = 1L;
        final SjukfallExtended sjukfall = new SjukfallExtended(createFact(100, 200, 1));

        //When
        final ExtendedSjukfallCalculator extendedSjukfallCalculator = new ExtendedSjukfallCalculator(aisle);
        final SjukfallExtended result = extendedSjukfallCalculator.getExtendedSjukfallStart(patient, sjukfall);

        //Then
        assertEquals(80, result.getStart());
        assertEquals(200, result.getEnd());
    }

    private Fact createFact(int startdatum, int slutdatum, int patient) {
        return new Fact(1,1,1,1,1, patient, startdatum, slutdatum,1,1,1,1,1,1,1,1,1, new int[0],1,false);
    }

}
