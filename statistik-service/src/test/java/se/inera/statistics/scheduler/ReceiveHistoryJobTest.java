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
package se.inera.statistics.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.statistics.scheduler.active.ReceiveHistoryJob;
import se.inera.statistics.service.processlog.Receiver;

@RunWith(MockitoJUnitRunner.class)
public class ReceiveHistoryJobTest {

    @Mock
    private Receiver receiver = Mockito.mock(Receiver.class);
    
    @InjectMocks
    ReceiveHistoryJob history = new ReceiveHistoryJob();

    @Test
    public void initialRateIsZero() {
        assertEquals(0,  history.getCurrentRate());
    }

    @Test
    public void rateIncreasesWithMeasurement() {
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        history.checkReceived();
        assertEquals(1, history.getCurrentRate());
    }

    @Test
    public void rateFlattensForConstantValues() {
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS - 1; i++) {
            history.checkReceived();
        }
        assertEquals(1, history.getCurrentRate());
        history.checkReceived();
        assertEquals(0, history.getCurrentRate());
    }

    @Test
    public void rollOverHistory() {
        // Fill history with 1
        Mockito.when(receiver.getAccepted()).thenReturn(1L);
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS; i++) {
            history.checkReceived();
        }
        assertEquals(0, history.getCurrentRate());

        // Add 3 to history
        Mockito.when(receiver.getAccepted()).thenReturn(3L);
        history.checkReceived();
        assertEquals(2, history.getCurrentRate());

        // Fill history with 3
        for (int i = 0; i < ReceiveHistoryJob.HISTORY_ITEMS; i++) {
            history.checkReceived();
        }
        assertEquals(0, history.getCurrentRate());
    }
}
