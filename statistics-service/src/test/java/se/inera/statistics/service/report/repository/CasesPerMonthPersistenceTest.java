package se.inera.statistics.service.report.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import se.inera.statistics.service.report.model.Sex;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
public class CasesPerMonthPersistenceTest extends CasesPerMonthPersistenceHandler {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void storedEventCanBeFetched() throws InterruptedException, NotSupportedException, SystemException {
        this.count("201302", "nationell", Sex.Female);


    }


    @Test
    public void store_nonexisting_row() {
        CasesPerMonthPersistenceHandler casesPerMonthPersistenceHandler;
    }
}
