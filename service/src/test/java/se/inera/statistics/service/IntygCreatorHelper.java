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
package se.inera.statistics.service;

public class IntygCreatorHelper {


    public static String getFk7263Xml() {
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:RegisterCertificate xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2\">\n" +
                "  <ns2:intyg>\n" +
                "    <intygs-id xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
                "      <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">123456789</ns3:root>\n" +
                "      <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1234567</ns3:extension>\n" +
                "    </intygs-id>\n" +
                "    <typ xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
                "      <ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">fk7263</ns3:code>\n" +
                "      <ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">f6fb361a-e31d-48b8-8657-99b63912dd9b</ns3:codeSystem>\n" +
                "      <ns3:displayName xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">Läkarintyg för sjukpenning utökat</ns3:displayName>\n" +
                "    </typ>\n" +
                "    <version xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">1</version>\n" +
                "    <signeringstidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">2015-12-07T15:48:05</signeringstidpunkt>\n" +
                "    <skickatTidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">2015-12-07T15:48:05</skickatTidpunkt>\n" +
                "    <patient xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\">\n" +
                "      <person-id>\n" +
                "        <ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">1.2.752.129.2.1.3.3</ns3:root>\n" +
                "        <ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">19680524-9288</ns3:extension>\n" +
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
                "      <delsvar id=\"1.1\">\n" +
                "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:code>2</ns3:code>\n" +
                "          <ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem>\n" +
                "        </ns3:cv>\n" +
                "      </delsvar>\n" +
                "      <delsvar id=\"1.2\">2015-12-08</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"28\">\n" +
                "      <delsvar id=\"28.1\">\n" +
                "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:code>1</ns3:code>\n" +
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
                "          <ns3:code>F32</ns3:code>\n" +
                "          <ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem>\n" +
                "        </ns3:cv>\n" +
                "      </delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"35\">\n" +
                "      <delsvar id=\"35.1\">Haltar när han dansar</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"17\">\n" +
                "      <delsvar id=\"17.1\">Kommer inte in i bilen</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"32\">\n" +
                "      <delsvar id=\"32.1\">\n" +
                "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:code>1</ns3:code>\n" +
                "          <ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem>\n" +
                "        </ns3:cv>\n" +
                "      </delsvar>\n" +
                "      <delsvar id=\"32.2\">\n" +
                "        <ns3:datePeriod xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:start>2013-02-01</ns3:start>\n" +
                "          <ns3:end>2013-02-14</ns3:end>\n" +
                "        </ns3:datePeriod>\n" +
                "      </delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"37\">\n" +
                "      <delsvar id=\"37.1\">Överskrider inte FMB</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"33\">\n" +
                "      <delsvar id=\"33.1\">true</delsvar>\n" +
                "      <delsvar id=\"33.2\">Kan bara jobba på nätterna.</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"34\">\n" +
                "      <delsvar id=\"34.1\">true</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"23\">\n" +
                "      <delsvar id=\"23.1\">Är bra på att dansa!</delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"39\">\n" +
                "      <delsvar id=\"39.1\">\n" +
                "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:code>1</ns3:code>\n" +
                "          <ns3:codeSystem>KV_FKMU_0006</ns3:codeSystem>\n" +
                "        </ns3:cv>\n" +
                "      </delsvar>\n" +
                "    </svar>\n" +
                "    <svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:2\" id=\"40\">\n" +
                "      <delsvar id=\"40.1\">\n" +
                "        <ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:2\">\n" +
                "          <ns3:code>11</ns3:code>\n" +
                "          <ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem>\n" +
                "        </ns3:cv>\n" +
                "      </delsvar>\n" +
                "      <delsvar id=\"40.2\">Jobbar bra om man inte stör honom</delsvar>\n" +
                "    </svar>\n" +
                "  </ns2:intyg>\n" +
                "</ns2:RegisterCertificate>";

        return data;
    }

    public static String getFk7263Json() {
        String data = "{\"aktivitetsbegransning\":\"\",\"annanAtgard\":\"Patienten ansvarar f\\u00f6r att armen h\\u00e5lls i stillhet\",\"annanReferens\":\"2010-01-24\",\"arbetsformagaPrognos\":\"Arbetsf\\u00f6rm\\u00e5ga: Skadan har f\\u00f6rv\\u00e4rrats vid varje tillf\\u00e4lle patienten anv\\u00e4nt armen. M\\u00e5ste h\\u00e5llas i total stillhet tills l\\u00e4kningsprocessen kommit en bit p\\u00e5 v\\u00e4g. Eventuellt kan utredning visa att operation \\u00e4r n\\u00f6dv\\u00e4ndig f\\u00f6r att l\\u00e4ka skadan.\",\"arbetsloshet\":true,\"atgardInomSjukvarden\":\"Utreds om operation \\u00e4r n\\u00f6dv\\u00e4ndig\",\"avstangningSmittskydd\":false,\"diagnosBeskrivning\":\"F\\u00f6rtydligande: extra kl\\u00e4md\",\"diagnosBeskrivning1\":\"Kl\\u00e4mskada p\\u00e5 skuldra och \\u00f6verarm\",\"diagnosKod\":\"G01\",\"diagnosKodsystem1\":\"ICD_10_SE\",\"foraldrarledighet\":true,\"funktionsnedsattning\":\"\",\"giltighet\":{\"from\":\"2011-01-26\",\"tom\":\"2011-05-31\"},\"grundData\":{\"patient\":{\"efternamn\":\"Test Testorsson\",\"fullstandigtNamn\":\"Test Testorsson\",\"personId\":\"19790717-9191\"},\"signeringsdatum\":\"2011-01-26T00:00:00.000\",\"skapadAv\":{\"fullstandigtNamn\":\"En L\\u00e4kare\",\"personId\":\"Personal HSA-ID\",\"vardenhet\":{\"arbetsplatsKod\":\"123456789011\",\"enhetsid\":\"enhet1\",\"enhetsnamn\":\"Kir mott\",\"epost\":\"kirmott@vardenhet.se\",\"postadress\":\"Lasarettsv\\u00e4gen 13\",\"postnummer\":\"85150\",\"postort\":\"Sundsvall\",\"telefonnummer\":\"060-1818000\",\"vardgivare\":{\"vardgivarid\":\"vg1\",\"vardgivarnamn\":\"Landstinget Norrland\"}}}},\"id\":\"80832895-5a9c-450a-bd74-08af43750788\",\"journaluppgifter\":\"2010-01-14\",\"kommentar\":\"Prognosen f\\u00f6r patienten \\u00e4r god.\\nHan kommer att kunna \\u00e5terg\\u00e5 till sitt arbete efter genomf\\u00f6rd behandling.\",\"kontaktMedFk\":true,\"nedsattMed100\":{\"from\":\"2013-02-05\",\"tom\":\"2013-02-06\"},\"nuvarandeArbete\":true,\"nuvarandeArbetsuppgifter\":\"Dirigent. Dirigerar en st\\u00f6rre orkester p\\u00e5 deltid\",\"prognosBedomning\":\"arbetsformagaPrognosGarInteAttBedoma\",\"rehabilitering\":\"rehabiliteringGarInteAttBedoma\",\"rekommendationKontaktArbetsformedlingen\":true,\"rekommendationKontaktForetagshalsovarden\":true,\"rekommendationOvrigt\":\"N\\u00e4r skadan f\\u00f6rb\\u00e4ttrats rekommenderas muskeluppbyggande sjukgymnastik\",\"rekommendationOvrigtCheck\":true,\"ressattTillArbeteEjAktuellt\":true,\"sjukdomsforlopp\":\"Bed\\u00f6mttillst\\u00e5nd: Patienten kl\\u00e4mde h\\u00f6ger \\u00f6verarm vid olycka i hemmet.\\nProblemen har p\\u00e5g\\u00e5tt en l\\u00e4ngre tid.\",\"telefonkontaktMedPatienten\":\"2011-01-12\",\"typ\":\"fk7263\",\"undersokningAvPatienten\":\"2011-01-26\"}";

        return data;
    }

    public static String getTsBas() {
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ns3:RegisterTSBas xmlns=\"urn:local:se:intygstjanster:services:1\"\n" +
                "    xmlns:ns2=\"urn:local:se:intygstjanster:services:types:1\"\n" +
                "    xmlns:ns3=\"urn:local:se:intygstjanster:services:RegisterTSBasResponder:1\">\n" +
                "    <ns3:intyg>\n" +
                "  <intygsId>intygsId0</intygsId>\n" +
                "  <intygsTyp>ts-bas</intygsTyp>\n" +
                "  <version>06</version>\n" +
                "  <utgava>07</utgava>\n" +
                "  <grundData>\n" +
                "    <signeringsTidstampel>2013-08-12T15:57:00</signeringsTidstampel>\n" +
                "    <skapadAv>\n" +
                "      <personId>\n" +
                "        <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "        <ns2:extension>SE0000000000-1333</ns2:extension>\n" +
                "      </personId>\n" +
                "      <fullstandigtNamn>Doktor Thompson</fullstandigtNamn>\n" +
                "      <atLakare>false</atLakare>\n" +
                "      <vardenhet>\n" +
                "        <enhetsId>\n" +
                "          <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "          <ns2:extension>SE0000000000-1337</ns2:extension>\n" +
                "        </enhetsId>\n" +
                "        <enhetsnamn>Vårdenhet Väst</enhetsnamn>\n" +
                "        <postadress>Enhetsvägen 12</postadress>\n" +
                "        <postnummer>54321</postnummer>\n" +
                "        <postort>Tumba</postort>\n" +
                "        <telefonnummer>08-1337</telefonnummer>\n" +
                "        <vardgivare>\n" +
                "          <vardgivarid>\n" +
                "            <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "            <ns2:extension>SE0000000000-HAHAHHSAA</ns2:extension>\n" +
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
                "</ns3:RegisterTSBas>\n";

        return data;
    }

    public static String getTsDiabetes() {
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ns3:RegisterTSDiabetes xmlns=\"urn:local:se:intygstjanster:services:1\"\n" +
                "    xmlns:ns2=\"urn:local:se:intygstjanster:services:types:1\"\n" +
                "    xmlns:ns3=\"urn:local:se:intygstjanster:services:RegisterTSDiabetesResponder:1\">\n" +
                "  <ns3:intyg>\n" +
                "    <intygsId>987654321</intygsId>\n" +
                "    <intygsTyp>ts-diabetes</intygsTyp>\n" +
                "    <version>02</version>\n" +
                "    <utgava>06</utgava>\n" +
                "    <grundData>\n" +
                "      <signeringsTidstampel>2013-08-12T15:57:00</signeringsTidstampel>\n" +
                "      <skapadAv>\n" +
                "        <personId>\n" +
                "          <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "          <ns2:extension>SE0000000000-1333</ns2:extension>\n" +
                "        </personId>\n" +
                "        <fullstandigtNamn>Doktor Thompson</fullstandigtNamn>\n" +
                "        <atLakare>false</atLakare>\n" +
                "        <vardenhet>\n" +
                "          <enhetsId>\n" +
                "            <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "            <ns2:extension>SE0000000000-1337</ns2:extension>\n" +
                "          </enhetsId>\n" +
                "          <enhetsnamn>Vårdenhet Väst</enhetsnamn>\n" +
                "          <postadress>Enhetsvägen 12</postadress>\n" +
                "          <postnummer>54321</postnummer>\n" +
                "          <postort>Tumba</postort>\n" +
                "          <telefonnummer>08-1337</telefonnummer>\n" +
                "          <vardgivare>\n" +
                "            <vardgivarid>\n" +
                "              <ns2:root>1.2.752.129.2.1.4.1</ns2:root>\n" +
                "              <ns2:extension>SE0000000000-HAHAHHSAA</ns2:extension>\n" +
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
                "</ns3:RegisterTSDiabetes>\n";

        return data;
    }
}
