/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.scheduler.active;

import org.junit.Test;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

import static org.junit.Assert.*;

public class MessageJobTest {

    @Test
    public void testCheckLogContinueProcessingUntilDone() throws Exception {
        //Given
        MessageLogConsumer consumer = Mockito.mock(MessageLogConsumer.class);
        Mockito.when(consumer.processBatch()).thenReturn(100).thenReturn(1).thenReturn(0);
        final MessageJob messageJob = new MessageJob(consumer);

        //When
        messageJob.checkLog();

        //Then
        Mockito.verify(consumer, Mockito.times(3)).processBatch();
    }

}
