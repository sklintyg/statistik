/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static se.inera.statistics.service.report.model.Kon.Female;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class SjukfallUtilTest {
    private Aisle aisle = new Aisle();

    @Test
    public void oneIntygIsOneSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010, 1, 47));
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(1, sjukfalls.size());
    }

    @Test
    public void twoCloseIntygForSamePersonIsOneSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010, 10, 1, 1));
        aisle.addLine(createFact(1, 1, 4025, 10, 1, 2));
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(1, sjukfalls.size());

        Sjukfall sjukfall = sjukfalls.iterator().next();
        assertEquals(2, sjukfall.getIntygCount());
        assertEquals(20, sjukfall.getRealDays());
        final List<Integer> lakare = Lists.transform(new ArrayList<>(sjukfall.getLakare()), new Function<Lakare, Integer>() {
            @Override
            public Integer apply(Lakare lakare) {
                return lakare.getId();
            }
        });
        assertEquals(2, lakare.size());
        assertTrue(lakare.contains(1));
        assertTrue(lakare.contains(2));
    }

    @Test
    public void twoFarSeparatedIntygForSamePersonAreTwoSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010));
        aisle.addLine(createFact(1, 1, 4026));
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void twoIntygForTwoPersonsAreTwoSjukfall() throws Exception {
        aisle.addLine(createFact(1, 1, 4010));
        aisle.addLine(createFact(1, 2, 4010));
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void sjukfallStartOnlyOnSelectedEnhetsButContinuesOnAnyEnhet() throws Exception {
        aisle.addLine(createFact(2, 1, 4010));
        aisle.addLine(createFact(1, 1, 4020));
        aisle.addLine(createFact(2, 1, 4030));

        aisle.addLine(createFact(2, 2, 4010));
        aisle.addLine(createFact(3, 2, 4020));
        aisle.addLine(createFact(2, 2, 4030));

        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle, 1, 3);
        assertEquals(2, sjukfalls.size());
        assertEquals(2, sjukfalls.iterator().next().getIntygCount());
        assertEquals(4020, sjukfalls.iterator().next().getStart());
        assertEquals(20, sjukfalls.iterator().next().getRealDays());
    }

    @Test
    public void iterator() throws Exception {
        aisle.addLine(createFact(2, 1, 4010));
        aisle.addLine(createFact(1, 1, 4020));
        aisle.addLine(createFact(2, 1, 4030));

        aisle.addLine(createFact(2, 2, 4010));
        aisle.addLine(createFact(3, 2, 4020));
        aisle.addLine(createFact(2, 2, 4030));

        aisle.sort();

        Iterator<SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 3, 1, aisle, new SjukfallUtil.EnhetFilter(2)).iterator();
        assertTrue(actives.next().getSjukfall().isEmpty());

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(2, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());

        actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 2, 12, aisle, new SjukfallUtil.EnhetFilter(2)).iterator();

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(0, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());
    }

    private Fact createFact(int enhet, int patient, int startdatum) {
        return createFact(enhet, patient, startdatum, 10, 1, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int lakarintyg) {
        return createFact(enhet, patient, startdatum, 10, lakarintyg, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int lakarintyg, int sjukskrivningslangd) {
        return createFact(enhet, patient, startdatum, sjukskrivningslangd, lakarintyg, 1);
    }

    private Fact createFact(int enhet, int patient, int startdatum, int sjukskrivningslangd, int lakarintyg, int lakarId) {
        return aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(enhet).
                    withLakarintyg(lakarintyg).withPatient(patient).withStartdatum(startdatum).withKon(Female).withAlder(45).
                    withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                    withSjukskrivningsgrad(100).withSjukskrivningslangd(sjukskrivningslangd).
                    withLakarkon(Female).withLakaralder(32).withLakarbefattning(new int[]{201010}).withLakarid(lakarId).build();
    }

    @Test
    public void oneSjukfalFromTwoDifferentEnhetsIsNotAffectedByEnhetFilter() throws Exception {
        int ENHET1 = 1;
        int ENHET2 = 2;
        LocalDate monthStart = new LocalDate("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET2, 1, WidelineConverter.toDay(monthStart.plusDays(10)), 2));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(20)), 3));

        aisle.sort();

        Iterator<SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle, new SjukfallUtil.EnhetFilter(ENHET1)).iterator();
        assertEquals(1, actives.next().getSjukfall().size());
    }

    @Test
    public void twoIntygsFarApartAreTwoSjukfalls() throws Exception {
        int ENHET1 = 1;
        LocalDate monthStart = new LocalDate("2010-11-01");
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart), 1));
        aisle.addLine(createFact(ENHET1, 1, WidelineConverter.toDay(monthStart.plusDays(20)), 3));

        aisle.sort();

        Iterator<SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle, new SjukfallUtil.EnhetFilter(ENHET1)).iterator();
        assertEquals(2, actives.next().getSjukfall().size());
    }

}
