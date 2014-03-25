package se.inera.statistics.service.warehouse.query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.statistics.service.report.util.Icd10;
import se.inera.statistics.service.warehouse.Fact;
import se.inera.statistics.service.warehouse.Sjukfall;
import se.inera.statistics.service.warehouse.SjukfallUtil;
import se.inera.statistics.service.warehouse.Warehouse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:icd10-test.xml", "classpath:query-test.xml"})
public class DiagnosgruppQueryTest {

    public static final String VARDGIVARE = "vardgivare";
    private Warehouse warehouse = new Warehouse();

    private int intyg;
    private int patient;

    @Autowired
    private Icd10 icd10;

    @Test
    public void one() {
        fact(4010, 0);
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        Map<String,Counter<String>> count = DiagnosgruppQuery.count(sjukfall);
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
        Collection<Sjukfall> sjukfall = SjukfallUtil.calculateSjukfall(warehouse.get(VARDGIVARE));
        List<Counter<String>> count = DiagnosgruppQuery.count(sjukfall, 4);

        assertEquals(4, count.size());
        assertEquals(4, count.get(0).getCount());
        assertEquals(2, count.get(1).getCount());
        assertEquals(3, count.get(3).getCount());
    }

    private void fact(int startday, int diagnoskapitel) {
        Fact fact = new Fact(3, 380, 38002, 1, intyg++, patient++, startday, 0, 45, diagnoskapitel, 14, 16, 100, 10, 0, 32, 201010);
        warehouse.accept(fact, VARDGIVARE);
    }
}
