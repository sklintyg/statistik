package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:process-log-impl-test.xml"} )
@Transactional
public class ProcessLogImplTest extends ProcessLogImpl {

    @PersistenceContext(unitName="IneraStatisticsLog")
    EntityManager manager;

    
    @Test
    public void storedEventCanBeFetched() throws InterruptedException, NotSupportedException, SystemException {
        long id = store(EventType.CREATED, "data");
        Event event = get(id);
        assertEquals("data", event.getData());
    }
}
