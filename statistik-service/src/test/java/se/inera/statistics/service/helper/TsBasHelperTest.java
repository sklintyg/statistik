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
package se.inera.statistics.service.helper;

import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.Test;

import se.inera.intygstjanster.ts.services.RegisterTSBasResponder.v1.RegisterTSBasType;
import se.inera.intygstjanster.ts.services.types.v1.II;
import se.inera.intygstjanster.ts.services.v1.GrundData;
import se.inera.intygstjanster.ts.services.v1.Patient;
import se.inera.intygstjanster.ts.services.v1.TSBasIntyg;
import se.inera.statistics.service.hsa.HSAKey;
import se.inera.statistics.service.processlog.IntygDTO;
import se.inera.statistics.service.report.model.Kon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TsBasHelperTest {

    private static final String xmlIntyg = "<ns3:RegisterTSBas xmlns=\"urn:local:se:intygstjanster:services:1\"\n" +
            "    xmlns:ns2=\"urn:local:se:intygstjanster:services:types:1\"\n" +
            "    xmlns:ns3=\"urn:local:se:intygstjanster:services:RegisterTSBasResponder:1\">\n" +
            "    <ns3:intyg>\n" +
            "  <intygsId>intygsId0</intygsId>\n" +
            "  <intygsTyp>ts-bas</intygsTyp>\n" +
            "  <version>06</version>\n" +
            "  <utgava>07</utgava>\n" +
            "  <grundData>\n" +
            "    <signeringsTidstampel>2013-03-17T15:57:00</signeringsTidstampel>\n" +
            "    <skapadAv>\n" +
            "      <personId>\n" +
            "        <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "        <ns2:extension>LakareId</ns2:extension>\n" +
            "      </personId>\n" +
            "      <fullstandigtNamn>Doktor Thompson</fullstandigtNamn>\n" +
            "      <atLakare>false</atLakare>\n" +
            "      <vardenhet>\n" +
            "        <enhetsId>\n" +
            "          <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "          <ns2:extension>Enhetsid</ns2:extension>\n" +
            "        </enhetsId>\n" +
            "        <enhetsnamn>Vårdenhet Väst</enhetsnamn>\n" +
            "        <postadress>Enhetsvägen 12</postadress>\n" +
            "        <postnummer>54321</postnummer>\n" +
            "        <postort>Tumba</postort>\n" +
            "        <telefonnummer>08-1337</telefonnummer>\n" +
            "        <vardgivare>\n" +
            "          <vardgivarid>\n" +
            "            <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
            "            <ns2:extension>VardgivarId</ns2:extension>\n" +
            "          </vardgivarid>\n" +
            "          <vardgivarnamn>Vårdgivarnamn</vardgivarnamn>\n" +
            "        </vardgivare>\n" +
            "      </vardenhet>\n" +
            "      <specialiteter>SPECIALITET</specialiteter>\n" +
            "      <befattningar>ST_LAKARE</befattningar>\n" +
            "    </skapadAv>\n" +
            "    <patient>\n" +
            "      <personId>\n" +
            "        <ns2:root>1.2.752.129.2.1.3.1</ns2:root>\n" +
            "        <ns2:extension>19121212-1212</ns2:extension>\n" +
            "      </personId>\n" +
            "      <fullstandigtNamn>Herr Dundersjuk</fullstandigtNamn>\n" +
            "      <fornamn>Herr</fornamn>\n" +
            "      <efternamn>Dundersjuk</efternamn>\n" +
            "      <postadress>Testvägen 12</postadress>\n" +
            "      <postnummer>12345</postnummer>\n" +
            "      <postort>Testort</postort>\n" +
            "    </patient>\n" +
            "  </grundData>\n" +
            "  <intygAvser>\n" +
            "    <korkortstyp>C1</korkortstyp>\n" +
            "    <korkortstyp>C1E</korkortstyp>\n" +
            "    <korkortstyp>C</korkortstyp>\n" +
            "    <korkortstyp>CE</korkortstyp>\n" +
            "    <korkortstyp>D1</korkortstyp>\n" +
            "    <korkortstyp>D1E</korkortstyp>\n" +
            "    <korkortstyp>D</korkortstyp>\n" +
            "    <korkortstyp>DE</korkortstyp>\n" +
            "    <korkortstyp>TAXI</korkortstyp>\n" +
            "    <korkortstyp>ANNAT</korkortstyp>\n" +
            "  </intygAvser>\n" +
            "  <identitetStyrkt>\n" +
            "    <idkontroll>IDK6</idkontroll>\n" +
            "  </identitetStyrkt>\n" +
            "  <synfunktion>\n" +
            "    <harSynfaltsdefekt>true</harSynfaltsdefekt>\n" +
            "    <harNattblindhet>true</harNattblindhet>\n" +
            "    <harProgressivOgonsjukdom>true</harProgressivOgonsjukdom>\n" +
            "    <harDiplopi>true</harDiplopi>\n" +
            "    <harNystagmus>true</harNystagmus>\n" +
            "    <synskarpaUtanKorrektion>\n" +
            "      <hogerOga>0.0</hogerOga>\n" +
            "      <vansterOga>0.0</vansterOga>\n" +
            "      <binokulart>0.0</binokulart>\n" +
            "    </synskarpaUtanKorrektion>\n" +
            "    <synskarpaMedKorrektion>\n" +
            "      <hogerOga>0.0</hogerOga>\n" +
            "      <harKontaktlinsHogerOga>true</harKontaktlinsHogerOga>\n" +
            "      <vansterOga>0.0</vansterOga>\n" +
            "      <harKontaktlinsVansterOga>true</harKontaktlinsVansterOga>\n" +
            "      <binokulart>0.0</binokulart>\n" +
            "    </synskarpaMedKorrektion>\n" +
            "    <harGlasStyrkaOver8Dioptrier>true</harGlasStyrkaOver8Dioptrier>\n" +
            "  </synfunktion>\n" +
            "  <horselBalanssinne>\n" +
            "    <harBalansrubbningYrsel>true</harBalansrubbningYrsel>\n" +
            "    <harSvartUppfattaSamtal4Meter>true</harSvartUppfattaSamtal4Meter>\n" +
            "  </horselBalanssinne>\n" +
            "  <rorelseorganensFunktioner>\n" +
            "    <harRorelsebegransning>true</harRorelsebegransning>\n" +
            "    <rorelsebegransningBeskrivning>Spik i foten</rorelsebegransningBeskrivning>\n" +
            "    <harOtillrackligRorelseformagaPassagerare>true</harOtillrackligRorelseformagaPassagerare>\n" +
            "  </rorelseorganensFunktioner>\n" +
            "  <hjartKarlSjukdomar>\n" +
            "    <harRiskForsamradHjarnFunktion>true</harRiskForsamradHjarnFunktion>\n" +
            "    <harHjarnskadaICNS>true</harHjarnskadaICNS>\n" +
            "    <harRiskfaktorerStroke>true</harRiskfaktorerStroke>\n" +
            "    <riskfaktorerStrokeBeskrivning>Förkärlek för Elvismackor</riskfaktorerStrokeBeskrivning>\n" +
            "  </hjartKarlSjukdomar>\n" +
            "  <diabetes>\n" +
            "    <harDiabetes>true</harDiabetes>\n" +
            "    <diabetesTyp>TYP2</diabetesTyp>\n" +
            "    <harBehandlingKost>true</harBehandlingKost>\n" +
            "  </diabetes>\n" +
            "  <neurologiskaSjukdomar>true</neurologiskaSjukdomar>\n" +
            "  <medvetandestorning>\n" +
            "    <harMedvetandestorning>true</harMedvetandestorning>\n" +
            "    <medvetandestorningBeskrivning>Beskrivning</medvetandestorningBeskrivning>\n" +
            "  </medvetandestorning>\n" +
            "  <harNjurSjukdom>true</harNjurSjukdom>\n" +
            "  <harKognitivStorning>true</harKognitivStorning>\n" +
            "  <harSomnVakenhetStorning>true</harSomnVakenhetStorning>\n" +
            "  <alkoholNarkotikaLakemedel>\n" +
            "    <harTeckenMissbruk>true</harTeckenMissbruk>\n" +
            "    <harVardinsats>true</harVardinsats>\n" +
            "    <harVardinsatsProvtagningBehov>true</harVardinsatsProvtagningBehov>\n" +
            "    <harLakarordineratLakemedelsbruk>true</harLakarordineratLakemedelsbruk>\n" +
            "    <lakarordineratLakemedelOchDos>Läkemedel och dos</lakarordineratLakemedelOchDos>\n" +
            "  </alkoholNarkotikaLakemedel>\n" +
            "  <harPsykiskStorning>true</harPsykiskStorning>\n" +
            "  <utvecklingsstorning>\n" +
            "    <harPsykiskUtvecklingsstorning>true</harPsykiskUtvecklingsstorning>\n" +
            "    <harAndrayndrom>true</harAndrayndrom>\n" +
            "  </utvecklingsstorning>\n" +
            "  <sjukhusvard>\n" +
            "    <harSjukhusvardEllerLakarkontakt>true</harSjukhusvardEllerLakarkontakt>\n" +
            "    <sjukhusvardEllerLakarkontaktDatum>20 Januari</sjukhusvardEllerLakarkontaktDatum>\n" +
            "    <sjukhusvardEllerLakarkontaktVardinrattning>Vårdcentralen</sjukhusvardEllerLakarkontaktVardinrattning>\n" +
            "    <sjukhusvardEllerLakarkontaktAnledning>Akut lungsot</sjukhusvardEllerLakarkontaktAnledning>\n" +
            "  </sjukhusvard>\n" +
            "  <ovrigMedicinering>\n" +
            "    <harStadigvarandeMedicinering>true</harStadigvarandeMedicinering>\n" +
            "    <stadigvarandeMedicineringBeskrivning>Alvedon</stadigvarandeMedicineringBeskrivning>\n" +
            "  </ovrigMedicinering>\n" +
            "  <ovrigKommentar>Här kommer en övrig kommentar</ovrigKommentar>\n" +
            "  <bedomning>\n" +
            "    <korkortstyp>C1</korkortstyp>\n" +
            "    <korkortstyp>C1E</korkortstyp>\n" +
            "    <korkortstyp>C</korkortstyp>\n" +
            "    <korkortstyp>CE</korkortstyp>\n" +
            "    <korkortstyp>D1</korkortstyp>\n" +
            "    <korkortstyp>D1E</korkortstyp>\n" +
            "    <korkortstyp>D</korkortstyp>\n" +
            "    <korkortstyp>DE</korkortstyp>\n" +
            "    <korkortstyp>TAXI</korkortstyp>\n" +
            "    <korkortstyp>ANNAT</korkortstyp>\n" +
            "    <kanInteTaStallning>false</kanInteTaStallning>\n" +
            "    <behovAvLakareSpecialistKompetens>Spektralanalys</behovAvLakareSpecialistKompetens>\n" +
            "  </bedomning>\n" +
            "</ns3:intyg>\n" +
            "</ns3:RegisterTSBas>";

    private TsBasHelper tsBasHelper = new TsBasHelper();


    @Test
    public void testConvertToDTONull() {
        IntygDTO dto = tsBasHelper.convertToDTO(null);

        assertNull(dto);
    }

    @Test
    public void testConvertToDTO() throws JAXBException {
        RegisterTSBasType intyg = tsBasHelper.unmarshalXml(xmlIntyg);

        IntygDTO dto = tsBasHelper.convertToDTO(intyg);
        LocalDate signeringsdatum = LocalDate.of(2013, 3, 17);

        assertEquals("19121212-1212", dto.getPatientid());
        assertEquals("ts-bas", dto.getIntygtyp());
        assertEquals("Enhetsid", dto.getEnhet());
        assertEquals(100, dto.getPatientData().getAlder());
        assertEquals(Kon.MALE, dto.getPatientData().getKon());
        assertEquals(signeringsdatum, dto.getSigneringsdatum());
    }

    @Test
    public void testExtractHSAKey() throws JAXBException {
        RegisterTSBasType intyg = tsBasHelper.unmarshalXml(xmlIntyg);

        HSAKey hsaKey = tsBasHelper.extractHSAKey(intyg);

        assertEquals("ENHETSID", hsaKey.getEnhetId().getId());
        assertEquals("VARDGIVARID", hsaKey.getVardgivareId().getId());
        assertEquals("LAKAREID", hsaKey.getLakareId().getId());
    }

    @Test
    public void testGetPatientDataWithNoDatePeriod() {
        final Patientdata result = callGetPatientdata("19500910-1824", tsBasHelper);
        assertEquals(ConversionHelper.NO_AGE, result.getAlder());
    }

    private Patientdata callGetPatientdata(String pnr, TsBasHelper rch) {
        RegisterTSBasType registerTSBasType = new RegisterTSBasType();
        final TSBasIntyg intyg = new TSBasIntyg();
        registerTSBasType.setIntyg(intyg);

        GrundData grundData = new GrundData();
        intyg.setGrundData(grundData);

        final Patient patient = new Patient();
        grundData.setPatient(patient);

        final II personId = new II();
        patient.setPersonId(personId);

        personId.setExtension(pnr);

        //When
        return rch.getPatientData(registerTSBasType);
    }

    @Test
    public void unmarshalXmlHandlesConcurrentCalls() {
        IntStream.range(1,25).parallel().forEach(value -> {
            try {
                tsBasHelper.unmarshalXml(xmlIntyg);
            } catch (JAXBException e) {
                //ok in this test
            } catch (Exception e) {
                fail("Unexpected exception: " + e.toString());
            }
        });
    }
}
