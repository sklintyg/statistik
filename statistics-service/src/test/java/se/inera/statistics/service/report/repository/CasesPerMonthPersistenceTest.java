package se.inera.statistics.service.report.repository;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.service.report.model.CasesPerMonthRow;
import se.inera.statistics.service.report.model.Sex;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
public class CasesPerMonthPersistenceTest extends CasesPerMonthPersistenceHandler {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void store_nonexisting_row_female() {

        this.count("201302", Sex.Female);

        CasesPerMonthRow check = this.getCasesPerMonthRow("201302");
        assertEquals(check.getFemale(), 1);
    }

    @Test
    public void store_existing_row_female() {
        this.count("201302", Sex.Female);

        this.count("201302", Sex.Female);

        CasesPerMonthRow check = this.getCasesPerMonthRow("201302");
        assertEquals(check.getFemale(), 2);
    }

    @Test
    public void store_nonexisting_row_male() {

        this.count("201302", Sex.Male);

        CasesPerMonthRow check = this.getCasesPerMonthRow("201302");
        assertEquals(check.getMale(), 1);
    }

    @Test
    public void store_existing_row_male() {
        this.count("201302", Sex.Female);

        this.count("201302", Sex.Male);

        CasesPerMonthRow check = this.getCasesPerMonthRow("201302");
        assertEquals(check.getMale(), 1);
    }

    @Test
    public void getCasesPerMonthReturnsOldestFirst() {
        this.count("201302", Sex.Female);
        this.count("201304", Sex.Female);
        this.count("201301", Sex.Female);

        Iterator<CasesPerMonthRow> check = this.getCasesPerMonth().iterator();
        assertEquals("201301", check.next().getPeriod());
        assertEquals("201302", check.next().getPeriod());
        assertEquals("201304", check.next().getPeriod());
    }

    // CHECKSTYLE:ON

}
