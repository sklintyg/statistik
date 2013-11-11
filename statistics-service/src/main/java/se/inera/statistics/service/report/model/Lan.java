package se.inera.statistics.service.report.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lan implements Iterable<String> {

    public static final String OVRIGT = "Okänt län";
    private final Map<String, String> kodToName = new LinkedHashMap<>();

    public Lan() {
        kodToName.put("10", "Blekinge län");
        kodToName.put("20", "Dalarnas län");
        kodToName.put("13", "Hallands län");
        kodToName.put("08", "Kalmar län");
        kodToName.put("07", "Kronobergs län");
        kodToName.put("09", "Gotlands län");
        kodToName.put("21", "Gävleborgs län");
        kodToName.put("23", "Jämtlands län");
        kodToName.put("06", "Jönköpings län");
        kodToName.put("25", "Norrbottens län");
        kodToName.put("12", "Skåne län");
        kodToName.put("01", "Stockholms län");
        kodToName.put("04", "Södermanlands län");
        kodToName.put("03", "Uppsala län");
        kodToName.put("17", "Värmlands län");
        kodToName.put("24", "Västerbottens län");
        kodToName.put("22", "Västernorrlands län");
        kodToName.put("19", "Västmanlands län");
        kodToName.put("14", "Västra Götalands län");
        kodToName.put("18", "Örebro län");
        kodToName.put("05", "Östergötlands län");
        kodToName.put(OVRIGT, OVRIGT);
    }

    public String getNamn(String kod) {
        String lan = kodToName.get(kod);
        return lan != null ? lan : OVRIGT;
    }

    @Override
    public Iterator<String> iterator() {
        return kodToName.keySet().iterator();
    }
}
