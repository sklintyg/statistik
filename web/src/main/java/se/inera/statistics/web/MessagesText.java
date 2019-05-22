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
package se.inera.statistics.web;

// CHECKSTYLE:OFF LineLength
public final class MessagesText {

    public static final String MESSAGE_NO_DATA = "Ingen data tillgänglig. Det beror på att det inte finns någon data för verksamheten.";
    public static final String MESSAGE_TOO_MUCH_DATA = "Rapporten innehåller mycket data, vilket kan göra diagrammet svårt att läsa. Överväg att filtrera resultatet för att minska mängden data.";
    public static final String MESSAGE_DIAGNOS_MISS_MATCH = "Du har gjort ett val av diagnos som inte matchar det val du gjort i diagnosfiltret (se 'Visa alla aktiva filter' ovan).";
    public static final String MESSAGE_NO_DIAGNOSIS = "Inga diagnoser valda";
    public static final String MESSAGE_OVERVIEW_WITH_FILTER = "Översikten visar alltid de senaste tre avslutade kalendermånaderna (%s) oavsett valt tidsintervall.";


    public static final String STATISTICS_LEVEL_NATIONELL = "Nationell statistik";
    public static final String STATISTICS_LEVEL_VERKSAMHET = "Verksamhetsstatistik";
    public static final String STATISTICS_LEVEL_REGION = "Regionsstatistik";

    public static final String EXCEL_FILTER_SHEET_NAME = "urval";
    public static final String EXCEL_FILTER_SHEET_LINK = "Se aktuellt urval i fliken";

    public static final String EXCEL_TABLE_SHEET_NAME = "tabell";
    public static final String EXCEL_TABLE_SHEET_LINK = "Se tabellen i fliken";

    public static final String EXCEL_FILTER_ENHET_ALL = "Enheter";
    public static final String EXCEL_FILTER_ENHETER = "Valda enheter";
    public static final String EXCEL_FILTER_DIAGNOSER = "Valda diagnoser";
    public static final String EXCEL_FILTER_LANGDER = "Valda sjukskrivningslängder";
    public static final String EXCEL_FILTER_AGEGROUPS = "Valda åldersgrupper";
    public static final String EXCEL_FILTER_INTYGTYPES = "Valda intygstyper";

    public static final String FILTER_WRONG_FROM_AND_END_DATE = "Det finns ingen statistik innan %s och ingen efter %s, visar statistik mellan %s och %s.";
    public static final String FILTER_WRONG_FROM_DATE = "Det finns ingen statistik innan %s. Visar statistik från tidigast möjliga datum.";
    public static final String FILTER_WRONG_END_DATE = "Det finns ingen statistik efter %s. Visar statistik fram till senast möjliga datum.";
    public static final String FILTER_TOO_EARLY_FROM_AND_END_DATE = "Det finns ingen statistik innan 2013-10. Vänligen välj ett senare från- och tilldatum.";
    public static final String FILTER_TOO_LATE_FROM_AND_END_DATE = "Du har valt två framtida datum. Vänligen välj ett tidigare från- och tilldatum.";

    public static final String FILTER_NO_DATA = "Det finns ingen statistik att visa för den angivna filtreringen. Överväg en mindre restriktiv filtrering.";
    public static final String FILTER_COULD_NOT_APPLY = "Kunde ej applicera valt filter. Vänligen kontrollera filterinställningarna.";

    public static final String REPORT_GROUP_OTHER = "Övriga";
    public static final String REPORT_GROUP_TOTALT = "Totalt";
    public static final String REPORT_GROUP_MALE = "Män";
    public static final String REPORT_GROUP_FEMALE = "Kvinnor";

    public static final String REPORT_COLUMN_ANTAL_SJUKFALL_TOTALT = "Antal sjukfall totalt";
    public static final String REPORT_COLUMN_ANTAL_SJUKFALL_MALE = "Antal sjukfall för män";
    public static final String REPORT_COLUMN_ANTAL_SJUKFALL_FEMALE = "Antal sjukfall för kvinnor";

    public static final String REPORT_COLUMN_ANDEL_SJUKFALL_MALE = "Andel sjukfall för män";
    public static final String REPORT_COLUMN_ANDEL_SJUKFALL_FEMALE = "Andel sjukfall för kvinnor";

    public static final String REPORT_COLUMN_ANDEL_INTYG_TOTALT = "Andel intyg totalt";
    public static final String REPORT_COLUMN_ANDEL_INTYG_MALE = "Andel intyg för män";
    public static final String REPORT_COLUMN_ANDEL_INTYG_FEMALE = "Andel intyg för kvinnor";

    public static final String REPORT_COLUMN_ANTAL_INTYG_TOTALT = "Antal intyg totalt";
    public static final String REPORT_COLUMN_ANTAL_INTYG_MALE = "Antal intyg för män";
    public static final String REPORT_COLUMN_ANTAL_INTYG_FEMALE = "Antal intyg för kvinnor";

    public static final String REPORT_COLUMN_ANTAL_KOMPLETTERINGAR_TOTALT = "Antal kompletteringar totalt";
    public static final String REPORT_COLUMN_ANTAL_KOMPLETTERINGAR_MALE = "Antal kompletteringar för män";
    public static final String REPORT_COLUMN_ANTAL_KOMPLETTERINGAR_FEMALE = "Antal kompletteringar för kvinnor";

    public static final String REPORT_COLUMN_ANTAL_MESSAGES_TOTALT = "Antal meddelanden totalt";
    public static final String REPORT_COLUMN_ANTAL_MESSAGES_MALE = "Antal meddelanden för män";
    public static final String REPORT_COLUMN_ANTAL_MESSAGES_FEMALE = "Antal meddelanden för kvinnor";

    public static final String REPORT_ANTAL_SJUKFALL = "Antal sjukfall";
    public static final String REPORT_ANTAL_INVANARE = "Antal invånare";
    public static final String REPORT_ANTAL_SJUKFALL_PER_1000_INVANARE = "Antal sjukfall per 1000 invånare";
    public static final String REPORT_ANTAL_LISTNINGAR = "Antal listningar i arbetsför ålder";
    public static final String REPORT_ANTAL_SJUKFALL_PER_1000_LISTNINGAR = "Antal sjukfall per 1000 listningar";


    public static final String REPORT_SAMTLIGA_LAN = "Samtliga län";
    public static final String REPORT_LAN = "Län";
    public static final String REPORT_PERIOD = "Period";
    public static final String REPORT_ENHET = "Enhet";
    public static final String REPORT_VARDENHET = "Vårdenhet";
    public static final String REPORT_LAKARE = "Läkare";
    public static final String REPORT_ALDERSGRUPPER_REST = "Andra åldersgrupper";
    public static final String REPORT_DIAGNOSIS_REST = "Andra diagnosgrupper";

    public static final String REPORT_ANDEL_KOMPLETTERINGAR = "Total andel som fått komplettering, alla intygstyper inkluderade";
    public static final String REPORT_ANTAL_MEDDELANDEN_TOTALT = "Antal meddelanden totalt";
    public static final String REPORT_ANTAL_SJUKFALL_TOTALT = "Antal sjukfall totalt";
    public static final String REPORT_ANTAL_KOMPLETTERINGAR_TOTALT = "Antal kompletteringar totalt";
    public static final String REPORT_ANTAL_SJUKFALL_PER_1000 = "Antal sjukfall per 1000 listningar i arbetsför ålder";

    private MessagesText() { }

}

// CHECKSTYLE:ON LineLength
