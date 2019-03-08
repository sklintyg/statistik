/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

public class Tstrk1062RegisterCertificateHelperTest {

    private static final String xmlIntyg = "<ns3:RegisterCertificate xmlns:ns5=\"urn:riv:clinicalprocess:healthcond:certificate:3.2\" xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:3\" xmlns:ns4=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\">\n" +
            "  <ns3:intyg>\n" +
            "    <ns2:intygs-id>\n" +
            "      <ns4:root>TSTNMT2321000156-1077</ns4:root>\n" +
            "      <ns4:extension>d74306bc-b91c-40e9-83b5-c2350637a6f6</ns4:extension>\n" +
            "    </ns2:intygs-id>\n" +
            "    <ns2:typ>\n" +
            "      <ns4:code>TSTRK1062</ns4:code>\n" +
            "      <ns4:codeSystem>f6fb361a-e31d-48b8-8657-99b63912dd9b</ns4:codeSystem>\n" +
            "      <ns4:displayName>Transportstyrelsens läkarintyg</ns4:displayName>\n" +
            "    </ns2:typ>\n" +
            "    <ns2:version>1.0</ns2:version>\n" +
            "    <ns2:signeringstidpunkt>2019-02-06T14:34:25</ns2:signeringstidpunkt>\n" +
            "    <ns2:skickatTidpunkt>2019-02-06T14:34:25</ns2:skickatTidpunkt>\n" +
            "    <ns2:patient>\n" +
            "      <ns2:person-id>\n" +
            "        <ns4:root>1.2.752.129.2.1.3.1</ns4:root>\n" +
            "        <ns4:extension>191212121212</ns4:extension>\n" +
            "      </ns2:person-id>\n" +
            "      <ns2:fornamn>Tolvan</ns2:fornamn>\n" +
            "      <ns2:efternamn>Tolvansson</ns2:efternamn>\n" +
            "      <ns2:postadress>Svensson, Storgatan 1, PL 1234</ns2:postadress>\n" +
            "      <ns2:postnummer>12345</ns2:postnummer>\n" +
            "      <ns2:postort>Småmåla</ns2:postort>\n" +
            "    </ns2:patient>\n" +
            "    <ns2:skapadAv>\n" +
            "      <ns2:personal-id>\n" +
            "        <ns4:root>1.2.752.129.2.1.4.1</ns4:root>\n" +
            "        <ns4:extension>TSTNMT2321000156-1079</ns4:extension>\n" +
            "      </ns2:personal-id>\n" +
            "      <ns2:fullstandigtNamn>Arnold Johansson</ns2:fullstandigtNamn>\n" +
            "      <ns2:forskrivarkod>0000000</ns2:forskrivarkod>\n" +
            "      <ns2:enhet>\n" +
            "        <ns2:enhets-id>\n" +
            "          <ns4:root>1.2.752.129.2.1.4.1</ns4:root>\n" +
            "          <ns4:extension>TSTNMT2321000156-1077</ns4:extension>\n" +
            "        </ns2:enhets-id>\n" +
            "        <ns2:arbetsplatskod>\n" +
            "          <ns4:root>1.2.752.29.4.71</ns4:root>\n" +
            "          <ns4:extension>1234567890</ns4:extension>\n" +
            "        </ns2:arbetsplatskod>\n" +
            "        <ns2:enhetsnamn>NMT vg3 ve1</ns2:enhetsnamn>\n" +
            "        <ns2:postadress>NMT gata 3</ns2:postadress>\n" +
            "        <ns2:postnummer>12345</ns2:postnummer>\n" +
            "        <ns2:postort>Testhult</ns2:postort>\n" +
            "        <ns2:telefonnummer>0101112131416</ns2:telefonnummer>\n" +
            "        <ns2:epost>enhet3@webcert.invalid.se</ns2:epost>\n" +
            "        <ns2:vardgivare>\n" +
            "          <ns2:vardgivare-id>\n" +
            "            <ns4:root>1.2.752.129.2.1.4.1</ns4:root>\n" +
            "            <ns4:extension>TSTNMT2321000156-102Q</ns4:extension>\n" +
            "          </ns2:vardgivare-id>\n" +
            "          <ns2:vardgivarnamn>NMT vg3</ns2:vardgivarnamn>\n" +
            "        </ns2:vardgivare>\n" +
            "      </ns2:enhet>\n" +
            "    </ns2:skapadAv>\n" +
            "    <ns2:relation>\n" +
            "      <ns2:typ>\n" +
            "        <ns4:code>ERSATT</ns4:code>\n" +
            "        <ns4:codeSystem>c2362fcd-eda0-4f9a-bd13-b3bbaf7f2146</ns4:codeSystem>\n" +
            "        <ns4:displayName>Ersätter</ns4:displayName>\n" +
            "      </ns2:typ>\n" +
            "      <ns2:intygs-id>\n" +
            "        <ns4:root>TSTNMT2321000156-1077</ns4:root>\n" +
            "        <ns4:extension>5969f242-8d84-46c7-8ad1-bc2c4cacfda1</ns4:extension>\n" +
            "      </ns2:intygs-id>\n" +
            "    </ns2:relation>\n" +
            "    <ns2:svar id=\"1\">\n" +
            "      <ns2:instans>1</ns2:instans>\n" +
            "      <ns2:delsvar id=\"1.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>IAV11</ns4:code>\n" +
            "          <ns4:codeSystem>24c41b8d-258a-46bf-a08a-b90738b28770</ns4:codeSystem>\n" +
            "          <ns4:displayName>AM</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"1\">\n" +
            "      <ns2:instans>2</ns2:instans>\n" +
            "      <ns2:delsvar id=\"1.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>IAV12</ns4:code>\n" +
            "          <ns4:codeSystem>24c41b8d-258a-46bf-a08a-b90738b28770</ns4:codeSystem>\n" +
            "          <ns4:displayName>A1</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"1\">\n" +
            "      <ns2:instans>3</ns2:instans>\n" +
            "      <ns2:delsvar id=\"1.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>IAV13</ns4:code>\n" +
            "          <ns4:codeSystem>24c41b8d-258a-46bf-a08a-b90738b28770</ns4:codeSystem>\n" +
            "          <ns4:displayName>A2</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"2\">\n" +
            "      <ns2:instans>4</ns2:instans>\n" +
            "      <ns2:delsvar id=\"2.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>IDK1</ns4:code>\n" +
            "          <ns4:codeSystem>e7cc8f30-a353-4c42-b17a-a189b6876647</ns4:codeSystem>\n" +
            "          <ns4:displayName>ID-kort</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"51\">\n" +
            "      <ns2:instans>1</ns2:instans>\n" +
            "      <ns2:delsvar id=\"51.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>A01</ns4:code>\n" +
            "          <ns4:codeSystem>1.2.752.116.1.1.1.1.3</ns4:codeSystem>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.2\">Tyfoidfeber och paratyfoidfeber</ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.3\">\n" +
            "        <ns4:partialDate>\n" +
            "          <ns4:format>YYYY</ns4:format>\n" +
            "          <ns4:value>2018</ns4:value>\n" +
            "        </ns4:partialDate>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"51\">\n" +
            "      <ns2:instans>2</ns2:instans>\n" +
            "      <ns2:delsvar id=\"51.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>B02</ns4:code>\n" +
            "          <ns4:codeSystem>1.2.752.116.1.1.1.1.3</ns4:codeSystem>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.2\">Bältros</ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.3\">\n" +
            "        <ns4:partialDate>\n" +
            "          <ns4:format>YYYY</ns4:format>\n" +
            "          <ns4:value>2017</ns4:value>\n" +
            "        </ns4:partialDate>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"51\">\n" +
            "      <ns2:instans>3</ns2:instans>\n" +
            "      <ns2:delsvar id=\"51.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>C03</ns4:code>\n" +
            "          <ns4:codeSystem>1.2.752.116.1.1.1.1.3</ns4:codeSystem>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.2\">Malign tumör i tandköttet</ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.3\">\n" +
            "        <ns4:partialDate>\n" +
            "          <ns4:format>YYYY</ns4:format>\n" +
            "          <ns4:value>2018</ns4:value>\n" +
            "        </ns4:partialDate>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"51\">\n" +
            "      <ns2:instans>4</ns2:instans>\n" +
            "      <ns2:delsvar id=\"51.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>D04</ns4:code>\n" +
            "          <ns4:codeSystem>1.2.752.116.1.1.1.1.3</ns4:codeSystem>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.2\">Cancer in situ i huden</ns2:delsvar>\n" +
            "      <ns2:delsvar id=\"51.3\">\n" +
            "        <ns4:partialDate>\n" +
            "          <ns4:format>YYYY</ns4:format>\n" +
            "          <ns4:value>2011</ns4:value>\n" +
            "        </ns4:partialDate>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"53\">\n" +
            "      <ns2:delsvar id=\"53.1\">true</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"54\">\n" +
            "      <ns2:delsvar id=\"54.1\">true</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"55\">\n" +
            "      <ns2:delsvar id=\"55.1\">Läkemedel a, b och c</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"56\">\n" +
            "      <ns2:delsvar id=\"56.1\">true</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"57\">\n" +
            "      <ns2:delsvar id=\"57.1\">false</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"58\">\n" +
            "      <ns2:delsvar id=\"58.1\">true</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"60\">\n" +
            "      <ns2:delsvar id=\"60.1\">Bedömning av aktuella symptom</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"61\">\n" +
            "      <ns2:delsvar id=\"61.1\">true</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"32\">\n" +
            "      <ns2:delsvar id=\"32.1\">Inga övriga kommentarer</ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"33\">\n" +
            "      <ns2:instans>1</ns2:instans>\n" +
            "      <ns2:delsvar id=\"33.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>VAR12</ns4:code>\n" +
            "          <ns4:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns4:codeSystem>\n" +
            "          <ns4:displayName>AM</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"33\">\n" +
            "      <ns2:instans>2</ns2:instans>\n" +
            "      <ns2:delsvar id=\"33.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>VAR13</ns4:code>\n" +
            "          <ns4:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns4:codeSystem>\n" +
            "          <ns4:displayName>A1</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "    <ns2:svar id=\"33\">\n" +
            "      <ns2:instans>3</ns2:instans>\n" +
            "      <ns2:delsvar id=\"33.1\">\n" +
            "        <ns4:cv>\n" +
            "          <ns4:code>VAR14</ns4:code>\n" +
            "          <ns4:codeSystem>e889fa20-1dee-4f79-8b37-03853e75a9f8</ns4:codeSystem>\n" +
            "          <ns4:displayName>A2</ns4:displayName>\n" +
            "        </ns4:cv>\n" +
            "      </ns2:delsvar>\n" +
            "    </ns2:svar>\n" +
            "  </ns3:intyg>\n" +
            "</ns3:RegisterCertificate>";

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
        LocalDate signeringsdatum = LocalDate.of(2019, 2, 6);

        assertEquals("191212121212", dto.getPatientid());
        assertEquals(IntygType.parseString("TSTRK1062"), dto.getIntygtyp());
        assertEquals("TSTNMT2321000156-1077", dto.getEnhet());
        assertEquals(106, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
        assertNull(dto.getDiagnoskod());
    }

    @Test
    public void unmarshalRegisterCertificateXmlHandlesConcurrentCalls() {
        IntStream.range(1,25).parallel().forEach(value -> {
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
