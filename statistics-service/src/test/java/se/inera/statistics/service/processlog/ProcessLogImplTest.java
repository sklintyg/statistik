package se.inera.statistics.service.processlog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml", "classpath:icd10.xml" })
@Transactional
@DirtiesContext
public class ProcessLogImplTest extends ProcessLogImpl {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void storedEventCanBeFetched() throws InterruptedException, NotSupportedException, SystemException {
        long id = store(EventType.CREATED, "data", "corr", 123L);
        IntygEvent event = get(id);
        assertEquals("data", event.getData());
    }

    @Test
    public void withNoNewEventsPollReturnsNothing() {
        List<IntygEvent> pending = getPending(2);
        assertTrue(pending.isEmpty());
    }

    @Test
    public void withTwoPendingEventPollReturnsFirstEvent() {
        store(EventType.CREATED, "1", "corr1", 123L);
        store(EventType.CREATED, "2", "corr2", 123L);

        List<IntygEvent> pending = getPending(2);
        assertEquals(2, pending.size());
        assertEquals("1", pending.get(0).getData());
    }

    @Test
    public void withTwoPendingEventsForSameIntygOnlyStoreFirst() {
        store(EventType.CREATED, "1", "corr", 123L);
        store(EventType.CREATED, "2", "corr", 123L);

        List<IntygEvent> pending = getPending(1);
        assertEquals("1", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertTrue(pending.isEmpty());
    }

    @Test
    public void withTwoPendingEventEachEventCanBeGottenInOrderAfterConfirm() {
        store(EventType.CREATED, "1", "corr1", 123L);
        store(EventType.CREATED, "2", "corr2", 123L);

        List<IntygEvent> pending = getPending(1);
        assertEquals("1", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertEquals("2", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertTrue(pending.isEmpty());
    }
    // CHECKSTYLE:ON MagicNumber
}
