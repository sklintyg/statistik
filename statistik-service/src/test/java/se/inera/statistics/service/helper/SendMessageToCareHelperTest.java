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
package se.inera.statistics.service.helper;

import org.junit.Before;
import org.junit.Test;
import se.inera.intyg.common.support.common.enumerations.PartKod;
import se.inera.statistics.service.report.model.Kon;
import se.riv.clinicalprocess.healthcond.certificate.sendMessageToCare.v1.SendMessageToCareType;

import javax.xml.bind.JAXBException;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class SendMessageToCareHelperTest {

    private static final String messageXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SendMessageToCare xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:SendMessageToCareResponder:1\" xmlns:urn2=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "  <meddelande-id>fd59cab6-942c-4b5d-a932-ea5117783afa</meddelande-id>\n" +
            "  <!--Optional:-->\n" +
            "  <referens-id>ref1</referens-id>\n" +
            "  <skickatTidpunkt>2016-11-02T00:00:00</skickatTidpunkt>\n" +
            "  <intygs-id>\n" +
            "    <urn2:root>?</urn2:root>\n" +
            "    <urn2:extension>fd59cab6-942c-4b5d-a932-ea5117783af7</urn2:extension>\n" +
            "  </intygs-id>\n" +
            "  <patientPerson-id>\n" +
            "    <urn2:root>1.2.752.129.2.1.3.1</urn2:root>\n" +
            "    <urn2:extension>191212121212</urn2:extension>\n" +
            "  </patientPerson-id>\n" +
            "  <logiskAdressMottagare>fk</logiskAdressMottagare>\n" +
            "  <amne>\n" +
            "    <urn2:code>KOMPLT</urn2:code>\n" +
            "    <urn2:codeSystem>ffa59d8f-8d7e-46ae-ac9e-31804e8e8499</urn2:codeSystem>\n" +
            "    <!--Optional:-->\n" +
            "    <urn2:displayName>Test</urn2:displayName>\n" +
            "  </amne>\n" +
            "  <!--Optional:-->\n" +
            "  <rubrik>Rubrik</rubrik>\n" +
            "  <meddelande>meddelande</meddelande>\n" +
            "  <skickatAv>\n" +
            "    <part>\n" +
            "      <urn2:code>FKASSA</urn2:code>\n" +
            "      <urn2:codeSystem>769bb12b-bd9f-4203-a5cd-fd14f2eb3b80</urn2:codeSystem>\n" +
            "    </part>\n" +
            "  </skickatAv>\n" +
            "  <sistaDatumForSvar>2016-11-02</sistaDatumForSvar>\n" +
            "</SendMessageToCare>";

    private SendMessageToCareType message;
    private SendMessageToCareHelper helper = new SendMessageToCareHelper();

    @Before
    public void setup() throws JAXBException {
        message = helper.unmarshalSendMessageToCareTypeXml(messageXml);
    }

    @Test
    public void testGetIntygsId() {
        assertEquals("fd59cab6-942c-4b5d-a932-ea5117783af7", helper.getIntygId(message));
    }

    @Test
    public void testGetSkickatAv() {
        assertEquals(PartKod.FKASSA, helper.getSkickatAv(message));
    }

    @Test
    public void testGetPatientId() {
        assertEquals("191212121212", helper.getPatientId(message));
    }

    @Test
    public void testGetAmneCode() {
        assertEquals("KOMPLT", helper.getAmneCode(message));
    }

    @Test
    public void testGetPatientData() {
        Patientdata expected = new Patientdata(103 ,Kon.MALE);

        Patientdata patientdata = helper.getPatientData(message);
        assertEquals(expected.getKon(), patientdata.getKon());
        assertEquals(expected.getAlder(), patientdata.getAlder());
    }

    @Test
    public void testGetSkickatTidpunkt() {
        LocalDateTime dateTime = LocalDateTime.of(2016, 11, 2, 0, 0, 0);

        assertEquals(dateTime, helper.getSkickatTidpunkt(message));
    }
}
