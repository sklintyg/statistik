package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Test
    public void withNoNewEventsPollReturnsNothing() {
        Event pending = getPending();
        assertNull(pending);
    }

    @Test
    public void withTwoPendingEventPollReturnsFirstEvent() {
        store(EventType.CREATED, "1");
        store(EventType.CREATED, "2");
        
        Event pending = getPending();
        assertEquals("1", pending.getData());
        assertEquals("1", pending.getData());
    }

    @Test
    public void withTwoPendingEventEacheEventCanBeGottenInOrderAfterConfirm() {
        store(EventType.CREATED, "1");
        store(EventType.CREATED, "2");
        
        Event pending = getPending();
        assertEquals("1", pending.getData());
        confirm(pending.getId());
        
        pending = getPending();
        assertEquals("2", pending.getData());
        confirm(pending.getId());

        pending = getPending();
        assertNull(pending);
    }

}
