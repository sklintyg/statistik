/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.processlog.message;

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
public class ProcessMessageLogImplTest extends ProcessMessageLogImpl {

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void storedEventCanBeFetched() throws InterruptedException, NotSupportedException, SystemException {
        long id = store(MessageEventType.SENT, "data", "corr", 123L);
        MessageEvent event = get(id);
        assertEquals("data", event.getData());
    }

    @Test
    public void withNoNewEventsPollReturnsNothing() {
        List<MessageEvent> pending = getPending(2);
        assertTrue(pending.isEmpty());
    }

    @Test
    public void withTwoPendingEventPollReturnsFirstEvent() {
        store(MessageEventType.SENT, "1", "corr1", 123L);
        store(MessageEventType.SENT, "2", "corr2", 123L);

        List<MessageEvent> pending = getPending(2);
        assertEquals(2, pending.size());
        assertEquals("1", pending.get(0).getData());
    }

    @Test
    public void withTwoPendingEventsForSameIntygOnlyStoreFirst() {
        store(MessageEventType.SENT, "1", "corr", 123L);
        store(MessageEventType.SENT, "2", "corr", 123L);

        List<MessageEvent> pending = getPending(1);
        assertEquals("1", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertTrue(pending.isEmpty());
    }

    @Test
    public void withTwoPendingEventEachEventCanBeGottenInOrderAfterConfirm() {
        store(MessageEventType.SENT, "1", "corr1", 123L);
        store(MessageEventType.SENT, "2", "corr2", 123L);

        List<MessageEvent> pending = getPending(1);
        assertEquals("1", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertEquals("2", pending.get(0).getData());
        confirm(pending.get(0).getId());

        pending = getPending(2);
        assertTrue(pending.isEmpty());
    }
    // CHECKSTYLE:ON MagicNumber

    @Transactional
    private MessageEvent get(long id) {
        return getManager().find(MessageEvent.class, id);
    }
}