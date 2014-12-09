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
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(47).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();

        aisle.addLine(fact);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(1, sjukfalls.size());
    }

    @Test
    public void twoCloseIntygForSamePersonIsOneSjukfall() throws Exception {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4025).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(2).build();
        aisle.addLine(fact2);
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
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4026).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void twoIntygForTwoPersonsAreTwoSjukfall() throws Exception {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(47).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(2).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(47).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle);
        assertEquals(2, sjukfalls.size());
    }

    @Test
    public void sjukfallStartOnlyOnSelectedEnhetsButContinuesOnAnyEnhet() throws Exception {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4020).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Fact fact3 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(1).withStartdatum(4030).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact3);

        Fact fact4 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(2).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact4);
        Fact fact5 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(3).
                withLakarintyg(1).withPatient(2).withStartdatum(4020).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact5);
        Fact fact6 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(2).withStartdatum(4030).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact6);

        Collection<Sjukfall> sjukfalls = SjukfallUtil.calculateSjukfall(aisle, 1, 3);
        assertEquals(2, sjukfalls.size());
        assertEquals(2, sjukfalls.iterator().next().getIntygCount());
        assertEquals(4020, sjukfalls.iterator().next().getStart());
        assertEquals(20, sjukfalls.iterator().next().getRealDays());
    }

    @Test
    public void iterator() throws Exception {
        Fact fact1 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(1).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact1);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(1).
                withLakarintyg(1).withPatient(1).withStartdatum(4020).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Fact fact3 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(1).withStartdatum(4030).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact3);

        Fact fact4 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(2).withStartdatum(4010).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact4);
        Fact fact5 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(3).
                withLakarintyg(1).withPatient(2).withStartdatum(4020).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact5);
        Fact fact6 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(2).
                withLakarintyg(1).withPatient(2).withStartdatum(4030).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact6);

        aisle.sort();

        Iterator<SjukfallUtil.SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 3, 1, aisle, 2).iterator();
        assertTrue(actives.next().getSjukfall().isEmpty());

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(2, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());

        actives = SjukfallUtil.sjukfallGrupper(new LocalDate("2010-11-01"), 2, 12, aisle, 2).iterator();

        assertEquals(2, actives.next().getSjukfall().size());
        assertEquals(0, actives.next().getSjukfall().size());
        assertFalse(actives.hasNext());
    }

    @Test
    public void oneSjukfalFromTwoDifferentEnhetsIsNotAffectedByEnhetFilter() throws Exception {
        int ENHET1 = 1;
        int ENHET2 = 2;
        LocalDate monthStart = new LocalDate("2010-11-01");
        Fact fact1 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(ENHET1).withLakarintyg(1).
                withPatient(1).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withSjukskrivningsgrad(100).
                withStartdatum(WidelineConverter.toDay(monthStart)).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact1);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(ENHET2).withLakarintyg(2).
                withPatient(1).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withSjukskrivningsgrad(100).
                withStartdatum(WidelineConverter.toDay(monthStart.plusDays(10))).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);
        Fact fact3 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(ENHET1).withLakarintyg(3).
                withPatient(1).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withSjukskrivningsgrad(100).
                withStartdatum(WidelineConverter.toDay(monthStart.plusDays(20))).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact3);

        aisle.sort();

        Iterator<SjukfallUtil.SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle, ENHET1).iterator();
        assertEquals(1, actives.next().getSjukfall().size());
    }

    @Test
    public void twoIntygsFarApartAreTwoSjukfalls() throws Exception {
        int ENHET1 = 1;
        LocalDate monthStart = new LocalDate("2010-11-01");
        Fact fact1 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(ENHET1).withLakarintyg(1).
                withPatient(1).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withSjukskrivningsgrad(100).
                withStartdatum(WidelineConverter.toDay(monthStart)).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact1);
        Fact fact2 = aFact().withLan(3).withKommun(380).withForsamling(38002).withEnhet(ENHET1).withLakarintyg(3).
                withPatient(1).withKon(Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).withSjukskrivningsgrad(100).
                withStartdatum(WidelineConverter.toDay(monthStart.plusDays(20))).withSjukskrivningslangd(10).
                withLakarkon(Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        aisle.addLine(fact2);

        aisle.sort();

        Iterator<SjukfallUtil.SjukfallGroup> actives = SjukfallUtil.sjukfallGrupper(monthStart, 1, 1, aisle, ENHET1).iterator();
        assertEquals(2, actives.next().getSjukfall().size());
    }

}
