/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.processlog.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:process-log-impl-test.xml", "classpath:icd10.xml"})
@Transactional
@DirtiesContext
public class ProcessMessageLogImplTest extends ProcessMessageLogImpl {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    @Ignore
    public void storedEventCanBeFetched() {
        long id = store(MessageEventType.SENT, "data", "corr", 123L);
        MessageEvent event = get(id);
        assertEquals("data", event.getData());
    }

    @Test
    @Ignore
    public void withNoNewEventsPollReturnsNothing() {
        List<MessageEvent> pending = getPending(2, 0, 1000);
        assertTrue(pending.isEmpty());
    }

    @Test
    @Ignore
    public void withTwoPendingEventPollReturnsFirstEvent() {
        store(MessageEventType.SENT, "1", "corr1", 123L);
        store(MessageEventType.SENT, "2", "corr2", 123L);

        List<MessageEvent> pending = getPending(2, 0, 1000);
        assertEquals(2, pending.size());
        assertEquals("1", pending.get(0).getData());
    }

    @Test
    @Ignore
    public void testSetProcessed() {
        // Given
        List<MessageEvent> pending = getPending(100, 0, 100);

        int pendingCount = pending.size();

        MessageEvent event = new MessageEvent(MessageEventType.SENT, "1", "corr1", 123L);
        long id = update(event);

        pending = getPending(100, 0, 100);

        int pendingCountAfterAdd = pending.size();

        assertEquals(pendingCount + 1, pendingCountAfterAdd);

        // When
        setProcessed(id);

        // Then
        pending = getPending(100, 0, 100);
        int pendingCountProcessed = pending.size();

        assertEquals(pendingCount, pendingCountProcessed);
    }

    @Test
    @Ignore
    public void testMaxNumberOfTries() {
        MessageEvent event = new MessageEvent(MessageEventType.SENT, "1", "corr1", 123L);
        event.setTries(4);
        update(event);

        List<MessageEvent> pending = getPending(2, 0, 1000);
        assertEquals(1, pending.size());

        pending = getPending(2, 0, 4);
        assertEquals(1, pending.size());

        pending = getPending(2, 0, 3);
        assertEquals(0, pending.size());
    }

    // CHECKSTYLE:ON MagicNumber

    @Transactional
    protected MessageEvent get(long id) {
        return getManager().find(MessageEvent.class, id);
    }
}
