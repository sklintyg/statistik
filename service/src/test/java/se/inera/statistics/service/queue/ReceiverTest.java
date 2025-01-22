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
package se.inera.statistics.service.queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.Processor;
import se.inera.statistics.service.processlog.Receiver;

@RunWith(MockitoJUnitRunner.class)
public class ReceiverTest {

    @Mock
    private ProcessLog processLog = Mockito.mock(ProcessLog.class);
    @Mock
    private Processor processor = Mockito.mock(Processor.class);
    @Mock
    private HSADecorator hsaDecorator = Mockito.mock(HSADecorator.class);

    @InjectMocks
    private Receiver receiver = new Receiver();

    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void acceptedEventdataIsStored() {
        String data = JSONSource.readTemplateAsString();

        receiver.accept(EventType.CREATED, data, "corr", 123L);

        verify(processLog).store(EventType.CREATED, data, "corr", 123L);
        verify(hsaDecorator).decorate(any(JsonNode.class), anyString());
    }
    // CHECKSTYLE:ON MagicNumber
}
