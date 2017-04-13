package se.inera.statistics.service.schemavalidation;

import org.junit.Test;
import se.inera.statistics.service.helper.RegisterCertificateHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LisjpValidatorTest {

    private final String INTYG_WITH_EMPTY_351 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:RegisterCertificate xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2\">\n" +
            "  <ns2:intyg>\n" +
            "    <intygs-id xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
            "      <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">123456789</ns3:root>\n" +
            "      <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1234567</ns3:extension>\n" +
            "    </intygs-id>\n" +
            "    <typ xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
            "      <ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">LISJP</ns3:code>\n" +
            "      <ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem>\n" +
            "      <ns3:displayName xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Läkarintyg för sjukpenning</ns3:displayName>\n" +
            "    </typ>\n" +
            "    <version xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">1</version>\n" +
            "    <signeringstidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">2017-04-10T15:34:45</signeringstidpunkt>\n" +
            "    <skickatTidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">2015-12-07T15:48:05</skickatTidpunkt>\n" +
            "    <patient xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
            "      <person-id>\n" +
            "        <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.1.3.1</ns3:root>\n" +
            "        <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">19671122-2941</ns3:extension>\n" +
            "      </person-id>\n" +
            "      <fornamn>Olivia</fornamn>\n" +
            "      <efternamn>Olsson</efternamn>\n" +
            "      <postadress>Pgatan 2</postadress>\n" +
            "      <postnummer>100 20</postnummer>\n" +
            "      <postort>Stadby gärde</postort>\n" +
            "    </patient>\n" +
            "    <skapadAv xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
            "      <personal-id>\n" +
            "        <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.1.4.1</ns3:root>\n" +
            "        <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Personal HSA-ID</ns3:extension>\n" +
            "      </personal-id>\n" +
            "      <fullstandigtNamn>Karl Karlsson</fullstandigtNamn>\n" +
            "      <forskrivarkod>09874321</forskrivarkod>\n" +
            "      <befattning>\n" +
            "        <ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Klinikchef</ns3:code>\n" +
            "        <ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.2.1.4</ns3:codeSystem>\n" +
            "      </befattning>\n" +
            "      <befattning>\n" +
            "        <ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Forskningsledare</ns3:code>\n" +
            "        <ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.2.1.4</ns3:codeSystem>\n" +
            "      </befattning>\n" +
            "      <enhet>\n" +
            "        <enhets-id>\n" +
            "          <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.1.4.1</ns3:root>\n" +
            "          <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">enhet1</ns3:extension>\n" +
            "        </enhets-id>\n" +
            "        <arbetsplatskod>\n" +
            "          <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.29.4.71</ns3:root>\n" +
            "          <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">45312</ns3:extension>\n" +
            "        </arbetsplatskod>\n" +
            "        <enhetsnamn>VE1</enhetsnamn>\n" +
            "        <postadress>Enhetsg. 1</postadress>\n" +
            "        <postnummer>100 10</postnummer>\n" +
            "        <postort>Stadby</postort>\n" +
            "        <telefonnummer>0812341234</telefonnummer>\n" +
            "        <epost>ve1@vg1.se</epost>\n" +
            "        <vardgivare>\n" +
            "          <vardgivare-id>\n" +
            "            <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.1.4.1</ns3:root>\n" +
            "            <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">vg1</ns3:extension>\n" +
            "          </vardgivare-id>\n" +
            "          <vardgivarnamn>VG1</vardgivarnamn>\n" +
            "        </vardgivare>\n" +
            "      </enhet>\n" +
            "      <specialistkompetens>\n" +
            "        <ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Kirurg</ns3:code>\n" +
            "      </specialistkompetens>\n" +
            "    </skapadAv>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"1\">\n" +
            "      <instans>1</instans>\n" +
            "      <delsvar id=\"1.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>TELEFONKONTAKT</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "      <delsvar id=\"1.2\">2015-12-08</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"1\">\n" +
            "      <instans>2</instans>\n" +
            "      <delsvar id=\"1.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>ANNAT</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "      <delsvar id=\"1.2\">2015-12-07</delsvar>\n" +
            "      <delsvar id=\"1.3\">Barndomsvän</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"28\">\n" +
            "      <instans>1</instans>\n" +
            "      <delsvar id=\"28.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>NUVARANDE_ARBETE</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0002</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"29\">\n" +
            "      <delsvar id=\"29.1\">Smed</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"6\">\n" +
            "      <delsvar id=\"6.1\">Klämskada skuldra</delsvar>\n" +
            "      <delsvar id=\"6.2\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>A01</ns3:code>\n" +
            "          <ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"35\">\n" +
            "      <delsvar id=\"35.1\"></delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"17\">\n" +
            "      <delsvar id=\"17.1\">Default arbetsbegransning</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"32\">\n" +
            "      <instans>1</instans>\n" +
            "      <delsvar id=\"32.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>HELT_NEDSATT</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
            "          <ns3:displayName>75%</ns3:displayName>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "      <delsvar id=\"32.2\">\n" +
            "        <ns3:datePeriod xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:start>2014-09-01</ns3:start>\n" +
            "          <ns3:end>2014-09-15</ns3:end>\n" +
            "        </ns3:datePeriod>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"37\">\n" +
            "      <delsvar id=\"37.1\">Överskrider inte FMB</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"34\">\n" +
            "      <delsvar id=\"34.1\">true</delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"39\">\n" +
            "      <delsvar id=\"39.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>PROGNOS_OKLAR</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0006</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"40\">\n" +
            "      <instans>1</instans>\n" +
            "      <delsvar id=\"40.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>OVRIGA_ATGARDER</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"40\">\n" +
            "      <instans>2</instans>\n" +
            "      <delsvar id=\"40.1\">\n" +
            "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
            "          <ns3:code>KONFLIKTHANTERING</ns3:code>\n" +
            "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
            "        </ns3:cv>\n" +
            "      </delsvar>\n" +
            "    </svar>\n" +
            "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"44\">\n" +
            "      <delsvar id=\"44.1\">Jobbar bra om man inte stör honom</delsvar>\n" +
            "    </svar>\n" +
            "  </ns2:intyg>\n" +
            "</ns2:RegisterCertificate>\n";

    @Test
    public void testEmptyFunktionsnedsattningsbeskrivningIsInvalidAccordingToSchematron() throws Exception {
        //Given
        final LisjpValidator lisjpValidator = new LisjpValidator();

        //When
        final String xmlContent = RegisterCertificateHelper.convertToV3(INTYG_WITH_EMPTY_351);
        final ValidateXmlResponse validateXmlResponse = lisjpValidator.validateSchematron(xmlContent);

        //Then
        assertFalse(validateXmlResponse.isValid());
        assertEquals(1, validateXmlResponse.getValidationErrors().size());
    }

}
