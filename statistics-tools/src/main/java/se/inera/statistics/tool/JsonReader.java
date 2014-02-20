package se.inera.statistics.tool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonReader {

    public static LocalDate BASE = new LocalDate("1900-01-01");
    public static DateTimeFormatter PERSONNUMMER = DateTimeFormat.forPattern("yyyyMMdd'");
    private static StringBuilder builder;

    public static void main(String[] args) throws IOException {
        try (OutputStream out = new GZIPOutputStream(new FileOutputStream("intyg.csv.gz"))) {
            try (InputStream in = new GZIPInputStream(new FileInputStream("intyg.json.gz"))) {

                ObjectMapper mapper = new ObjectMapper();
                JsonParser parser = mapper.getFactory().createJsonParser(in);

                ObjectReader reader = mapper.reader();

                JsonNode node;
                int id = 0;
                while ((node = reader.readTree(parser)) != null) {
                    id++;
                    int lakare = Integer.parseInt(node.get("skapadAv").get("id").get("extension").textValue().substring(7));
                    int enhet = Integer.parseInt(node.get("skapadAv").get("vardenhet").get("id").get("extension").textValue().substring(6));
                    int vardgivare = Integer.parseInt(node.get("skapadAv").get("vardenhet").get("vardgivare").get("id").get("extension").textValue().substring(11));

                    String personid = node.get("patient").get("id").get("extension").textValue();

                    String icd10 = filter(node.get("observationer"), "ICD-10", "observationskod", "codeSystemName").get("observationskod").get("code").textValue();
                    JsonNode nedsattning = filter(node.get("observationer"), "302119000", "observationskod", "code");
                    LocalDate from = new LocalDate(nedsattning.get("observationsperiod").get("from").textValue());
                    LocalDate tom = new LocalDate(nedsattning.get("observationsperiod").get("tom").textValue());
                    int sjukskrivningsgrad = 100 - nedsattning.get("varde").get(0).get("quantity").asInt();
                    int kon = 1 + ((personid.charAt(12)) & 1);

                    int alder = Years.yearsBetween(LocalDate.parse(personid.substring(0, 8), PERSONNUMMER), from).getYears();
                    start();
                    add(id);
                    add(personToInt(personid));
                    add(alder);
                    add(icd10);
                    add(kon);
                    add(sjukskrivningsgrad);
                    add(Days.daysBetween(BASE, from).getDays());
                    add(Days.daysBetween(from, tom).getDays() + 1);
                    add(lakare);
                    add(enhet);
                    add(vardgivare);
                    add(enhet%290);
                    end();
                    out.write(builder.toString().getBytes());
                }
            }
            out.close();
        }
    }

    private static void end() {
        builder.append('\n');
    }

    private static int personToInt(String personid) {
        // 19750719-4038
        int factor = 1;
        int v = personid.charAt(11) - '0';
        factor *= 10;
        v += (personid.charAt(10) - 'A') * factor;
        factor *= 26;
        v += (personid.charAt(9) - 'A') * factor;
        factor *= 26;
        v += Integer.parseInt(personid.substring(6, 8)) * factor;
        factor *= 32;
        v += Integer.parseInt(personid.substring(4, 6)) * factor;
        factor *= 13;
        v += (Integer.parseInt(personid.substring(0, 2)) - 19) * factor;
        return v;
    }

    private static void add(String value) {
        comma();
        builder.append(value);
    }

    private static void add(int value) {
        comma();
        builder.append(value);
    }

    private static void comma() {
        if (builder.length() > 0) {
            builder.append(',');
        }
    }

    private static void start() {
        builder = new StringBuilder(300);
    }

    private static JsonNode filter(JsonNode array, String pattern, String... path) {
        Iterator<JsonNode> nodes = array.elements();
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            JsonNode matcher = node;
            for (String element : path) {
                matcher = matcher.path(element);
            }
            if (pattern.equals(matcher.textValue())) {
                return node;
            }
        }
        return null;
    }
}
