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
package se.inera.statistics.service.helper.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.stream.IntStream;
import javax.xml.bind.JAXBException;
import org.junit.Test;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

public class Tstrk1009RegisterCertificateHelperTest {

    private static final String xmlIntyg =
        "<ns4:RegisterCertificate xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:dsf=\"http://www.w3.org/2002/06/xmldsig-filter2\" xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:3\" xmlns:ns4=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\" xmlns:ns5=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns:ns6=\"urn:riv:clinicalprocess:healthcond:certificate:3.2\" xmlns:ns7=\"urn:riv:itintegration:registry:1\">\n"
            +
            "  <ns4:intyg>\n" +
            "    <ns3:intygs-id>\n" +
            "      <ns5:root>TSTNMT2321000156-1077</ns5:root>\n" +
            "      <ns5:extension>ebd91ddb-3acf-4f96-aeb6-d83bde8d12d3</ns5:extension>\n" +
            "    </ns3:intygs-id>\n" +
            "    <ns3:typ>\n" +
            "      <ns5:code>TSTRK1009</ns5:code>\n" +
            "      <ns5:codeSystem>f6fb361a-e31d-48b8-8657-99b63912dd9b</ns5:codeSystem>\n" +
            "      <ns5:displayName>Transportstyrelsens läkarintyg</ns5:displayName>\n" +
            "    </ns3:typ>\n" +
            "    <ns3:version>1.0</ns3:version>\n" +
            "    <ns3:signeringstidpunkt>2019-01-16T14:02:40</ns3:signeringstidpunkt>\n" +
            "    <ns3:skickatTidpunkt>2019-01-16T14:02:40</ns3:skickatTidpunkt>\n" +
            "    <ns3:patient>\n" +
            "      <ns3:person-id>\n" +
            "        <ns5:root>1.2.752.129.2.1.3.1</ns5:root>\n" +
            "        <ns5:extension>191212121212</ns5:extension>\n" +
            "      </ns3:person-id>\n" +
            "      <ns3:fornamn>Tolvan</ns3:fornamn>\n" +
            "      <ns3:efternamn>Tolvansson</ns3:efternamn>\n" +
            "      <ns3:postadress>Svensson, Storgatan 1, PL 1234</ns3:postadress>\n" +
            "      <ns3:postnummer>12345</ns3:postnummer>\n" +
            "      <ns3:postort>Småmåla</ns3:postort>\n" +
            "    </ns3:patient>\n" +
            "    <ns3:skapadAv>\n" +
            "      <ns3:personal-id>\n" +
            "        <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
            "        <ns5:extension>TSTNMT2321000156-1079</ns5:extension>\n" +
            "      </ns3:personal-id>\n" +
            "      <ns3:fullstandigtNamn>Arnold Johansson</ns3:fullstandigtNamn>\n" +
            "      <ns3:forskrivarkod>0000000</ns3:forskrivarkod>\n" +
            "      <ns3:enhet>\n" +
            "        <ns3:enhets-id>\n" +
            "          <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
            "          <ns5:extension>TSTNMT2321000156-1077</ns5:extension>\n" +
            "        </ns3:enhets-id>\n" +
            "        <ns3:arbetsplatskod>\n" +
            "          <ns5:root>1.2.752.29.4.71</ns5:root>\n" +
            "          <ns5:extension>1234567890</ns5:extension>\n" +
            "        </ns3:arbetsplatskod>\n" +
            "        <ns3:enhetsnamn>NMT vg3 ve1</ns3:enhetsnamn>\n" +
            "        <ns3:postadress>NMT gata 3</ns3:postadress>§\n" +
            "        <ns3:postnummer>12345</ns3:postnummer>\n" +
            "        <ns3:postort>Testhult</ns3:postort>\n" +
            "        <ns3:telefonnummer>0101112131416</ns3:telefonnummer>\n" +
            "        <ns3:epost>enhet3@webcert.invalid.se</ns3:epost>\n" +
            "        <ns3:vardgivare>\n" +
            "          <ns3:vardgivare-id>\n" +
            "            <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
            "            <ns5:extension>TSTNMT2321000156-102Q</ns5:extension>\n" +
            "          </ns3:vardgivare-id>\n" +
            "          <ns3:vardgivarnamn>NMT vg3</ns3:vardgivarnamn>\n" +
            "        </ns3:vardgivare>\n" +
            "      </ns3:enhet>\n" +
            "    </ns3:skapadAv>\n" +
            "    <ns3:svar id=\"2\">\n" +
            "      <ns3:delsvar id=\"2.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>IDK1</ns5:code>\n" +
            "          <ns5:codeSystem>e7cc8f30-a353-4c42-b17a-a189b6876647</ns5:codeSystem>\n" +
            "          <ns5:displayName>ID-kort</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"46\">\n" +
            "      <ns3:delsvar id=\"46.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>OLAMPLIGHET</ns5:code>\n" +
            "          <ns5:codeSystem>1.2.752.129.5.1.4</ns5:codeSystem>\n" +
            "          <ns5:displayName>Anmälan om olämplighet</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"47\">\n" +
            "      <ns3:delsvar id=\"47.1\">Riktigt sjuk</ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"48\">\n" +
            "      <ns3:delsvar id=\"48.1\">2019-01-07</ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>1</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR12</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Moped klass I</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>2</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR13</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Lätt motorcykel</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>3</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR14</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Mellanstor motorcykel</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>4</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR15</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Motorcykel</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>5</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR16</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Personbil och lätt lastbil</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>6</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR17</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Personbil, lätt lastbil och ett eller flera släpfordon</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>7</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR18</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Traktor</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>8</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR1</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Medeltung lastbil och enbart ett lätt släpfordon</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>9</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR2</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Medeltung lastbil och ett eller flera släpfordon oavsett vikt</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>10</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR3</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Tung lastbil och enbart ett lätt släpfordon</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>11</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR4</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Tung lastbil och ett eller flera släpfordon oavsett vikt</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>12</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR5</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Mellanstor buss</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>13</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR6</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Mellanstor buss och ett eller flera släpfordon oavsett vikt</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>14</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR7</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Buss</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>15</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR8</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Buss och enbart ett lätt släpfordon</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"1\">\n" +
            "      <ns3:instans>16</ns3:instans>\n" +
            "      <ns3:delsvar id=\"1.1\">\n" +
            "        <ns5:cv>\n" +
            "          <ns5:code>VAR9</ns5:code>\n" +
            "          <ns5:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns5:codeSystem>\n" +
            "          <ns5:displayName>Taxiförarlegitimation</ns5:displayName>\n" +
            "        </ns5:cv>\n" +
            "      </ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "    <ns3:svar id=\"49\">\n" +
            "      <ns3:delsvar id=\"49.1\">true</ns3:delsvar>\n" +
            "    </ns3:svar>\n" +
            "  </ns4:intyg>\n" +
            "</ns4:RegisterCertificate>\n";

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
        LocalDate signeringsdatum = LocalDate.of(2019, 1, 16);

        assertEquals("191212121212", dto.getPatientid());
        assertEquals(IntygType.parseString("TSTRK1009"), dto.getIntygtyp());
        assertEquals("TSTNMT2321000156-1077", dto.getEnhet());
        assertEquals(106, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
        assertNull(dto.getDiagnoskod());
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
