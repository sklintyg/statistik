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
import org.junit.Test;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;
import se.riv.clinicalprocess.healthcond.certificate.registerCertificate.v3.RegisterCertificateType;

public class Af00251RegisterCertificateHelperTest {

    private static final String xmlIntyg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ns4:RegisterCertificate xmlns:ns6=\"urn:riv:clinicalprocess:healthcond:certificate:3.2\"\n" +
        "                         xmlns:ns5=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"\n" +
        "                         xmlns:dsf=\"http://www.w3.org/2002/06/xmldsig-filter2\"\n" +
        "                         xmlns:ns4=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\"\n" +
        "                         xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
        "                         xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:3\">\n" +
        "    <ns4:intyg>\n" +
        "        <ns3:intygs-id>\n" +
        "            <ns5:root>TSTNMT2321000156-1077</ns5:root>\n" +
        "            <ns5:extension>bdc70c9e-23b7-4d7a-9e6a-7f9b04c7cd27</ns5:extension>\n" +
        "        </ns3:intygs-id>\n" +
        "        <ns3:typ>\n" +
        "            <ns5:code>AF00251</ns5:code>\n" +
        "            <ns5:codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</ns5:codeSystem>\n" +
        "            <ns5:displayName>Läkarintyg för deltagare i arbetsmarknadspolitiska program</ns5:displayName>\n" +
        "        </ns3:typ>\n" +
        "        <ns3:version>1.0</ns3:version>\n" +
        "        <ns3:signeringstidpunkt>2018-07-06T15:49:09</ns3:signeringstidpunkt>\n" +
        "        <ns3:skickatTidpunkt>2018-07-06T15:49:09</ns3:skickatTidpunkt>\n" +
        "        <ns3:patient>\n" +
        "            <ns3:person-id>\n" +
        "                <ns5:root>1.2.752.129.2.1.3.1</ns5:root>\n" +
        "                <ns5:extension>191212121212</ns5:extension>\n" +
        "            </ns3:person-id>\n" +
        "            <ns3:fornamn></ns3:fornamn>\n" +
        "            <ns3:efternamn></ns3:efternamn>\n" +
        "            <ns3:postadress></ns3:postadress>\n" +
        "            <ns3:postnummer></ns3:postnummer>\n" +
        "            <ns3:postort></ns3:postort>\n" +
        "        </ns3:patient>\n" +
        "        <ns3:skapadAv>\n" +
        "            <ns3:personal-id>\n" +
        "                <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
        "                <ns5:extension>TSTNMT2321000156-1079</ns5:extension>\n" +
        "            </ns3:personal-id>\n" +
        "            <ns3:fullstandigtNamn>Arnold Johansson</ns3:fullstandigtNamn>\n" +
        "            <ns3:forskrivarkod>0000000</ns3:forskrivarkod>\n" +
        "            <ns3:enhet>\n" +
        "                <ns3:enhets-id>\n" +
        "                    <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
        "                    <ns5:extension>TSTNMT2321000156-1077</ns5:extension>\n" +
        "                </ns3:enhets-id>\n" +
        "                <ns3:arbetsplatskod>\n" +
        "                    <ns5:root>1.2.752.29.4.71</ns5:root>\n" +
        "                    <ns5:extension>1234567890</ns5:extension>\n" +
        "                </ns3:arbetsplatskod>\n" +
        "                <ns3:enhetsnamn>NMT vg3 ve1</ns3:enhetsnamn>\n" +
        "                <ns3:postadress>NMT gata 3</ns3:postadress>\n" +
        "                <ns3:postnummer>12345</ns3:postnummer>\n" +
        "                <ns3:postort>Testhult</ns3:postort>\n" +
        "                <ns3:telefonnummer>0101112131416</ns3:telefonnummer>\n" +
        "                <ns3:epost>enhet3@webcert.invalid.se</ns3:epost>\n" +
        "                <ns3:vardgivare>\n" +
        "                    <ns3:vardgivare-id>\n" +
        "                        <ns5:root>1.2.752.129.2.1.4.1</ns5:root>\n" +
        "                        <ns5:extension>TSTNMT2321000156-102Q</ns5:extension>\n" +
        "                    </ns3:vardgivare-id>\n" +
        "                    <ns3:vardgivarnamn>NMT vg3</ns3:vardgivarnamn>\n" +
        "                </ns3:vardgivare>\n" +
        "            </ns3:enhet>\n" +
        "        </ns3:skapadAv>\n" +
        "        <ns3:svar id=\"1\">\n" +
        "            <ns3:instans>1</ns3:instans>\n" +
        "            <ns3:delsvar id=\"1.1\">\n" +
        "                <ns5:cv>\n" +
        "                    <ns5:code>ANNAT</ns5:code>\n" +
        "                    <ns5:codeSystem>FKMU_0001</ns5:codeSystem>\n" +
        "                    <ns5:displayName>Annat</ns5:displayName>\n" +
        "                </ns5:cv>\n" +
        "            </ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"1.2\">2018-10-31</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"1.3\">Vi fikade tillsammans, han bjöd på kaffet!</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"1\">\n" +
        "            <ns3:instans>2</ns3:instans>\n" +
        "            <ns3:delsvar id=\"1.1\">\n" +
        "                <ns5:cv>\n" +
        "                    <ns5:code>UNDERSOKNING</ns5:code>\n" +
        "                    <ns5:codeSystem>FKMU_0001</ns5:codeSystem>\n" +
        "                    <ns5:displayName>Min undersokning av patienten</ns5:displayName>\n" +
        "                </ns5:cv>\n" +
        "            </ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"1.2\">2018-10-30</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"2\">\n" +
        "            <ns3:delsvar id=\"2.1\">Kan nästan jobba.</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"2.2\">\n" +
        "                <ns5:cv>\n" +
        "                    <ns5:code>DELTID</ns5:code>\n" +
        "                    <ns5:codeSystem>1.2.752.129.5.1.2</ns5:codeSystem>\n" +
        "                    <ns5:displayName>Deltid</ns5:displayName>\n" +
        "                </ns5:cv>\n" +
        "            </ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"2.3\">22</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"3\">\n" +
        "            <ns3:delsvar id=\"3.1\">Ont i armen.</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"4\">\n" +
        "            <ns3:delsvar id=\"4.1\">Använd bara höger arm.</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"5\">\n" +
        "            <ns3:delsvar id=\"5.1\">true</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"6\">\n" +
        "            <ns3:instans>1</ns3:instans>\n" +
        "            <ns3:delsvar id=\"6.1\">44</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"6.2\">\n" +
        "                <ns5:datePeriod>\n" +
        "                    <ns5:start>2018-09-01</ns5:start>\n" +
        "                    <ns5:end>2018-09-30</ns5:end>\n" +
        "                </ns5:datePeriod>\n" +
        "            </ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"6\">\n" +
        "            <ns3:instans>2</ns3:instans>\n" +
        "            <ns3:delsvar id=\"6.1\">33</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"6.2\">\n" +
        "                <ns5:datePeriod>\n" +
        "                    <ns5:start>2018-10-01</ns5:start>\n" +
        "                    <ns5:end>2018-10-31</ns5:end>\n" +
        "                </ns5:datePeriod>\n" +
        "            </ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"6\">\n" +
        "            <ns3:instans>3</ns3:instans>\n" +
        "            <ns3:delsvar id=\"6.1\">22</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"6.2\">\n" +
        "                <ns5:datePeriod>\n" +
        "                    <ns5:start>2018-11-01</ns5:start>\n" +
        "                    <ns5:end>2018-11-30</ns5:end>\n" +
        "                </ns5:datePeriod>\n" +
        "            </ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"6\">\n" +
        "            <ns3:instans>4</ns3:instans>\n" +
        "            <ns3:delsvar id=\"6.1\">11</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"6.2\">\n" +
        "                <ns5:datePeriod>\n" +
        "                    <ns5:start>2018-12-01</ns5:start>\n" +
        "                    <ns5:end>2018-12-31</ns5:end>\n" +
        "                </ns5:datePeriod>\n" +
        "            </ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"7\">\n" +
        "            <ns3:delsvar id=\"7.1\">true</ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"7.2\">Många fikapauser.</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns3:svar id=\"8\">\n" +
        "            <ns3:delsvar id=\"8.1\">\n" +
        "                <ns5:cv>\n" +
        "                    <ns5:code>ATERGA_MED_ANPASSNING</ns5:code>\n" +
        "                    <ns5:codeSystem>1.2.752.129.5.1.3</ns5:codeSystem>\n" +
        "                    <ns5:displayName>Patienten kan återgå med anpassning</ns5:displayName>\n" +
        "                </ns5:cv>\n" +
        "            </ns3:delsvar>\n" +
        "            <ns3:delsvar id=\"8.2\">Behöver assistent.</ns3:delsvar>\n" +
        "        </ns3:svar>\n" +
        "        <ns6:underskrift>\n" +
        "            <ds:Signature>\n" +
        "                <ds:SignedInfo>\n" +
        "                    <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
        "                    <ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n" +
        "                    <ds:Reference URI=\"\">\n" +
        "                        <ds:Transforms>\n" +
        "                            <ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n" +
        "                            <ds:Transform Algorithm=\"http://www.w3.org/TR/1999/REC-xslt-19991116\">\n" +
        "                                <xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" +
        "                                >\n" +
        "                                    <xsl:output indent=\"no\" omit-xml-declaration=\"yes\"/>\n" +
        "                                    <xsl:strip-space elements=\"*\"/>\n" +
        "                                    <xsl:param name=\"removeElementsNamed\"\n" +
        "                                               select=\"'|skickatTidpunkt|relation|status|underskrift|'\"/>\n" +
        "                                    <xsl:template match=\"*\">\n" +
        "                                        <xsl:choose>\n" +
        "                                            <xsl:when\n" +
        "                                                    test=\"contains($removeElementsNamed,concat('|',local-name(),'|' ))\"/>\n" +
        "                                            <xsl:otherwise>\n" +
        "                                                <xsl:element name=\"{local-name(.)}\">\n" +
        "                                                    <xsl:apply-templates select=\"node()|@*\"/>\n" +
        "                                                </xsl:element>\n" +
        "                                            </xsl:otherwise>\n" +
        "                                        </xsl:choose>\n" +
        "                                    </xsl:template>\n" +
        "                                    <xsl:template match=\"@*\">\n" +
        "                                        <xsl:copy/>\n" +
        "                                    </xsl:template>\n" +
        "                                </xsl:stylesheet>\n" +
        "                            </ds:Transform>\n" +
        "                            <ds:Transform Algorithm=\"http://www.w3.org/2002/06/xmldsig-filter2\">\n" +
        "                                <dsf:XPath Filter=\"intersect\">\n" +
        "                                    //extension[text()='bdc70c9e-23b7-4d7a-9e6a-7f9b04c7cd27']/../..\n" +
        "                                </dsf:XPath>\n" +
        "                            </ds:Transform>\n" +
        "                            <ds:Transform Algorithm=\"http://www.w3.org/2002/06/xmldsig-filter2\">\n" +
        "                                <dsf:XPath Filter=\"subtract\">//*[local-name() = 'skickatTidpunkt']|//*[local-name() =\n" +
        "                                    'relation']|//*[local-name() = 'status']|//*[local-name() = 'underskrift']\n" +
        "                                </dsf:XPath>\n" +
        "                            </ds:Transform>\n" +
        "                            <ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
        "                        </ds:Transforms>\n" +
        "                        <ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n" +
        "                        <ds:DigestValue>H8BEFCMmYDI+aCir1BxgXYGMNNuqakRk7GgmHaEnbjY=</ds:DigestValue>\n" +
        "                    </ds:Reference>\n" +
        "                </ds:SignedInfo>\n" +
        "                <ds:SignatureValue>\n" +
        "                    HuuYFFiZp1UrPWMf3T/SA+tyDXRLgcV97QxSBcTe0SENHxZR6zBh1S3+//Y/U2MUkyCo3yRaThvAxvX3MVeC/eHlWdNl0yo2M7kk0qnXapJbnoVaeDJnvLEM9JywRNH0laNhzM7+DRVLA+J7HHF+CPQWF9OuK8otdLryvknYtwY=\n"
        +
        "                </ds:SignatureValue>\n" +
        "                <ds:KeyInfo>\n" +
        "                    <ds:X509Data>\n" +
        "                        <ds:X509Certificate>\n" +
        "                            MIIB+zCCAWQCCQCUxqAHHrhg+jANBgkqhkiG9w0BAQsFADBCMQswCQYDVQQGEwJTRTELMAkGA1UECAwCVkcxEzARBgNVBAcMCkdvdGhlbmJ1cmcxETAPBgNVBAoMCENhbGxpc3RhMB4XDTE4MDMxMDIwMDY0MFoXDTIxMTIwNDIwMDY0MFowQjELMAkGA1UEBhMCU0UxCzAJBgNVBAgMAlZHMRMwEQYDVQQHDApHb3RoZW5idXJnMREwDwYDVQQKDAhDYWxsaXN0YTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA4cB6VC0f9ne0UKC/XzsoP5ocv7WyGt5378f/DGnVAF3aWzderzLnXMqSdGbLOuEzUUdbjYgQkqQSs6wy872KLf0RzQzllxwpBQJ/2r+CrW6tROJa0FYEIhgWDdRGlS+9+hd3E9Ilz2PTZDF4c1C+4l/xq149OCgiAGfadeBZA5MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQDU+Mrw98Qm8K0U8A208Ee01PZeIpqC9CIRIXJd0PFwXJjTlGIWckwrdsgbGtwOAlA2rzAx/FUhQD4/1F4G5mo/DrtOzzx9fKE0+MQreTC/HOm61ja3cWm4yI5G0W7bLTBBhsEoOzclycNK/QjeP+wYO+k11mtPM4SP4kCj3gh97g==\n"
        +
        "                        </ds:X509Certificate>\n" +
        "                    </ds:X509Data>\n" +
        "                </ds:KeyInfo>\n" +
        "            </ds:Signature>\n" +
        "        </ns6:underskrift>\n" +
        "    </ns4:intyg>\n" +
        "</ns4:RegisterCertificate>\n";

    private RegisterCertificateHelper registerCertificateHelper = new RegisterCertificateHelper();


    @Test
    public void testConvertToDTO() throws JAXBException {
        RegisterCertificateType intyg = registerCertificateHelper.unmarshalXml(xmlIntyg);

        IntygDTO dto = registerCertificateHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2018, 7, 6);

        assertEquals("191212121212", dto.getPatientid());
        assertEquals(IntygType.parseString("AF00251"), dto.getIntygtyp());
        assertEquals("TSTNMT2321000156-1077", dto.getEnhet());
        assertEquals(105, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
        assertNull(dto.getDiagnoskod());
    }

    @Test
    public void unmarshalRegisterCertificateXmlHandlesConcurrentCalls() {
        IntStream.range(1, 25).parallel().forEach(value -> {
            try {
                testConvertToDTO();
            } catch (Exception e) {
                fail("Unexpected exception: " + e.toString());
            }
        });
    }
}
