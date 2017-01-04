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
package se.inera.statistics.service.queue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.DocumentHelper;
import se.inera.statistics.service.hsa.HSADecorator;
import se.inera.statistics.service.processlog.EventType;
import se.inera.statistics.service.processlog.IntygFormat;
import se.inera.statistics.service.processlog.ProcessLog;
import se.inera.statistics.service.processlog.Processor;
import se.inera.statistics.service.processlog.Receiver;

import com.fasterxml.jackson.databind.JsonNode;

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
        String data = JSONSource.readTemplateAsString(DocumentHelper.IntygVersion.VERSION1).toString();

        receiver.accept(EventType.CREATED, data, "corr", 123L);

        verify(processLog).store(EventType.CREATED, data, "corr", 123L);
        verify(hsaDecorator).decorate(any(JsonNode.class), anyString());
    }
    // CHECKSTYLE:ON MagicNumber
}
