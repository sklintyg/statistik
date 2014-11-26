package se.inera.statistics.service.warehouse.query;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;
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
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class AldersgruppQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 10, 45);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<Ranges.Range,Counter<Ranges.Range>> count = AldersgruppQuery.count(sjukfall);
        assertEquals(1, count.get(AldersgroupUtil.RANGES.rangeFor("41-45 år")).getCount());
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
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Ranges.Range>> count = AldersgruppQuery.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(3, count.get(3).getCount());
    }

    private void fact(int startday, int length, int alder) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.Female).withAlder(alder).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSjukskrivningslangd(length).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefatttning(201010).withLakarid(1).build();
        warehouse.accept(fact, VARDGIVARE);
    }
}
