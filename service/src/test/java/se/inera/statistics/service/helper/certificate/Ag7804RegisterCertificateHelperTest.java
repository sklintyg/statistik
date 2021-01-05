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

public class Ag7804RegisterCertificateHelperTest {

    private static final String xmlIntyg = "<?xml version=\"1.0\"?>\n" +
        "<ns2:RegisterCertificate xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\" xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">\n"
        +
        "  <ns2:intyg>\n" +
        "    <intygs-id>\n" +
        "      <ns3:root>IntygsId</ns3:root>\n" +
        "      <ns3:extension>9020fbb9-e387-40b0-ag7801-001</ns3:extension>\n" +
        "    </intygs-id>\n" +
        "    <typ>\n" +
        "      <ns3:code>AG7804</ns3:code>\n" +
        "      <ns3:codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem>\n" +
        "      <ns3:displayName>Läkarintyg om arbetsförmåga – arbetsgivare</ns3:displayName>\n" +
        "    </typ>\n" +
        "    <version>1.0</version>\n" +
        "    <signeringstidpunkt>2015-12-07T15:48:05</signeringstidpunkt>\n" +
        "    <skickatTidpunkt>2015-12-07T15:48:05</skickatTidpunkt>\n" +
        "    <patient>\n" +
        "      <person-id>\n" +
        "        <ns3:root>1.2.752.129.2.1.3.1</ns3:root>\n" +
        "        <ns3:extension>191212121212</ns3:extension>\n" +
        "      </person-id>\n" +
        "      <fornamn>Tolvan</fornamn>\n" +
        "      <efternamn>Tolvansson</efternamn>\n" +
        "      <postadress>Svensson, Storgatan 1, PL 1234</postadress>\n" +
        "      <postnummer>12345</postnummer>\n" +
        "      <postort>Sm&#xE5;m&#xE5;la</postort>\n" +
        "    </patient>\n" +
        "    <skapadAv>\n" +
        "      <personal-id>\n" +
        "        <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "        <ns3:extension>TSTNMT2321000156-1079</ns3:extension>\n" +
        "      </personal-id>\n" +
        "      <fullstandigtNamn>Arnold Johansson</fullstandigtNamn>\n" +
        "      <forskrivarkod>0000000</forskrivarkod>\n" +
        "      <befattning>\n" +
        "        <ns3:code>203090</ns3:code>\n" +
        "        <ns3:codeSystem>1.2.752.129.2.2.1.4</ns3:codeSystem>\n" +
        "        <ns3:displayName>L&#xE4;kare legitimerad, annan</ns3:displayName>\n" +
        "      </befattning>\n" +
        "      <enhet>\n" +
        "        <enhets-id>\n" +
        "          <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "          <ns3:extension>Enhetsid</ns3:extension>\n" +
        "        </enhets-id>\n" +
        "        <arbetsplatskod>\n" +
        "          <ns3:root>1.2.752.29.4.71</ns3:root>\n" +
        "          <ns3:extension>1234567890</ns3:extension>\n" +
        "        </arbetsplatskod>\n" +
        "        <enhetsnamn>NMT vg3 ve1</enhetsnamn>\n" +
        "        <postadress>NMT gata 2</postadress>\n" +
        "        <postnummer>12345</postnummer>\n" +
        "        <postort>Testhult</postort>\n" +
        "        <telefonnummer>0101112131415</telefonnummer>\n" +
        "        <epost>enhet2@webcert.invalid.se</epost>\n" +
        "        <vardgivare>\n" +
        "          <vardgivare-id>\n" +
        "            <ns3:root>1.2.752.129.2.1.4.1</ns3:root>\n" +
        "            <ns3:extension>TSTNMT2321000156-102Q</ns3:extension>\n" +
        "          </vardgivare-id>\n" +
        "          <vardgivarnamn>NMT vg3</vardgivarnamn>\n" +
        "        </vardgivare>\n" +
        "      </enhet>\n" +
        "    </skapadAv>\n" +
        "    <svar id=\"1\">\n" +
        "      <instans>1</instans>\n" +
        "      <delsvar id=\"1.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>UNDERSOKNING</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
        "          <ns3:displayName>Min unders&#xF6;kning av patienten</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"1.2\">2016-08-01</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"1\">\n" +
        "      <instans>2</instans>\n" +
        "      <delsvar id=\"1.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>TELEFONKONTAKT</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
        "          <ns3:displayName>Min telefonkontakt med patienten</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"1.2\">2016-08-02</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"1\">\n" +
        "      <instans>3</instans>\n" +
        "      <delsvar id=\"1.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>JOURNALUPPGIFTER</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
        "          <ns3:displayName>Journaluppgifter fr&#xE5;n den</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"1.2\">2016-08-03</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"1\">\n" +
        "      <instans>4</instans>\n" +
        "      <delsvar id=\"1.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>ANNAT</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
        "          <ns3:displayName>Annat</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"1.2\">2016-08-04</delsvar>\n" +
        "      <delsvar id=\"1.3\">Telepatisk kommunikation</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"28\">\n" +
        "      <instans>1</instans>\n" +
        "      <delsvar id=\"28.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>NUVARANDE_ARBETE</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0002</ns3:codeSystem>\n" +
        "          <ns3:displayName>Nuvarande arbete</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"29\">\n" +
        "      <delsvar id=\"29.1\">Siare</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"100\">\n" +
        "      <delsvar id=\"100.1\">true</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"6\">\n" +
        "      <delsvar id=\"6.1\">J&#xE4;rnbristanemi</delsvar>\n" +
        "      <delsvar id=\"6.2\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>D50</ns3:code>\n" +
        "          <ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"6.3\">Huntingtons sjukdom</delsvar>\n" +
        "      <delsvar id=\"6.4\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>G10</ns3:code>\n" +
        "          <ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"6.5\">Br&#xE4;nnskada av f&#xF6;rsta graden p&#xE5; h&#xF6;ft och nedre extremitet utom fotled och fot</delsvar>\n"
        +
        "      <delsvar id=\"6.6\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>T241</ns3:code>\n" +
        "          <ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"101\">\n" +
        "      <delsvar id=\"101.1\">true</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"35\">\n" +
        "      <delsvar id=\"35.1\">Inga fynd gjordes</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"17\">\n" +
        "      <delsvar id=\"17.1\">Har sv&#xE5;rt att sitta och ligga.. Och st&#xE5;. F&#xE5;r huka sig.</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"19\">\n" +
        "      <delsvar id=\"19.1\">Meditering, sj&#xE4;lvmedicinering</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"20\">\n" +
        "      <delsvar id=\"20.1\">Inga planerade &#xE5;tg&#xE4;rder. Patienten har ingen almanacka.</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"32\">\n" +
        "      <instans>1</instans>\n" +
        "      <delsvar id=\"32.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>HELT_NEDSATT</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
        "          <ns3:displayName>100%</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"32.2\">\n" +
        "        <ns3:datePeriod>\n" +
        "          <ns3:start>2016-08-08</ns3:start>\n" +
        "          <ns3:end>2016-08-22</ns3:end>\n" +
        "        </ns3:datePeriod>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"32\">\n" +
        "      <instans>2</instans>\n" +
        "      <delsvar id=\"32.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>TRE_FJARDEDEL</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
        "          <ns3:displayName>75%</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"32.2\">\n" +
        "        <ns3:datePeriod>\n" +
        "          <ns3:start>2016-08-23</ns3:start>\n" +
        "          <ns3:end>2016-08-24</ns3:end>\n" +
        "        </ns3:datePeriod>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"32\">\n" +
        "      <instans>3</instans>\n" +
        "      <delsvar id=\"32.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>HALFTEN</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
        "          <ns3:displayName>50%</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"32.2\">\n" +
        "        <ns3:datePeriod>\n" +
        "          <ns3:start>2016-08-25</ns3:start>\n" +
        "          <ns3:end>2016-08-27</ns3:end>\n" +
        "        </ns3:datePeriod>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"32\">\n" +
        "      <instans>4</instans>\n" +
        "      <delsvar id=\"32.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>EN_FJARDEDEL</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
        "          <ns3:displayName>25%</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"32.2\">\n" +
        "        <ns3:datePeriod>\n" +
        "          <ns3:start>2016-08-29</ns3:start>\n" +
        "          <ns3:end>2016-11-26</ns3:end>\n" +
        "        </ns3:datePeriod>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"37\">\n" +
        "      <delsvar id=\"37.1\">Har f&#xF6;ljt beslutst&#xF6;det till punkt och pricka.</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"33\">\n" +
        "      <delsvar id=\"33.1\">true</delsvar>\n" +
        "      <delsvar id=\"33.2\">Har bra och d&#xE5;liga dagar. B&#xE4;ttre att jobba 22h-24h de bra dagarna s&#xE5; patienten kan vila sedan.</delsvar>\n"
        +
        "    </svar>\n" +
        "    <svar id=\"34\">\n" +
        "      <delsvar id=\"34.1\">true</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"39\">\n" +
        "      <delsvar id=\"39.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>ATER_X_ANTAL_DGR</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0006</ns3:codeSystem>\n" +
        "          <ns3:displayName>Patienten kommer med stor sannolikhet att &#xE5;terg&#xE5; helt i nuvarande syssels&#xE4;ttning efter x antal dagar</ns3:displayName>\n"
        +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "      <delsvar id=\"39.3\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>SEXTIO_DGR</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0007</ns3:codeSystem>\n" +
        "          <ns3:displayName>60 dagar</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>1</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>ARBETSTRANING</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Arbetstr&#xE4;ning</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>2</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>ARBETSANPASSNING</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Arbetsanpassning</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>3</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>BESOK_ARBETSPLATS</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Bes&#xF6;k p&#xE5; arbetsplatsen</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>4</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>ERGONOMISK</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Ergonomisk bed&#xF6;mning</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>5</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>HJALPMEDEL</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Hj&#xE4;lpmedel</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>6</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>KONTAKT_FHV</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Kontakt med f&#xF6;retagsh&#xE4;lsov&#xE5;rd</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>7</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>OMFORDELNING</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>Omf&#xF6;rdelning av arbetsuppgifter</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"40\">\n" +
        "      <instans>8</instans>\n" +
        "      <delsvar id=\"40.1\">\n" +
        "        <ns3:cv>\n" +
        "          <ns3:code>OVRIGA_ATGARDER</ns3:code>\n" +
        "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
        "          <ns3:displayName>&#xD6;vrigt</ns3:displayName>\n" +
        "        </ns3:cv>\n" +
        "      </delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"44\">\n" +
        "      <delsvar id=\"44.1\">D&#xE4;rf&#xF6;r.</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"25\">\n" +
        "      <delsvar id=\"25.1\">Inga &#xF6;vriga upplysningar.</delsvar>\n" +
        "    </svar>\n" +
        "    <svar id=\"103\">\n" +
        "      <delsvar id=\"103.1\">true</delsvar>\n" +
        "      <delsvar id=\"103.2\">Alltid roligt att prata med FK.</delsvar>\n" +
        "    </svar>\n" +
        "  </ns2:intyg>\n" +
        "</ns2:RegisterCertificate>\n";

    private Ag7804RegisterCertificateHelper registerCertificateHelper = new Ag7804RegisterCertificateHelper();


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
        assertEquals(IntygType.parseString("AG7804"), dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(102, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
        assertEquals("D50", dto.getDiagnoskod());
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
