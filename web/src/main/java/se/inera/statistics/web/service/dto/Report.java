/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.service.dto;

public enum Report {

    N_SJUKFALLTOTALT(StatisticsLevel.NATIONELL, "SjukfallTotalt", "Antal sjukfall"),
    N_DIAGNOSGRUPP(StatisticsLevel.NATIONELL, "Diagnosgrupp", "Antal sjukfall fördelat på diagnosgrupp"),
    N_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.NATIONELL,
        "DiagnosgruppEnskiltDiagnoskapitel", "Antal sjukfall för"),
    N_ALDERSGRUPP(StatisticsLevel.NATIONELL, "Aldersgrupp", "Antal sjukfall fördelat på åldersgrupp"),
    N_SJUKSKRIVNINGSGRAD(StatisticsLevel.NATIONELL, "Sjukskrivningsgrad", "Antal sjukfall fördelat på sjukskrivningsgrad"),
    N_SJUKSKRIVNINGSLANGD(StatisticsLevel.NATIONELL, "Sjukskrivningslangd", "Antal sjukfall fördelat på sjukskrivningslängd"),
    N_LAN(StatisticsLevel.NATIONELL, "Lan", "Antal sjukfall per 1000 invånare fördelat på län"),
    N_LANANDELSJUKFALLPERKON(StatisticsLevel.NATIONELL, "Konsfordelningperlan", "Andel sjukfall per kön fördelat på län"),
    N_MEDDELANDENPERAMNE(StatisticsLevel.NATIONELL, "Meddelandentotalt", "Antal inkomna meddelanden fördelat på ämne"),
    N_INTYGPERTYP(StatisticsLevel.NATIONELL, "Intygtotalt", "Antal utfärdade intyg fördelat på intygstyp"),
    N_INTYGPERSJUKFALL(StatisticsLevel.NATIONELL, "IntygPerSjukfall", "Antal intyg per sjukfall"),
    N_ANDELKOMPLETTERINGAR(StatisticsLevel.NATIONELL, "Kompletteringandelintyg", "Andel utfärdade intyg som fått en kompletteringsbegäran"),
    N_KOMPLETTERINGARPERFRAGA(StatisticsLevel.NATIONELL, "Kompletteringarperfraga", "Antal kompletteringar per fråga"),
    V_SJUKFALLTOTALT(StatisticsLevel.VERKSAMHET, "SjukfallTotalt", "Antal sjukfall"),
    V_INTYGPERMANAD(StatisticsLevel.VERKSAMHET, "IntygPerManad", "Antal intyg"),
    V_INTYGPERTYP(StatisticsLevel.VERKSAMHET, "Intygtotalt", "Antal utfärdade intyg fördelat på intygstyp"),
    V_MEDDELANDENTOTALT(StatisticsLevel.VERKSAMHET, "MeddelandenTotalt", "Antal inkomna meddelanden"),
    V_MEDDELANDENPERAMNE(StatisticsLevel.VERKSAMHET, "Meddelandentotalt", "Antal inkomna meddelanden fördelat på ämne"),
    V_MEDDELANDENPERAMNEPERENHET(StatisticsLevel.VERKSAMHET, "Vardenhet", "Antal inkomna meddelanden fördelat på vårdenhet"),
    V_MEDDELANDENPERAMNEPERLAKARE(StatisticsLevel.VERKSAMHET, "Lakare", "Antal inkomna meddelanden fördelat på ämne och läkare"),
    V_ANDELKOMPLETTERINGAR(StatisticsLevel.VERKSAMHET, "Kompletteringandelintyg",
        "Andel utfärdade intyg som fått en kompletteringsbegäran"),
    V_KOMPLETTERINGARPERFRAGA(StatisticsLevel.VERKSAMHET, "Kompletteringarperfraga", "Antal kompletteringar per fråga"),
    V_VARDENHET(StatisticsLevel.VERKSAMHET, "Vardenhet", "Antal sjukfall fördelat på vårdenhet eller enhet"),
    V_SJUKFALLPERLAKARE(StatisticsLevel.VERKSAMHET, "SjukfallPerLakare", "Antal sjukfall fördelat på läkare"),
    V_DIAGNOSGRUPP(StatisticsLevel.VERKSAMHET, "Diagnosgrupp", "Antal sjukfall fördelat på diagnosgrupp"),
    V_DIAGNOSGRUPPENSKILTDIAGNOSKAPITEL(StatisticsLevel.VERKSAMHET,
        "DiagnosgruppEnskiltDiagnoskapitel", "Antal sjukfall för"),
    V_DIAGNOSGRUPPJAMFORVALFRIADIAGNOSER(StatisticsLevel.VERKSAMHET,
        "DiagnosgruppJamforValfriaDiagnoser", "Jämförelse av valfria diagnoser"),
    V_ALDERSGRUPP(StatisticsLevel.VERKSAMHET, "Aldersgrupp", "Antal sjukfall fördelat på åldersgrupp"),
    V_LAKARALDEROCHKON(StatisticsLevel.VERKSAMHET, "LakaralderOchKon", "Antal sjukfall fördelat på läkares ålder och kön"),
    V_LAKARBEFATTNING(StatisticsLevel.VERKSAMHET, "Lakarbefattning", "Antal sjukfall fördelat på läkarbefattning"),
    V_INTYGPERSJUKFALL(StatisticsLevel.VERKSAMHET, "IntygPerSjukfall", "Antal intyg per sjukfall"),
    V_SJUKSKRIVNINGSGRAD(StatisticsLevel.VERKSAMHET, "Sjukskrivningsgrad", "Antal sjukfall fördelat på sjukskrivningsgrad"),
    V_SJUKSKRIVNINGSLANGD(StatisticsLevel.VERKSAMHET, "Sjukskrivningslangd", "Antal sjukfall fördelat på sjukskrivningslängd"),
    V_SJUKSKRIVNINGSLANGDMERAN90DAGAR(StatisticsLevel.VERKSAMHET,
        "SjukskrivningslangdMerAn90Dagar", "Antal sjukfall som är längre än 90 dagar"),
    L_SJUKFALLTOTALT(StatisticsLevel.REGION, "SjukfallTotalt", "Antal sjukfall"),
    L_VARDENHET(StatisticsLevel.REGION, "Vardenhet", "Antal sjukfall fördelat på vårdenhet"),
    L_VARDENHETLISTNINGAR(StatisticsLevel.REGION, "VardenhetListningar", "Antal sjukfall per 1000 listningar fördelat på vårdenhet"),
    L_INTYGPERSJUKFALL(StatisticsLevel.REGION, "IntygPerSjukfall", "Antal intyg per sjukfall"),
    L_ANDELKOMPLETTERINGAR(StatisticsLevel.REGION, "Kompletteringandelintyg", "Andel utfärdade intyg som fått en kompletteringsbegäran"),
    L_KOMPLETTERINGARPERFRAGA(StatisticsLevel.REGION, "Kompletteringarperfraga", "Antal kompletteringar per fråga"),
    L_MEDDELANDENPERAMNE(StatisticsLevel.REGION, "Meddelandentotalt", "Antal inkomna meddelanden fördelat på ämne"),
    L_INTYGPERTYP(StatisticsLevel.REGION, "Intygtotalt", "Antal utfärdade intyg fördelat på intygstyp"),
    L_MEDDELANDENPERAMNEPERENHET(StatisticsLevel.REGION, "Vardenhet", "Antal inkomna meddelanden fördelat på vårdenhet");

    private final StatisticsLevel statisticsLevel;
    private final String shortName; //e.g. used in file name, should therefore not contain spaces
    private final String longName; //e.g. used for title in excel export

    Report(StatisticsLevel statisticsLevel, String shortName, String longName) {
        this.statisticsLevel = statisticsLevel;
        this.shortName = shortName;
        this.longName = longName;
    }

    public StatisticsLevel getStatisticsLevel() {
        return statisticsLevel;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

}
