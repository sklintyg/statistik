/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.processlog;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.statistics.service.IntygCreatorHelper;
import se.inera.statistics.service.helper.RegisterCertificateResolver;
import se.inera.statistics.service.helper.certificate.TsBasHelper;
import se.inera.statistics.service.helper.certificate.TsDiabetesHelper;
import se.inera.statistics.service.hsa.HSADecorator;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

@RunWith(MockitoJUnitRunner.class)
public class ReceiverTest {

    @Mock
    private RegisterCertificateResolver registerCertificateResolver;

    @Mock
    private TsBasHelper tsBasHelper;

    @Mock
    private TsDiabetesHelper tsDiabetesHelper;

    @Mock
    private ProcessLog processLog;

    @Mock
    private HSADecorator hsaDecorator;

    @InjectMocks
    private Receiver receiver;

    @Test
    public void testAcceptIncreaseAccepted() {
        String intyg = "";
        String documentId = "";
        long timestamp = 0;

        long accepted = receiver.getAccepted();

        receiver.accept(EventType.CREATED, intyg, documentId, timestamp);

        assertEquals(accepted + 1, receiver.getAccepted());
        Mockito.verify(hsaDecorator, times(0)).populateHsaData(Mockito.any(RegisterTSBasType.class), Mockito.anyString());
    }

    @Test
    public void testAcceptTsBas() {
        String intyg = IntygCreatorHelper.getTsBas();
        String documentId = "123";
        long timestamp = 0;

        long accepted = receiver.getAccepted();

        receiver.accept(EventType.CREATED, intyg, documentId, timestamp);

        assertEquals(accepted + 1, receiver.getAccepted());

        Mockito.verify(hsaDecorator, times(1)).populateHsaData(nullable(RegisterTSBasType.class), eq(documentId));
    }

    @Test
    public void testAcceptTsDiabetes() {
        String intyg = IntygCreatorHelper.getTsDiabetes();
        String documentId = "123";
        long timestamp = 0;

        long accepted = receiver.getAccepted();

        receiver.accept(EventType.CREATED, intyg, documentId, timestamp);

        assertEquals(accepted + 1, receiver.getAccepted());

        Mockito.verify(hsaDecorator, times(1)).populateHsaData(nullable(RegisterTSDiabetesType.class), eq(documentId));
    }

    @Test
    public void testAcceptJson() {
        String intyg = IntygCreatorHelper.getFk7263Json();
        String documentId = "123";
        long timestamp = 0;

        long accepted = receiver.getAccepted();

        receiver.accept(EventType.CREATED, intyg, documentId, timestamp);

        assertEquals(accepted + 1, receiver.getAccepted());

        Mockito.verify(hsaDecorator, times(1)).decorate(Mockito.any(JsonNode.class), eq(documentId));
    }

    @Test
    public void testAcceptRegisterCertificate() {
        String intyg = IntygCreatorHelper.getFk7263Xml();
        String documentId = "123";
        long timestamp = 0;

        long accepted = receiver.getAccepted();

        receiver.accept(EventType.CREATED, intyg, documentId, timestamp);

        assertEquals(accepted + 1, receiver.getAccepted());

        Mockito.verify(hsaDecorator, times(1)).populateHsaData(nullable(RegisterCertificateType.class), eq(documentId));
    }
}
