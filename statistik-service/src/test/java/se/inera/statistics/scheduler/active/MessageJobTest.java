/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.statistics.scheduler.active;

import org.junit.Test;
import org.mockito.Mockito;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

public class MessageJobTest {

    @Test
    public void testCheckLogContinueProcessingUntilDone() throws Exception {
        //Given
        MessageLogConsumer consumer = Mockito.mock(MessageLogConsumer.class);
        Mockito.when(consumer.processBatch(Mockito.anyLong())).thenReturn(100L).thenReturn(101L).thenReturn(101L);
        final MessageJob messageJob = new MessageJob(consumer);

        //When
        messageJob.checkLog();

        //Then
        Mockito.verify(consumer, Mockito.times(3)).processBatch(Mockito.anyLong());
    }

}
