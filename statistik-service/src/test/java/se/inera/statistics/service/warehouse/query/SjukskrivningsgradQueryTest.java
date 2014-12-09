package se.inera.statistics.service.warehouse.query;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

public class SjukskrivningsgradQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Test
    public void one() {
        fact(4010, 100);
        warehouse.complete(LocalDateTime.now());
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
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<Integer>> count = SjukskrivningsgradQuery.count(sjukfall);

        assertEquals(4, count.size());
        assertEquals(3, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(4, count.get(3).getCount());
    }

    private void fact(int startday, int grad) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(0).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(grad).withStartdatum(startday).withSjukskrivningslangd(10).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefatttning(new int[]{201010}).withLakarid(1).build();
        warehouse.accept(fact, VARDGIVARE);
    }

}
