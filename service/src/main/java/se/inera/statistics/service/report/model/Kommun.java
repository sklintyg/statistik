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
package se.inera.statistics.service.report.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Kommun implements Iterable<String> {

    public static final String OVRIGT = "Okänd kommun";
    public static final String OVRIGT_ID = "0000";
    private final Map<String, String> kodToName = new LinkedHashMap<>();

    // CHECKSTYLE:OFF MethodLength
    public Kommun() {
        kodToName.put(OVRIGT_ID, OVRIGT);
        kodToName.put("0114", "Upplands Väsby");
        kodToName.put("0115", "Vallentuna");
        kodToName.put("0117", "Österåker");
        kodToName.put("0120", "Värmdö");
        kodToName.put("0123", "Järfälla");
        kodToName.put("0125", "Ekerö");
        kodToName.put("0126", "Huddinge");
        kodToName.put("0127", "Botkyrka");
        kodToName.put("0128", "Salem");
        kodToName.put("0136", "Haninge");
        kodToName.put("0138", "Tyresö");
        kodToName.put("0139", "Upplands-Bro");
        kodToName.put("0140", "Nykvarn");
        kodToName.put("0160", "Täby");
        kodToName.put("0162", "Danderyd");
        kodToName.put("0163", "Sollentuna");
        kodToName.put("0180", "Stockholm");
        kodToName.put("0181", "Södertälje");
        kodToName.put("0182", "Nacka");
        kodToName.put("0183", "Sundbyberg");
        kodToName.put("0184", "Solna");
        kodToName.put("0186", "Lidingö");
        kodToName.put("0187", "Vaxholm");
        kodToName.put("0188", "Norrtälje");
        kodToName.put("0191", "Sigtuna");
        kodToName.put("0192", "Nynäshamn");
        kodToName.put("0305", "Håbo");
        kodToName.put("0319", "Älvkarleby");
        kodToName.put("0330", "Knivsta");
        kodToName.put("0331", "Heby");
        kodToName.put("0360", "Tierp");
        kodToName.put("0380", "Uppsala");
        kodToName.put("0381", "Enköping");
        kodToName.put("0382", "Östhammar");
        kodToName.put("0428", "Vingåker");
        kodToName.put("0461", "Gnesta");
        kodToName.put("0480", "Nyköping");
        kodToName.put("0481", "Oxelösund");
        kodToName.put("0482", "Flen");
        kodToName.put("0483", "Katrineholm");
        kodToName.put("0484", "Eskilstuna");
        kodToName.put("0486", "Strängnäs");
        kodToName.put("0488", "Trosa");
        kodToName.put("0509", "Ödeshög");
        kodToName.put("0512", "Ydre");
        kodToName.put("0513", "Kinda");
        kodToName.put("0560", "Boxholm");
        kodToName.put("0561", "Åtvidaberg");
        kodToName.put("0562", "Finspång");
        kodToName.put("0563", "Valdemarsvik");
        kodToName.put("0580", "Linköping");
        kodToName.put("0581", "Norrköping");
        kodToName.put("0582", "Söderköping");
        kodToName.put("0583", "Motala");
        kodToName.put("0584", "Vadstena");
        kodToName.put("0586", "Mjölby");
        kodToName.put("0604", "Aneby");
        kodToName.put("0617", "Gnosjö");
        kodToName.put("0642", "Mullsjö");
        kodToName.put("0643", "Habo");
        kodToName.put("0662", "Gislaved");
        kodToName.put("0665", "Vaggeryd");
        kodToName.put("0680", "Jönköping");
        kodToName.put("0682", "Nässjö");
        kodToName.put("0683", "Värnamo");
        kodToName.put("0684", "Sävsjö");
        kodToName.put("0685", "Vetlanda");
        kodToName.put("0686", "Eksjö");
        kodToName.put("0687", "Tranås");
        kodToName.put("0760", "Uppvidinge");
        kodToName.put("0761", "Lessebo");
        kodToName.put("0763", "Tingsryd");
        kodToName.put("0764", "Alvesta");
        kodToName.put("0765", "Älmhult");
        kodToName.put("0767", "Markaryd");
        kodToName.put("0780", "Växjö");
        kodToName.put("0781", "Ljungby");
        kodToName.put("0821", "Högsby");
        kodToName.put("0834", "Torsås");
        kodToName.put("0840", "Mörbylånga");
        kodToName.put("0860", "Hultsfred");
        kodToName.put("0861", "Mönsterås");
        kodToName.put("0862", "Emmaboda");
        kodToName.put("0880", "Kalmar");
        kodToName.put("0881", "Nybro");
        kodToName.put("0882", "Oskarshamn");
        kodToName.put("0883", "Västervik");
        kodToName.put("0884", "Vimmerby");
        kodToName.put("0885", "Borgholm");
        kodToName.put("0980", "Gotland");
        kodToName.put("1060", "Olofström");
        kodToName.put("1080", "Karlskrona");
        kodToName.put("1081", "Ronneby");
        kodToName.put("1082", "Karlshamn");
        kodToName.put("1083", "Sölvesborg");
        kodToName.put("1214", "Svalöv");
        kodToName.put("1230", "Staffanstorp");
        kodToName.put("1231", "Burlöv");
        kodToName.put("1233", "Vellinge");
        kodToName.put("1256", "Östra Göinge");
        kodToName.put("1257", "Örkelljunga");
        kodToName.put("1260", "Bjuv");
        kodToName.put("1261", "Kävlinge");
        kodToName.put("1262", "Lomma");
        kodToName.put("1263", "Svedala");
        kodToName.put("1264", "Skurup");
        kodToName.put("1265", "Sjöbo");
        kodToName.put("1266", "Hörby");
        kodToName.put("1267", "Höör");
        kodToName.put("1270", "Tomelilla");
        kodToName.put("1272", "Bromölla");
        kodToName.put("1273", "Osby");
        kodToName.put("1275", "Perstorp");
        kodToName.put("1276", "Klippan");
        kodToName.put("1277", "Åstorp");
        kodToName.put("1278", "Båstad");
        kodToName.put("1280", "Malmö");
        kodToName.put("1281", "Lund");
        kodToName.put("1282", "Landskrona");
        kodToName.put("1283", "Helsingborg");
        kodToName.put("1284", "Höganäs");
        kodToName.put("1285", "Eslöv");
        kodToName.put("1286", "Ystad");
        kodToName.put("1287", "Trelleborg");
        kodToName.put("1290", "Kristianstad");
        kodToName.put("1291", "Simrishamn");
        kodToName.put("1292", "Ängelholm");
        kodToName.put("1293", "Hässleholm");
        kodToName.put("1315", "Hylte");
        kodToName.put("1380", "Halmstad");
        kodToName.put("1381", "Laholm");
        kodToName.put("1382", "Falkenberg");
        kodToName.put("1383", "Varberg");
        kodToName.put("1384", "Kungsbacka");
        kodToName.put("1401", "Härryda");
        kodToName.put("1402", "Partille");
        kodToName.put("1407", "Öckerö");
        kodToName.put("1415", "Stenungsund");
        kodToName.put("1419", "Tjörn");
        kodToName.put("1421", "Orust");
        kodToName.put("1427", "Sotenäs");
        kodToName.put("1430", "Munkedal");
        kodToName.put("1435", "Tanum");
        kodToName.put("1438", "Dals-Ed");
        kodToName.put("1439", "Färgelanda");
        kodToName.put("1440", "Ale");
        kodToName.put("1441", "Lerum");
        kodToName.put("1442", "Vårgårda");
        kodToName.put("1443", "Bollebygd");
        kodToName.put("1444", "Grästorp");
        kodToName.put("1445", "Essunga");
        kodToName.put("1446", "Karlsborg");
        kodToName.put("1447", "Gullspång");
        kodToName.put("1452", "Tranemo");
        kodToName.put("1460", "Bengtsfors");
        kodToName.put("1461", "Mellerud");
        kodToName.put("1462", "Lilla Edet");
        kodToName.put("1463", "Mark");
        kodToName.put("1465", "Svenljunga");
        kodToName.put("1466", "Herrljunga");
        kodToName.put("1470", "Vara");
        kodToName.put("1471", "Götene");
        kodToName.put("1472", "Tibro");
        kodToName.put("1473", "Töreboda");
        kodToName.put("1480", "Göteborg");
        kodToName.put("1481", "Mölndal");
        kodToName.put("1482", "Kungälv");
        kodToName.put("1484", "Lysekil");
        kodToName.put("1485", "Uddevalla");
        kodToName.put("1486", "Strömstad");
        kodToName.put("1487", "Vänersborg");
        kodToName.put("1488", "Trollhättan");
        kodToName.put("1489", "Alingsås");
        kodToName.put("1490", "Borås");
        kodToName.put("1491", "Ulricehamn");
        kodToName.put("1492", "Åmål");
        kodToName.put("1493", "Mariestad");
        kodToName.put("1494", "Lidköping");
        kodToName.put("1495", "Skara");
        kodToName.put("1496", "Skövde");
        kodToName.put("1497", "Hjo");
        kodToName.put("1498", "Tidaholm");
        kodToName.put("1499", "Falköping");
        kodToName.put("1715", "Kil");
        kodToName.put("1730", "Eda");
        kodToName.put("1737", "Torsby");
        kodToName.put("1760", "Storfors");
        kodToName.put("1761", "Hammarö");
        kodToName.put("1762", "Munkfors");
        kodToName.put("1763", "Forshaga");
        kodToName.put("1764", "Grums");
        kodToName.put("1765", "Årjäng");
        kodToName.put("1766", "Sunne");
        kodToName.put("1780", "Karlstad");
        kodToName.put("1781", "Kristinehamn");
        kodToName.put("1782", "Filipstad");
        kodToName.put("1783", "Hagfors");
        kodToName.put("1784", "Arvika");
        kodToName.put("1785", "Säffle");
        kodToName.put("1814", "Lekeberg");
        kodToName.put("1860", "Laxå");
        kodToName.put("1861", "Hallsberg");
        kodToName.put("1862", "Degerfors");
        kodToName.put("1863", "Hällefors");
        kodToName.put("1864", "Ljusnarsberg");
        kodToName.put("1880", "Örebro");
        kodToName.put("1881", "Kumla");
        kodToName.put("1882", "Askersund");
        kodToName.put("1883", "Karlskoga");
        kodToName.put("1884", "Nora");
        kodToName.put("1885", "Lindesberg");
        kodToName.put("1904", "Skinnskatteberg");
        kodToName.put("1907", "Surahammar");
        kodToName.put("1960", "Kungsör");
        kodToName.put("1961", "Hallstahammar");
        kodToName.put("1962", "Norberg");
        kodToName.put("1980", "Västerås");
        kodToName.put("1981", "Sala");
        kodToName.put("1982", "Fagersta");
        kodToName.put("1983", "Köping");
        kodToName.put("1984", "Arboga");
        kodToName.put("2021", "Vansbro");
        kodToName.put("2023", "Malung-Sälen");
        kodToName.put("2026", "Gagnef");
        kodToName.put("2029", "Leksand");
        kodToName.put("2031", "Rättvik");
        kodToName.put("2034", "Orsa");
        kodToName.put("2039", "Älvdalen");
        kodToName.put("2061", "Smedjebacken");
        kodToName.put("2062", "Mora");
        kodToName.put("2080", "Falun");
        kodToName.put("2081", "Borlänge");
        kodToName.put("2082", "Säter");
        kodToName.put("2083", "Hedemora");
        kodToName.put("2084", "Avesta");
        kodToName.put("2085", "Ludvika");
        kodToName.put("2101", "Ockelbo");
        kodToName.put("2104", "Hofors");
        kodToName.put("2121", "Ovanåker");
        kodToName.put("2132", "Nordanstig");
        kodToName.put("2161", "Ljusdal");
        kodToName.put("2180", "Gävle");
        kodToName.put("2181", "Sandviken");
        kodToName.put("2182", "Söderhamn");
        kodToName.put("2183", "Bollnäs");
        kodToName.put("2184", "Hudiksvall");
        kodToName.put("2260", "Ånge");
        kodToName.put("2262", "Timrå");
        kodToName.put("2280", "Härnösand");
        kodToName.put("2281", "Sundsvall");
        kodToName.put("2282", "Kramfors");
        kodToName.put("2283", "Sollefteå");
        kodToName.put("2284", "Örnsköldsvik");
        kodToName.put("2303", "Ragunda");
        kodToName.put("2305", "Bräcke");
        kodToName.put("2309", "Krokom");
        kodToName.put("2313", "Strömsund");
        kodToName.put("2321", "Åre");
        kodToName.put("2326", "Berg");
        kodToName.put("2361", "Härjedalen");
        kodToName.put("2380", "Östersund");
        kodToName.put("2401", "Nordmaling");
        kodToName.put("2403", "Bjurholm");
        kodToName.put("2404", "Vindeln");
        kodToName.put("2409", "Robertsfors");
        kodToName.put("2417", "Norsjö");
        kodToName.put("2418", "Malå");
        kodToName.put("2421", "Storuman");
        kodToName.put("2422", "Sorsele");
        kodToName.put("2425", "Dorotea");
        kodToName.put("2460", "Vännäs");
        kodToName.put("2462", "Vilhelmina");
        kodToName.put("2463", "Åsele");
        kodToName.put("2480", "Umeå");
        kodToName.put("2481", "Lycksele");
        kodToName.put("2482", "Skellefteå");
        kodToName.put("2505", "Arvidsjaur");
        kodToName.put("2506", "Arjeplog");
        kodToName.put("2510", "Jokkmokk");
        kodToName.put("2513", "Överkalix");
        kodToName.put("2514", "Kalix");
        kodToName.put("2518", "Övertorneå");
        kodToName.put("2521", "Pajala");
        kodToName.put("2523", "Gällivare");
        kodToName.put("2560", "Älvsbyn");
        kodToName.put("2580", "Luleå");
        kodToName.put("2581", "Piteå");
        kodToName.put("2582", "Boden");
        kodToName.put("2583", "Haparanda");
        kodToName.put("2584", "Kiruna");
    }
    // CHECKSTYLE:ON MethodLength

    public String getNamn(String kod) {
        String kommun = kodToName.get(kod);
        return kommun != null ? kommun : OVRIGT;
    }

    @Override
    public Iterator<String> iterator() {
        return kodToName.keySet().iterator();
    }

}
