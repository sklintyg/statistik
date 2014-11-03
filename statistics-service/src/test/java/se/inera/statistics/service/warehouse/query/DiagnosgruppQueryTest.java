package se.inera.statistics.service.warehouse.query;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static se.inera.statistics.service.warehouse.Fact.aFact;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10.xml", "classpath:query-test.xml"})
public class DiagnosgruppQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Autowired
    private Icd10 icd10;

    @Autowired
    private DiagnosgruppQuery query;

    @Test
    public void one() {
        fact(4010, 0);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<String,Counter<String>> count = query.count(sjukfall);
        assertEquals(1, count.get(icd10.getKapitel("A00-B99").getId()).getCount());
    }

    @Test
    public void useMax() {
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 0);
        fact(4010, 200);
        fact(4010, 200);
        fact(4010, 400);
        fact(4010, 400);
        fact(4010, 500);
        fact(4010, 500);
        fact(4010, 500);
        fact(4010, 600);
        warehouse.complete(LocalDateTime.now());
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<String>> count = query.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(3, count.get(3).getCount());
    }

    private void fact(int startday, int diagnoskapitel) {
        Fact fact = aFact().withLan(3).withKommun(380).withForsamling(38002).
                withEnhet(1).withLakarintyg(intyg++).
                withPatient(patient++).withKon(Kon.Female).withAlder(45).
                withDiagnoskapitel(diagnoskapitel).withDiagnosavsnitt(14).withDiagnoskategori(16).
                withSjukskrivningsgrad(100).withStartdatum(startday).withSjukskrivningslangd(10).
                withLakarkon(Kon.Female).withLakaralder(32).withLakarbefatttning(201010).withLakarid(1).build();

        warehouse.accept(fact, VARDGIVARE);
    }
}
