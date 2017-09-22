/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.report.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class VerksamhetsTyp implements Iterable<String> {

    public static final String OVRIGT_ID = "00";
    public static final String OVRIGT = "Okänd verksamhetstyp";
    public static final String VARDCENTRAL_ID = "02";
    public static final String VARDCENTRAL = "Vårdcentral";

    private final Map<String, String> kodToName = new LinkedHashMap<>();
    private final Map<String, String> longKodToName = new LinkedHashMap<>();

    // CHECKSTYLE:OFF MethodLength
    public VerksamhetsTyp() {
        kodToName.put(OVRIGT_ID, OVRIGT);
        kodToName.put(VARDCENTRAL_ID, VARDCENTRAL);
        kodToName.put("10", "Barn- och ungdomsverksamhet");
        kodToName.put("11", "Medicinsk verksamhet");
        kodToName.put("12", "Laboratorieverksamhet");
        kodToName.put("13", "Opererande verksamhet");
        kodToName.put("14", "Övrig medicinsk verksamhet");
        kodToName.put("15", "Primärvårdsverksamhet");
        kodToName.put("16", "Psykiatrisk verksamhet");
        kodToName.put("17", "Radiologisk verksamhet");
        kodToName.put("18", "Tandvårdsverksamhet");
        kodToName.put("20", "Övrig medicinsk serviceverksamhet");
        kodToName.put("23", "Socialtjänstverksamhet");

        longKodToName.put("1001", "Avancerad hemsjukvårdsverksamhet, barn och ungdom");
        longKodToName.put("1002", "Allergologi, barn och ungdom");
        longKodToName.put("1004", "Ätstörningsvård, barn och ungdom");
        longKodToName.put("1006", "Cystisk fibros-verksamhet");
        longKodToName.put("1007", "Habilitering, barn och ungdom");
        longKodToName.put("1009", "Intensivvård, barn och ungdom");
        longKodToName.put("1010", "Kardiologi, barn och ungdom");
        longKodToName.put("1011", "Kirurgi, barn och ungdom");
        longKodToName.put("1012", "Klinisk fysiologi, barn och ungdom");
        longKodToName.put("1013", "Medicin, barn och ungdom");
        longKodToName.put("1014", "Modersmjölksverksamhet");
        longKodToName.put("1016", "Medicinsk njursjukvård, barn och ungdom");
        longKodToName.put("1017", "Neonatalvård");
        longKodToName.put("1018", "Intensivvård, neonatal");
        longKodToName.put("1019", "Neurologi, barn och ungdom");
        longKodToName.put("1020", "Neuropsykiatri, barn och ungdom");
        longKodToName.put("1021", "Onkologi, barn och ungdom");
        longKodToName.put("1024", "Psykiatri, barn och ungdom");
        longKodToName.put("1025", "Radiologi, barn och ungdom");
        longKodToName.put("1027", "Omsorgsverksamhet för psykiskt utvecklingsstörda, barn och ungdom");
        longKodToName.put("1028", "Lekterapi");
        longKodToName.put("1029", "Akutverksamhet vid sjukhus, barn- och ungdomskirurgi");
        longKodToName.put("1030", "Akutverksamhet vid sjukhus, barn- och ungdomsmedicin");
        longKodToName.put("1031", "Akutverksamhet vid sjukhus, barn- och ungdomsortopedi");
        longKodToName.put("1032", "Akutverksamhet vid sjukhus, barn- och ungdomspsykiatri");
        longKodToName.put("1033", "Psykologverksamhet, barn och ungdom");
        longKodToName.put("1034", "Psykoterapi, barn och ungdom");
        longKodToName.put("1100", "Akutverksamhet vid sjukhus, specialiserad vård");
        longKodToName.put("1101", "Avancerad hemsjukvårdsverksamhet");
        longKodToName.put("1102", "Allergologi");
        longKodToName.put("1103", "Astma, stödverksamhet");
        longKodToName.put("1104", "Diabetologi");
        longKodToName.put("1105", "Dialysverksamhet");
        longKodToName.put("1106", "Endokrinologi");
        longKodToName.put("1108", "Geriatrik");
        longKodToName.put("1109", "Geriatrisk rehabilitering ");
        longKodToName.put("1110", "Hematologi");
        longKodToName.put("1112", "Dermatologi");
        longKodToName.put("1113", "Hypertoni, stödverksamhet");
        longKodToName.put("1114", "Internmedicin");
        longKodToName.put("1115", "Infektionssjukvård");
        longKodToName.put("1116", "Kardiologi");
        longKodToName.put("1117", "Koagulationssjukvård");
        longKodToName.put("1119", "Lungsjukvård");
        longKodToName.put("1120", "Medicinsk njursjukvård");
        longKodToName.put("1121", "Rehabiliteringsmedicin");
        longKodToName.put("1122", "Arbets- och miljömedicin");
        longKodToName.put("1123", "Neurologi");
        longKodToName.put("1124", "Onkologi");
        longKodToName.put("1125", "Reumatologi");
        longKodToName.put("1128", "Yrkesdermatologi");
        longKodToName.put("1129", "Andningsfysiologi");
        longKodToName.put("1130", "Endoskopiverksamhet");
        longKodToName.put("1131", "Medicinsk gastroenterologi och hepatologi ");
        longKodToName.put("1133", "Venereologi");
        longKodToName.put("1134", "Intensivvård, allmän");
        longKodToName.put("1135", "Minnesmottagning");
        longKodToName.put("1136", "Osteoporosvård");
        longKodToName.put("1137", "Strokevård");
        longKodToName.put("1138", "Sömnutredningsverksamhet");
        longKodToName.put("1140", "Akutverksamhet vid sjukhus, kardiologi");
        longKodToName.put("1141", "Hemofiliverksamhet");
        longKodToName.put("1142", "Diabetes, stödverksamhet");
        longKodToName.put("1143", "Inkontinens, stödverksamhet");
        longKodToName.put("1144", "Akutverksamhet vid sjukhus, infektion");
        longKodToName.put("1145", "Akutverksamhet vid sjukhus, internmedicin");
        longKodToName.put("1146", "Akutverksamhet vid sjukhus, neurologi");
        longKodToName.put("1147", "Smärtrehabiliteringsverksamhet");
        longKodToName.put("1148", "Intensivvård, hjärta");
        longKodToName.put("1149", "Intensivvård, neuro");
        longKodToName.put("1150", "Intensivvård, thorax");
        longKodToName.put("1151", "Intermediärvård");
        longKodToName.put("1152", "Ljusbehandlingsverksamhet");
        longKodToName.put("1153", "Förebyggande hälso- och sjukvård, stödverksamhet");
        longKodToName.put("1154", "Postoperativ uppvakningsverksamhet");
        longKodToName.put("1155", "Neurologisk rehabilitering");
        longKodToName.put("1202", "Blodverksamhet");
        longKodToName.put("1205", "Koagulationsverksamhet");
        longKodToName.put("1207", "Klinisk bakteriologi");
        longKodToName.put("1209", "Klinisk farmakologi");
        longKodToName.put("1210", "Klinisk fysiologi");
        longKodToName.put("1212", "Klinisk genetik");
        longKodToName.put("1214", "Klinisk immunologi");
        longKodToName.put("1215", "Klinisk kemi");
        longKodToName.put("1216", "Klinisk neurofysiologi");
        longKodToName.put("1217", "Klinisk nutrition");
        longKodToName.put("1218", "Klinisk patologi och cytologi");
        longKodToName.put("1219", "Klinisk virologi");
        longKodToName.put("1220", "Klinisk mikrobiologi");
        longKodToName.put("1222", "Klinisk mykologi");
        longKodToName.put("1229", "Rättsmedicin");
        longKodToName.put("1230", "Vävnadstypningsverksamhet");
        longKodToName.put("1232", "Stamcellsverksamhet");
        longKodToName.put("1233", "Rättsgenetik ");
        longKodToName.put("1234", "Rättskemi");
        longKodToName.put("1235", "Blodgivningsverksamhet");
        longKodToName.put("1236", "Transfusionsmedicin");
        longKodToName.put("1237", "Klinisk parasitologi");
        longKodToName.put("1238", "Laboratorieverksamhet, öppen vård");
        longKodToName.put("1302", "Anestesiologi");
        longKodToName.put("1304", "Audiologi");
        longKodToName.put("1305", "BB-verksamhet");
        longKodToName.put("1306", "Brännskadevård, riksuppdrag");
        longKodToName.put("1307", "Kolorektal kirurgi");
        longKodToName.put("1308", "Endokrin kirurgi");
        longKodToName.put("1309", "Förlossningsverksamhet");
        longKodToName.put("1310", "Gastroenterologisk kirurgi");
        longKodToName.put("1311", "Gynekologi");
        longKodToName.put("1312", "Onkologi, gynekologisk");
        longKodToName.put("1313", "Handkirurgi");
        longKodToName.put("1314", "Idrottsmedicin");
        longKodToName.put("1315", "Kirurgi");
        longKodToName.put("1316", "Kärlkirurgi");
        longKodToName.put("1317", "Leverkirurgi");
        longKodToName.put("1318", "Neurokirurgi");
        longKodToName.put("1324", "Ortopedi");
        longKodToName.put("1325", "Ortoptik");
        longKodToName.put("1326", "Plastikkirurgi");
        longKodToName.put("1328", "Smärtbehandlingsverksamhet, akut smärta");
        longKodToName.put("1329", "Thoraxkirurgi");
        longKodToName.put("1330", "Transplantationsverksamhet");
        longKodToName.put("1332", "Urologi");
        longKodToName.put("1333", "Öron-, näs- och halssjukvård");
        longKodToName.put("1334", "Foniatri");
        longKodToName.put("1335", "Ögonsjukvård");
        longKodToName.put("1336", "Bröstmottagning");
        longKodToName.put("1338", "Inkontinensvård");
        longKodToName.put("1340", "Reproduktionsmedicin");
        longKodToName.put("1342", "Stomi, stödverksamhet");
        longKodToName.put("1343", "Överviktsvård");
        longKodToName.put("1344", "Övre gastroenterologisk kirurgi");
        longKodToName.put("1345", "Rekonstruktiv plastikkirurgi");
        longKodToName.put("1346", "Estetisk plastikkirurgi");
        longKodToName.put("1347", "Akutverksamhet vid sjukhus, ortopedi");
        longKodToName.put("1348", "Akutverksamhet vid sjukhus, gynekologi ");
        longKodToName.put("1349", "Akutverksamhet vid sjukhus, kirurgi");
        longKodToName.put("1350", "Akutverksamhet vid sjukhus, våldtagna kvinnor");
        longKodToName.put("1351", "Akutverksamhet vid sjukhus, ögonsjukvård");
        longKodToName.put("1352", "Akutverksamhet vid sjukhus, öron-, näs- och halssjukvård");
        longKodToName.put("1353", "Specialistmödravård");
        longKodToName.put("1354", "Amningsmottagning");
        longKodToName.put("1355", "Ultraljudsverksamhet, gynekologisk");
        longKodToName.put("1402", "Arbetsterapi");
        longKodToName.put("1403", "Dietistverksamhet");
        longKodToName.put("1404", "Habilitering, vuxna ");
        longKodToName.put("1406", "Kiropraktik");
        longKodToName.put("1407", "Kuratorsverksamhet");
        longKodToName.put("1408", "Logopedi");
        longKodToName.put("1409", "Naprapati");
        longKodToName.put("1410", "Optikerverksamhet");
        longKodToName.put("1411", "Psykologverksamhet, vuxna");
        longKodToName.put("1412", "Psykoterapi, vuxna");
        longKodToName.put("1413", "Sjukgymnastik");
        longKodToName.put("1500", "Akutverksamhet, primärvårdsjour");
        longKodToName.put("1502", "Allmänmedicin");
        longKodToName.put("1504", "Barnhälsovård/BVC");
        longKodToName.put("1505", "Barnmorskemottagning");
        longKodToName.put("1506", "Distriktssköterskeverksamhet");
        longKodToName.put("1508", "Företagshälsovård");
        longKodToName.put("1509", "Hälso- och sjukvård i särskilt boende");
        longKodToName.put("1512", "Krigs- och tortyrskadevård");
        longKodToName.put("1516", "Primärvårdsrehabilitering");
        longKodToName.put("1517", "Skolhälsovård");
        longKodToName.put("1519", "Ungdomsmottagning");
        longKodToName.put("1600", "Akutverksamhet vid sjukhus, vuxenpsykiatri");
        longKodToName.put("1603", "Alkoholsjukvård");
        longKodToName.put("1604", "Allmänpsykiatri, vuxna");
        longKodToName.put("1605", "Dövpsykiatri");
        longKodToName.put("1606", "Familjebehandlingsverksamhet");
        longKodToName.put("1607", "Geropsykiatri");
        longKodToName.put("1609", "Narkomanisjukvård ");
        longKodToName.put("1614", "Rättspsykiatrisk vård");
        longKodToName.put("1615", "Rättspsykiatriska undersökningar, verksamhet");
        longKodToName.put("1618", "Läkemedelsberoendevård");
        longKodToName.put("1619", "Psykosvård");
        longKodToName.put("1620", "Akutverksamhet, psykiatriska jourteam");
        longKodToName.put("1621", "Affektiva sjukdomar, verksamhet");
        longKodToName.put("1622", "Spelberoendevård ");
        longKodToName.put("1623", "Neuropsykiatri, vuxna");
        longKodToName.put("1624", "Beroendevård");
        longKodToName.put("1625", "Ätstörningsvård, vuxna");
        longKodToName.put("1626", "Psykiatri, vuxna");
        longKodToName.put("1702", "Datortomografiverksamhet");
        longKodToName.put("1706", "MR-verksamhet");
        longKodToName.put("1707", "Mammografi");
        longKodToName.put("1708", "Bild- och funktionsmedicin");
        longKodToName.put("1709", "Nuklearmedicin");
        longKodToName.put("1710", "Neuroradiologi");
        longKodToName.put("1715", "Strålbehandlingsverksamhet");
        longKodToName.put("1720", "PET-verksamhet");
        longKodToName.put("1721", "Ultraljudsverksamhet");
        longKodToName.put("1801", "Akutverksamhet, allmäntandvård");
        longKodToName.put("1802", "Allmäntandvård");
        longKodToName.put("1803", "Tandhygienistverksamhet");
        longKodToName.put("1804", "Pedodonti");
        longKodToName.put("1805", "Bettfysiologi");
        longKodToName.put("1806", "Endodonti");
        longKodToName.put("1807", "Käkkirurgi/Oral kirurgi");
        longKodToName.put("1808", "Odontologisk radiologi");
        longKodToName.put("1809", "Oral protetik");
        longKodToName.put("1810", "Ortodonti");
        longKodToName.put("1811", "Parodontologi");
        longKodToName.put("1812", "Akutverksamhet vid sjukhus, käkkirurgi");
        longKodToName.put("1813", "Sjukhustandvård/Oral medicin");
        longKodToName.put("2003", "Fotvård");
        longKodToName.put("2004", "Giftinformationsverksamhet");
        longKodToName.put("2005", "Hjälpmedelsverksamhet");
        longKodToName.put("2006", "Hörselcentral");
        longKodToName.put("2008", "Medicinsk strålningsfysik");
        longKodToName.put("2009", "Medicinteknisk verksamhet");
        longKodToName.put("2011", "Ortopedteknisk verksamhet");
        longKodToName.put("2012", "Perukmakarverksamhet");
        longKodToName.put("2014", "Sjuktransportverksamhet");
        longKodToName.put("2015", "Sjukvårdsrådgivning");
        longKodToName.put("2016", "Sterilisering, instrument");
        longKodToName.put("2017", "Syncentral");
        longKodToName.put("2021", "Vårdhygienverksamhet");
        longKodToName.put("2022", "Öppenvårdsapotek");
        longKodToName.put("2023", "Sjukhusapotek");
        longKodToName.put("2024", "Vaccinationsverksamhet");
        longKodToName.put("2025", "Ambulanstransportverksamhet ");
        longKodToName.put("2026", "Smittskyddsverksamhet");
        longKodToName.put("2027", "Medicinsk hyperbarverksamhet");
        longKodToName.put("2300", "Äldreboende, verksamhet");
        longKodToName.put("2301", "Gruppbostad, personer med demenssjukdom, verksamhet");
        longKodToName.put("2302", "Gruppbostad, personer m. psyk. funktionsneds., verksamhet");
        longKodToName.put("2303", "Gruppbostad, vuxna m. fys. funktionsneds., verksamhet");
        longKodToName.put("2304", "Stödboende, verksamhet");
        longKodToName.put("2305", "Skyddat boende, verksamhet");
        longKodToName.put("2306", "Korttidsvård");
        longKodToName.put("2307", "Hem för vård el. boende, vuxna m. missbruks- el. beroendeproblem");
        longKodToName.put("2308", "Hem för vård el. boende, personer m. psyk. funktionsneds.");
        longKodToName.put("2309", "LVM-hem, verksamhet");
        longKodToName.put("2310", "Servicebostad, verksamhet");
        longKodToName.put("2311", "Dagverksamhet");
        longKodToName.put("2312", "Öppen verksamhet");
        longKodToName.put("2313", "Daglig verksamhet");
        longKodToName.put("2314", "Daglig sysselsättning");
        longKodToName.put("2315", "Personlig assistans");
        longKodToName.put("2316", "Avlösarservice i hemmet");
        longKodToName.put("2317", "Ledsagarservice");
        longKodToName.put("2318", "Hemtjänst, omvårdnadsverksamhet");
        longKodToName.put("2319", "Hemtjänst, serviceverksamhet");
        longKodToName.put("2320", "Boendestöd");
        longKodToName.put("2321", "Familjerådgivning");
        longKodToName.put("2322", "Familjecentral, verksamhet");
        longKodToName.put("2323", "Familjerätt, verksamhet");
        longKodToName.put("2324", "Personligt ombud, verksamhet");
        longKodToName.put("2325", "Anhörigstöd");
        longKodToName.put("2326", "Stöd till brottsoffer");
        longKodToName.put("2327", "Stöd till våldutövare");
        longKodToName.put("2328", "Praktikplatser och kompetenshöjande insatser, verksamhet");
        longKodToName.put("2329", "Myndighetsutövning inom äldreomsorg");
        longKodToName.put("2330", "Myndighetsutövning inom individ- och familjeomsorg");
        longKodToName.put("2331", "Myndighetsutövning inom området personer med funktionsneds.");
    }
    // CHECKSTYLE:ON MethodLength

    public String getGruppId(String id) {
        return (id != null && id.length() >= 2) ? id.substring(0, 2) : OVRIGT_ID;
    }

    public String getNamn(String kod) {
        String verksamhet = kodToName.get(kod);
        return verksamhet != null ? verksamhet : OVRIGT;
    }

    @Override
    public Iterator<String> iterator() {
        return longKodToName.keySet().iterator();
    }

}
