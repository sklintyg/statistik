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

import org.junit.Test;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v2.RegisterCertificateType;

import javax.xml.bind.JAXBException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RegisterCertificateHelperTest {

    private static final String xmlIntyg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p1:RegisterCertificate xmlns:ns3=\"urn:riv:insuranceprocess:healthreporting:2\"\n" +
            "    xmlns:ns0=\"urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificateResponder:3\"\n" +
            "    xmlns:ns5=\"urn:riv:insuranceprocess:healthreporting:mu7263:3\"\n" +
            "    xmlns:p2=\"urn:riv:clinicalprocess:healthcond:certificate:2\"\n" +
            "    xmlns:p1=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2\"\n" +
            "    xmlns:p3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "   <p1:intyg>\n" +
            "      <p2:intygs-id>\n" +
            "         <p3:root>6ea04fd0-5fef-4809-823b-efeddf8a4d55</p3:root>\n" +
            "         <p3:extension>6ea04fd0-5fef-4809-823b-efeddf8a4d55</p3:extension>\n" +
            "      </p2:intygs-id>\n" +
            "      <p2:typ>\n" +
            "         <p3:code>\n" +
            "            FK7263\n" +
            "         </p3:code>\n" +
            "         <p3:codeSystem>\n" +
            "            f6fb361a-e31d-48b8-8657-99b63912dd9b\n" +
            "         </p3:codeSystem>\n" +
            "         <p3:displayName>Läkarintyg enligt 3 kap, 8 § lagen (1962:381) om allmän försäkring</p3:displayName>\n" +
            "      </p2:typ>\n" +
            "      <p2:version>1</p2:version>\n" +
            "      <p2:signeringstidpunkt>2013-03-17T00:00:00</p2:signeringstidpunkt>\n" +
            "      <p2:skickatTidpunkt>2013-03-17T00:00:00</p2:skickatTidpunkt>\n" +
            "      <p2:patient>\n" +
            "         <p2:person-id>\n" +
            "            <p3:root>1.2.752.129.2.1.3.1</p3:root>\n" +
            "            <p3:extension>19121212-1212</p3:extension>\n" +
            "         </p2:person-id>\n" +
            "         <p2:fornamn>Test</p2:fornamn>\n" +
            "         <p2:efternamn>Testorsson</p2:efternamn>\n" +
            "         <p2:postadress>.</p2:postadress>\n" +
            "         <p2:postnummer>.</p2:postnummer>\n" +
            "         <p2:postort>.</p2:postort>\n" +
            "      </p2:patient>\n" +
            "      <p2:skapadAv>\n" +
            "         <p2:personal-id>\n" +
            "            <p3:root>1.2.752.129.2.1.4.1</p3:root>\n" +
            "            <p3:extension>Personal HSA-ID</p3:extension>\n" +
            "         </p2:personal-id>\n" +
            "         <p2:fullstandigtNamn>En Läkare</p2:fullstandigtNamn>\n" +
            "         <p2:forskrivarkod>1234567</p2:forskrivarkod>\n" +
            "         <p2:enhet>\n" +
            "            <p2:enhets-id>\n" +
            "               <p3:root>1.2.752.129.2.1.4.1</p3:root>\n" +
            "               <p3:extension>Enhetsid</p3:extension>\n" +
            "            </p2:enhets-id>\n" +
            "            <p2:arbetsplatskod>\n" +
            "               <p3:root>1.2.752.29.4.71</p3:root>\n" +
            "               <p3:extension>123456789011</p3:extension>\n" +
            "            </p2:arbetsplatskod>\n" +
            "            <p2:enhetsnamn>Kir Mott</p2:enhetsnamn>\n" +
            "            <p2:postadress>Lasarettvägen 13</p2:postadress>\n" +
            "            <p2:postnummer>85150</p2:postnummer>\n" +
            "            <p2:postort>Sundsvall</p2:postort>\n" +
            "            <p2:telefonnummer>060-8188000</p2:telefonnummer>\n" +
            "            <p2:vardgivare>\n" +
            "               <p2:vardgivare-id>\n" +
            "                  <p3:root>1.2.752.129.2.1.4.1</p3:root>\n" +
            "                  <p3:extension>12345678</p3:extension>\n" +
            "               </p2:vardgivare-id>\n" +
            "               <p2:vardgivarnamn>Landstinget Norrland</p2:vardgivarnamn>\n" +
            "            </p2:vardgivare>\n" +
            "         </p2:enhet>\n" +
            "      </p2:skapadAv>\n" +
            "      <p2:svar id=\"1\">\n" +
            "         <p2:delsvar id=\"1.1\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>2</p3:code>\n" +
            "               <p3:codeSystem>KV_FKMU_0001</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "         <p2:delsvar id=\"1.2\">2013-03-10</p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"6\">\n" +
            "         <p2:delsvar id=\"6.1\">Klämskada på överarm</p2:delsvar>\n" +
            "         <p2:delsvar id=\"6.2\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>S47</p3:code>\n" +
            "               <p3:codeSystem>1.2.752.116.1.1.1.1.3</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"17\">\n" +
            "         <p2:delsvar id=\"17.1\">Kraftigt nedsatt rörlighet i överarmen pga skadan. Böj- och sträckförmågan är mycket dålig.\n" +
            "            Smärtar vid rörelse vilket ger att patienten inte kan använda armen särkilt mycket.\n" +
            "            Patienten bör/kan inte använda armen förrän skadan läkt. Skadan förvärras vid för tidigt\n" +
            "            påtvingad belastning. Patienten kan inte lyfta armen utan den ska hållas riktad nedåt och i fast läge så mycket\n" +
            "            som möjligt under tiden för läkning.\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"28\">\n" +
            "         <p2:delsvar id=\"28.1\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>1</p3:code>\n" +
            "               <p3:codeSystem>KV_FKMU_0002</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"29\">\n" +
            "         <p2:delsvar id=\"29.1\">Dirigent\n" +
            "            Dirigerar en för större orkester på deltid\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"32\">\n" +
            "         <p2:delsvar id=\"32.1\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>TRE_FJARDEDEL</p3:code>\n" +
            "               <p3:codeSystem>KV_FKMU_0003</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "         <p2:delsvar id=\"32.2\">\n" +
            "            <p3:datePeriod>\n" +
            "               <p3:start>2013-03-17</p3:start>\n" +
            "               <p3:end>2013-04-07</p3:end>\n" +
            "            </p3:datePeriod>\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"33\">\n" +
            "         <p2:delsvar id=\"33.1\">true</p2:delsvar>\n" +
            "         <p2:delsvar id=\"33.2\">\n" +
            "            En motivering\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"35\">\n" +
            "         <p2:delsvar id=\"35.1\">Patienten klämde höger överarm vid olycka i hemmet. Problemen har pågått en längre tid.\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"39\">\n" +
            "         <p2:delsvar id=\"39.1\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>4</p3:code>\n" +
            "               <p3:codeSystem>KV_FKMU_0006</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "         <p2:delsvar id=\"39.2\">Skadan har förvärrats vid varje tillfälle patienten använt armen. Måste hållas i total stillhet\n" +
            "            tills läkningsprocessen kommit en bit på väg. Eventuellt kan utredning visa att operation är nödvändig för att\n" +
            "            läka skadan.\n" +
            "         </p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "      <p2:svar id=\"40\">\n" +
            "         <p2:delsvar id=\"40.1\">\n" +
            "            <p3:cv>\n" +
            "               <p3:code>4</p3:code>\n" +
            "               <p3:codeSystem>KV_FKMU_0004</p3:codeSystem>\n" +
            "            </p3:cv>\n" +
            "         </p2:delsvar>\n" +
            "         <p2:delsvar id=\"40.2\">När skadan förbättrats rekommenderas muskeluppbyggande sjukgymnastik</p2:delsvar>\n" +
            "      </p2:svar>\n" +
            "   </p1:intyg>\n" +
            "</p1:RegisterCertificate>";

    private RegisterCertificateHelper registerCertificateHelper = new RegisterCertificateHelper();


    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = registerCertificateHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTO() throws JAXBException {
        RegisterCertificateType intyg = registerCertificateHelper.unmarshalRegisterCertificateXml(xmlIntyg);

        IntygDTO dto = registerCertificateHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2013, 3, 17);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals("FK7263", dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(100, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());

    }
}