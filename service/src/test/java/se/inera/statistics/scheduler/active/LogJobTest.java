/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;
import se.inera.statistics.service.monitoring.MonitoringLogService;
import se.inera.statistics.service.processlog.LogConsumer;
import se.inera.statistics.service.processlog.intygsent.IntygsentLogConsumer;
import se.inera.statistics.service.processlog.message.MessageLogConsumer;

public class LogJobTest {

    @Test
    public void testCheckLogContinueProcessingUntilDone() {
        //Given
        MonitoringLogService monitoringLogService = Mockito.mock(MonitoringLogService.class);
        LogConsumer logConsumer = Mockito.mock(LogConsumer.class);
        IntygsentLogConsumer intygsentLogConsumer = Mockito.mock(IntygsentLogConsumer.class);
        MessageLogConsumer messageLogConsumer = Mockito.mock(MessageLogConsumer.class);
        LogMDCHelper mdcHelper = new LogMDCHelper();

        Mockito.when(logConsumer.processBatch()).thenReturn(100).thenReturn(0);
        Mockito.when(intygsentLogConsumer.processBatch()).thenReturn(100).thenReturn(100).thenReturn(100).thenReturn(100).thenReturn(0);
        Mockito.when(messageLogConsumer.processBatch(Mockito.anyLong())).thenReturn(100L).thenReturn(101L).thenReturn(101L);

        final LogJob logJob = new LogJob(monitoringLogService, logConsumer, intygsentLogConsumer, messageLogConsumer, mdcHelper);

        //When
        logJob.run();

        //Then
        Mockito.verify(logConsumer, Mockito.times(2)).processBatch();
        Mockito.verify(intygsentLogConsumer, Mockito.times(5)).processBatch();
        Mockito.verify(messageLogConsumer, Mockito.times(3)).processBatch(Mockito.anyLong());
    }

}
