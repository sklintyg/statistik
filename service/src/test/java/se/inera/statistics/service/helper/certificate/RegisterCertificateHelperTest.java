/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.helper.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.stream.IntStream;
import javax.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;
import se.riv.clinicalprocess.healthcond.certificate.types.v3.PersonId;
import se.riv.clinicalprocess.healthcond.certificate.v3.Intyg;
import se.riv.clinicalprocess.healthcond.certificate.v3.Patient;

public class RegisterCertificateHelperTest {

    private static final String xmlIntyg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ns2:RegisterCertificate xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"\n" +
        "    xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\"\n" +
        "    xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">\n" +
        "  <ns2:intyg>\n" +
        "    <intygs-id>\n" +
        "      <ns3:root>SE2321000016-H489</ns3:root>\n" +
        "      <ns3:extension>1234567</ns3:extension>\n" +
        "    </intygs-id>\n" +
        "    <typ>\n" +
        "      <ns3:code>DB</ns3:code>\n" +
        "      <ns3:codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem>\n" +
        "      <ns3:displayName>Dödsbevis</ns3:displayName>\n" +
        "    </typ>\n" +
        "    <version>1.0</version>\n" +
        "    <signeringstidpunkt>2015-12-07T15:48:05</signeringstidpunkt>\n" +
        "    <skickatTidpunkt>2015-12-07T15:48:05</skickatTidpunkt>\n" +
        "    <patient>\n" +
        "      <person-id>\n" +
        "        <ns3:root>1.2.752.129.2.1.3.1</ns3:root>\n" +
        "        <ns3:extension>191212121212</ns3:extension>\n" +
        "      </person-id>\n" +
        "      <fornamn>Olivia</fornamn>\n" +
        "      <efternamn>Olsson</efternamn>\n" +
        "      <postadress>Testgatan 1</postadress>\n" +
        "      <postnummer>111 11</postnummer>\n" +
        "      <postort>Teststaden</postort>\n" +
        "    </patient>\n" +
        "    <skapadAv>\n" +
        "      <personal-id>\n" +
        "        <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "        <ns3:extension>SE2321000016-6G5R</ns3:extension>\n" +
        "      </personal-id>\n" +
        "      <fullstandigtNamn>Karl Karlsson</fullstandigtNamn>\n" +
        "      <forskrivarkod>09874321</forskrivarkod>\n" +
        "      <befattning>\n" +
        "        <ns3:code>201010</ns3:code>\n" +
        "        <ns3:codeSystem>1.2.752.129.2.2.1.4</ns3:codeSystem>\n" +
        "        <ns3:displayName>Överläkare</ns3:displayName>\n" +
        "      </befattning>\n" +
        "      <befattning>\n" +
        "        <ns3:code>204510</ns3:code>\n" +
        "        <ns3:codeSystem>1.2.752.129.2.2.1.4</ns3:codeSystem>\n" +
        "        <ns3:displayName>Psykolog</ns3:displayName>\n" +
        "      </befattning>\n" +
        "      <enhet>\n" +
        "        <enhets-id>\n" +
        "          <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "          <ns3:extension>Enhetsid</ns3:extension>\n" +
        "        </enhets-id>\n" +
        "        <arbetsplatskod>\n" +
        "          <ns3:root>1.2.752.29.4.71</ns3:root>\n" +
        "          <ns3:extension>45312</ns3:extension>\n" +
        "        </arbetsplatskod>\n" +
        "        <enhetsnamn>Vårdenheten</enhetsnamn>\n" +
        "        <postadress>Enhetsg. 1</postadress>\n" +
        "        <postnummer>100 10</postnummer>\n" +
        "        <postort>Stadby</postort>\n" +
        "        <telefonnummer>0812341234</telefonnummer>\n" +
        "        <epost>ve1@vg1.se</epost>\n" +
        "        <vardgivare>\n" +
        "          <vardgivare-id>\n" +
        "            <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "            <ns3:extension>SE2321000016-39KJ</ns3:extension>\n" +
        "          </vardgivare-id>\n" +
        "          <vardgivarnamn>Vårdgivaren</vardgivarnamn>\n" +
        "        </vardgivare>\n" +
        "      </enhet>\n" +
        "    </skapadAv>\n" +
        "    <svar id=\"1\">\n" +
        "      <delsvar id=\"1.1\">körkort</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"2\">\n" +
        "      <delsvar id=\"2.1\">false</delsvar>\n" +
        "      <delsvar id=\"2.2\">2017-01-01</delsvar>\n" +
        "      <delsvar id=\"2.3\">2017-01-02</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"3\">\n" +
        "      <delsvar id=\"3.1\">kommun</delsvar>\n" +
        "      <delsvar id=\"3.2\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>SJUKHUS</ns3:code>\n" +
        "          <ns3:codeSystem>65f0069f-14b5-4634-b187-5193580a3349</ns3:codeSystem>\n" +
        "          <ns3:displayName>Sjukhus</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"4\">\n" +
        "      <delsvar id=\"4.1\">true</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"5\">\n" +
        "      <delsvar id=\"5.1\">true</delsvar>\n" +
        "      <delsvar id=\"5.2\">true</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"6\">\n" +
        "      <delsvar id=\"6.1\">false</delsvar>\n" +
        "      <delsvar id=\"6.2\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>UNDERSOKNING_SKA_GORAS</ns3:code>\n" +
        "          <ns3:codeSystem>da46dd8c-b3f1-4e39-8d62-777d069213ea</ns3:codeSystem>\n" +
        "          <ns3:displayName>Rättsmedicinsk undersökning ska göras</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"7\">\n" +
        "      <delsvar id=\"7.1\">true</delsvar>\n" +
        "    </svar>\n" +
        "  </ns2:intyg>\n" +
        "</ns2:RegisterCertificate>";

    private RegisterCertificateHelper registerCertificateHelper = new RegisterCertificateHelper();


    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = registerCertificateHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTO() throws JAXBException {
        RegisterCertificateType intyg = registerCertificateHelper.unmarshalXml(xmlIntyg);

        IntygDTO dto = registerCertificateHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2015, 12, 7);

        assertEquals("191212121212", dto.getPatientid());
        assertEquals(IntygType.parseString("DB"), dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(102, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());

    }

    @Test
    public void testGetPatientDataHappyPath() throws Exception {
        final Patientdata result = callGetPatientdata("19500910-1824", LocalDate.of(2017, 02, 21));
        assertEquals(66, result.getAlder());
    }

    @Test
    public void testGetPatientDataWithTrailingSpace() throws Exception {
        final Patientdata result = callGetPatientdata("19500910-1824 ", LocalDate.of(2017, 02, 21));
        assertEquals(66, result.getAlder());
    }

    @Test
    public void testGetPatientDataWithStartingSpace() throws Exception {
        final Patientdata result = callGetPatientdata(" 19500910-1824", LocalDate.of(2017, 02, 21));
        assertEquals(66, result.getAlder());
    }

    @Test
    public void testGetPatientDataWithNull() throws Exception {
        final Patientdata result = callGetPatientdata(null, LocalDate.of(2017, 02, 21));
        Assert.assertEquals(ConversionHelper.NO_AGE, result.getAlder());
    }

    @Test
    public void testGetPatientDataWithNoDatePeriod() throws Exception {
        final Patientdata result = callGetPatientdata("19500910-1824", null);
        assertEquals(ConversionHelper.NO_AGE, result.getAlder());
    }

    private Patientdata callGetPatientdata(String pnr, LocalDate signeraDate) {
        RegisterCertificateType registerCertificateType = new RegisterCertificateType();
        final Intyg intyg = new Intyg();
        registerCertificateType.setIntyg(intyg);

        final Patient patient = new Patient();
        intyg.setPatient(patient);

        final PersonId personId = new PersonId();
        patient.setPersonId(personId);

        if (signeraDate != null) {
            intyg.setSigneringstidpunkt(signeraDate.atStartOfDay());
        }

        personId.setExtension(pnr);

        //When
        return registerCertificateHelper.getPatientData(registerCertificateType);
    }

    @Test
    public void unmarshalRegisterCertificateXmlHandlesConcurrentCalls() {
        IntStream.range(1, 25).parallel().forEach(value -> {
            try {
                registerCertificateHelper.unmarshalXml(xmlIntyg);
            } catch (JAXBException e) {
                //ok in this test
            } catch (Exception e) {
                fail("Unexpected exception: " + e.toString());
            }
        });
    }
}
