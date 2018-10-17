/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import se.inera.intygstjanster.ts.services.RegisterTSDiabetesResponder.v1.RegisterTSDiabetesType;
import se.inera.intygstjanster.ts.services.types.v1.II;
import se.inera.intygstjanster.ts.services.v1.GrundData;
import se.inera.intygstjanster.ts.services.v1.Patient;
import se.inera.intygstjanster.ts.services.v1.TSDiabetesIntyg;
import se.inera.statistics.service.helper.ConversionHelper;
import se.inera.statistics.service.helper.Patientdata;
import se.inera.statistics.service.helper.certificate.TsDiabetesHelper;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.warehouse.IntygType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TsDiabetesHelperTest {

    private static final String xmlIntyg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns3:RegisterTSDiabetes xmlns=\"urn:local:se:intygstjanster:services:1\"\n" +
            "    xmlns:ns2=\"urn:local:se:intygstjanster:services:types:1\"\n" +
            "    xmlns:ns3=\"urn:local:se:intygstjanster:services:RegisterTSDiabetesResponder:1\">\n" +
            "  <ns3:intyg>\n" +
            "    <intygsId>987654321</intygsId>\n" +
            "    <intygsTyp>ts-diabetes</intygsTyp>\n" +
            "    <version>02</version>\n" +
            "    <utgava>06</utgava>\n" +
            "    <grundData>\n" +
            "      <signeringsTidstampel>2013-03-17T15:57:00</signeringsTidstampel>\n" +
            "      <skapadAv>\n" +
            "        <personId>\n" +
            "          <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "          <ns2:extension>Lakareid</ns2:extension>\n" +
            "        </personId>\n" +
            "        <fullstandigtNamn>Doktor Thompson</fullstandigtNamn>\n" +
            "        <atLakare>false</atLakare>\n" +
            "        <vardenhet>\n" +
            "          <enhetsId>\n" +
            "            <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "            <ns2:extension>Enhetsid</ns2:extension>\n" +
            "          </enhetsId>\n" +
            "          <enhetsnamn>Vårdenhet Väst</enhetsnamn>\n" +
            "          <postadress>Enhetsvägen 12</postadress>\n" +
            "          <postnummer>54321</postnummer>\n" +
            "          <postort>Tumba</postort>\n" +
            "          <telefonnummer>08-1337</telefonnummer>\n" +
            "          <vardgivare>\n" +
            "            <vardgivarid>\n" +
            "              <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "              <ns2:extension>VardgivarId</ns2:extension>\n" +
            "            </vardgivarid>\n" +
            "            <vardgivarnamn>Vårdgivarnamn</vardgivarnamn>\n" +
            "          </vardgivare>\n" +
            "        </vardenhet>\n" +
            "        <specialiteter>SPECIALITET</specialiteter>\n" +
            "        <befattningar>ST_LAKARE</befattningar>\n" +
            "      </skapadAv>\n" +
            "      <patient>\n" +
            "        <personId>\n" +
            "          <ns2:root>1.2.752.129.2.1.3.1</ns2:root>\n" +
            "          <ns2:extension>19121212-1212</ns2:extension>\n" +
            "        </personId>\n" +
            "        <fullstandigtNamn>Herr Dundersjuk</fullstandigtNamn>\n" +
            "        <fornamn>Herr</fornamn>\n" +
            "        <efternamn>Dundersjuk</efternamn>\n" +
            "        <postadress>Testvägen 12</postadress>\n" +
            "        <postnummer>12345</postnummer>\n" +
            "        <postort>Testort</postort>\n" +
            "      </patient>\n" +
            "    </grundData>\n" +
            "    <intygAvser>\n" +
            "      <korkortstyp>AM</korkortstyp>\n" +
            "      <korkortstyp>A1</korkortstyp>\n" +
            "      <korkortstyp>A2</korkortstyp>\n" +
            "      <korkortstyp>A</korkortstyp>\n" +
            "      <korkortstyp>B</korkortstyp>\n" +
            "      <korkortstyp>BE</korkortstyp>\n" +
            "      <korkortstyp>TRAKTOR</korkortstyp>\n" +
            "      <korkortstyp>C1</korkortstyp>\n" +
            "      <korkortstyp>C1E</korkortstyp>\n" +
            "      <korkortstyp>C</korkortstyp>\n" +
            "      <korkortstyp>CE</korkortstyp>\n" +
            "      <korkortstyp>D1</korkortstyp>\n" +
            "      <korkortstyp>D1E</korkortstyp>\n" +
            "      <korkortstyp>D</korkortstyp>\n" +
            "      <korkortstyp>DE</korkortstyp>\n" +
            "      <korkortstyp>TAXI</korkortstyp>\n" +
            "    </intygAvser>\n" +
            "    <identitetStyrkt>\n" +
            "      <idkontroll>IDK6</idkontroll>\n" +
            "    </identitetStyrkt>\n" +
            "    <diabetes>\n" +
            "      <debutArDiabetes>2012</debutArDiabetes>\n" +
            "      <diabetesTyp>TYP2</diabetesTyp>\n" +
            "      <harBehandlingKost>true</harBehandlingKost>\n" +
            "      <harBehandlingTabletter>true</harBehandlingTabletter>\n" +
            "      <harBehandlingInsulin>true</harBehandlingInsulin>\n" +
            "      <insulinBehandlingSedanAr>2012</insulinBehandlingSedanAr>\n" +
            "      <annanBehandlingBeskrivning>Hypnos</annanBehandlingBeskrivning>\n" +
            "    </diabetes>\n" +
            "    <hypoglykemier>\n" +
            "      <harKunskapOmAtgarder>true</harKunskapOmAtgarder>\n" +
            "      <harTeckenNedsattHjarnfunktion>true</harTeckenNedsattHjarnfunktion>\n" +
            "      <saknarFormagaKannaVarningstecken>true</saknarFormagaKannaVarningstecken>\n" +
            "      <harAllvarligForekomst>true</harAllvarligForekomst>\n" +
            "      <allvarligForekomstBeskrivning>Beskrivning</allvarligForekomstBeskrivning>\n" +
            "      <harAllvarligForekomstTrafiken>true</harAllvarligForekomstTrafiken>\n" +
            "      <allvarligForekomstTrafikBeskrivning>Beskrivning</allvarligForekomstTrafikBeskrivning>\n" +
            "      <genomforEgenkontrollBlodsocker>true</genomforEgenkontrollBlodsocker>\n" +
            "      <harAllvarligForekomstVakenTid>false</harAllvarligForekomstVakenTid>\n" +
            "    </hypoglykemier>\n" +
            "    <separatOgonLakarintygKommerSkickas>false</separatOgonLakarintygKommerSkickas>\n" +
            "    <synfunktion>\n" +
            "      <harSynfaltsdefekt>false</harSynfaltsdefekt>\n" +
            "      <synskarpaUtanKorrektion>\n" +
            "        <hogerOga>0.0</hogerOga>\n" +
            "        <vansterOga>0.0</vansterOga>\n" +
            "        <binokulart>0.0</binokulart>\n" +
            "      </synskarpaUtanKorrektion>\n" +
            "      <synskarpaMedKorrektion>\n" +
            "        <hogerOga>0.0</hogerOga>\n" +
            "        <vansterOga>0.0</vansterOga>\n" +
            "        <binokulart>0.0</binokulart>\n" +
            "      </synskarpaMedKorrektion>\n" +
            "      <harDiplopi>true</harDiplopi>\n" +
            "      <finnsSynfaltsprovning>true</finnsSynfaltsprovning>\n" +
            "      <synfaltsprovningUtanAnmarkning>true</synfaltsprovningUtanAnmarkning>\n" +
            "      <finnsProvningOgatsRorlighet>true</finnsProvningOgatsRorlighet>\n" +
            "    </synfunktion>\n" +
            "    <ovrigKommentar>Kommentarer av det viktiga slaget</ovrigKommentar>\n" +
            "    <bedomning>\n" +
            "      <korkortstyp>AM</korkortstyp>\n" +
            "      <korkortstyp>A1</korkortstyp>\n" +
            "      <korkortstyp>A2</korkortstyp>\n" +
            "      <korkortstyp>A</korkortstyp>\n" +
            "      <korkortstyp>B</korkortstyp>\n" +
            "      <korkortstyp>BE</korkortstyp>\n" +
            "      <korkortstyp>TRAKTOR</korkortstyp>\n" +
            "      <korkortstyp>C1</korkortstyp>\n" +
            "      <korkortstyp>C1E</korkortstyp>\n" +
            "      <korkortstyp>C</korkortstyp>\n" +
            "      <korkortstyp>CE</korkortstyp>\n" +
            "      <korkortstyp>D1</korkortstyp>\n" +
            "      <korkortstyp>D1E</korkortstyp>\n" +
            "      <korkortstyp>D</korkortstyp>\n" +
            "      <korkortstyp>DE</korkortstyp>\n" +
            "      <korkortstyp>TAXI</korkortstyp>\n" +
            "      <lamplighetInnehaBehorighetSpecial>true</lamplighetInnehaBehorighetSpecial>\n" +
            "      <behovAvLakareSpecialistKompetens>Kronologisk bastuberedning</behovAvLakareSpecialistKompetens>\n" +
            "    </bedomning>\n" +
            "  </ns3:intyg>\n" +
            "</ns3:RegisterTSDiabetes>";

    private TsDiabetesHelper tsDiabetesHelper = new TsDiabetesHelper();


    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = tsDiabetesHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTO() throws JAXBException {
        RegisterTSDiabetesType intyg = tsDiabetesHelper.unmarshalXml(xmlIntyg);

        IntygDTO dto = tsDiabetesHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2013, 3, 17);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals(IntygType.getByItIntygType("ts-diabetes"), dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(100, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    @Test
    public void testConvertToDTOWithWrongOuterElementInXmlShouldWorkIntyg7276() throws JAXBException {
        final String faultyXml = xmlIntyg.replaceAll("ns3:RegisterTSDiabetes", "ThisIsWrong");
        RegisterTSDiabetesType intyg = tsDiabetesHelper.unmarshalXml(faultyXml);

        IntygDTO dto = tsDiabetesHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2013, 3, 17);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals(IntygType.getByItIntygType("ts-diabetes"), dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(100, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    @Test
    public void testExtractHSAKey() throws JAXBException {
        RegisterTSDiabetesType intyg = tsDiabetesHelper.unmarshalXml(xmlIntyg);

        HSAKey hsaKey = tsDiabetesHelper.extractHSAKey(intyg);

        assertEquals("ENHETSID", hsaKey.getEnhetId().getId());
        assertEquals("VARDGIVARID", hsaKey.getVardgivareId().getId());
        assertEquals("LAKAREID", hsaKey.getLakareId().getId());
    }

    @Test
    public void testGetPatientDataWithNoDatePeriod() {
        final Patientdata result = callGetPatientdata("19500910-1824", tsDiabetesHelper);
        Assert.assertEquals(ConversionHelper.NO_AGE, result.getAlder());
    }

    private Patientdata callGetPatientdata(String pnr, TsDiabetesHelper rch) {
        RegisterTSDiabetesType registerTSDiabetesType = new RegisterTSDiabetesType();
        final TSDiabetesIntyg intyg = new TSDiabetesIntyg();
        registerTSDiabetesType.setIntyg(intyg);

        GrundData grundData = new GrundData();
        intyg.setGrundData(grundData);

        final Patient patient = new Patient();
        grundData.setPatient(patient);

        final II personId = new II();
        patient.setPersonId(personId);

        personId.setExtension(pnr);

        //When
        return rch.getPatientData(registerTSDiabetesType);
    }

    @Test
    public void unmarshalXmlHandlesConcurrentCalls() {
        IntStream.range(1,25).parallel().forEach(value -> {
            try {
                tsDiabetesHelper.unmarshalXml(xmlIntyg);
            } catch (JAXBException e) {
                //ok in this test
            } catch (Exception e) {
                fail("Unexpected exception: " + e.toString());
            }
        });
    }
}
