package se.inera.statistics.service.report.repository;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.model.SimpleKonDataRow;
import se.inera.statistics.service.report.model.SimpleKonResponse;
import se.inera.statistics.service.report.util.Verksamhet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
@DirtiesContext
public class SjukfallPerManadPersistenceTest extends SjukfallPerManadPersistenceHandler {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void store_nonexisting_row_female() {
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Female);

        Range range = new Range(new LocalDate("2013-02"), new LocalDate("2013-02"));
        SimpleKonResponse<SimpleKonDataRow> check = this.getCasesPerMonth("hsaid", range);
        assertEquals(1, check.getRows().get(0).getFemale().intValue());
    }

    @Test
    public void store_existing_row_female() {
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Female);
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Female);

        Range range = new Range(new LocalDate("2013-02"), new LocalDate("2013-02"));
        SimpleKonResponse<SimpleKonDataRow> check = this.getCasesPerMonth("hsaid", range);
        assertEquals(2, check.getRows().get(0).getFemale().intValue());
    }

    @Test
    public void store_nonexisting_row_male() {
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Male);

        Range range = new Range(new LocalDate("2013-02"), new LocalDate("2013-02"));
        SimpleKonResponse<SimpleKonDataRow> check = this.getCasesPerMonth("hsaid", range);
        assertEquals(1, check.getRows().get(0).getMale().intValue());
    }

    @Test
    public void store_existing_row_male() {
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Female);
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Male);

        Range range = new Range(new LocalDate("2013-02"), new LocalDate("2013-02"));
        SimpleKonResponse<SimpleKonDataRow> check = this.getCasesPerMonth("hsaid", range);
        assertEquals(1, check.getRows().get(0).getMale().intValue());
    }

    @Test
    public void getCasesPerMonthReturnsOldestFirst() {
        this.count("hsaid", "2013-02", Verksamhet.ENHET, Kon.Female);
        this.count("hsaid", "2013-04", Verksamhet.ENHET, Kon.Female);
        this.count("hsaid", "2013-01", Verksamhet.ENHET, Kon.Female);

        Range range = new Range(new LocalDate("2013-01"), new LocalDate("2013-04"));

        Iterator<SimpleKonDataRow> check = this.getCasesPerMonth("hsaid", range).getRows().iterator();
        assertEquals("jan 2013", check.next().getName());
        assertEquals("feb 2013", check.next().getName());
        assertEquals("mar 2013", check.next().getName());
        assertEquals("apr 2013", check.next().getName());
    }

    // CHECKSTYLE:ON

}
