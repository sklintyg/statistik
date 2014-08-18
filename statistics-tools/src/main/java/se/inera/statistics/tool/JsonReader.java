package se.inera.statistics.tool;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

public final class JsonReader {

    public static final LocalDate BASE = new LocalDate("1900-01-01");
    public static final DateTimeFormatter PERSONNUMMER = DateTimeFormat.forPattern("yyyyMMdd'");
    public static final String ENCODING = "UTF-8";
    private static StringBuilder builder;

    private JsonReader() {
    }
    // CHECKSTYLE:OFF MagicNumber
    public static void main(String[] args) throws IOException {
        try (OutputStream out = new GZIPOutputStream(new FileOutputStream("intyg.csv.gz"))) {
            try (InputStream in = new GZIPInputStream(new FileInputStream("intyg.json.gz"))) {

                Sjukfall sjukfall = new Sjukfall();
                ObjectMapper mapper = new ObjectMapper();
                JsonParser parser = mapper.getFactory().createJsonParser(in);

                ObjectReader reader = mapper.reader();

                JsonNode node;
                int id = 0;
                int pruneLimit = 0;
                while ((node = reader.readTree(parser)) != null) {
                    id++;
                    int lakare = Integer.parseInt(node.get("skapadAv").get("id").get("extension").textValue().substring(7));
                    int enhet = Integer.parseInt(node.get("skapadAv").get("vardenhet").get("id").get("extension").textValue().substring(6));
                    int vardgivare = Integer.parseInt(node.get("skapadAv").get("vardenhet").get("vardgivare").get("id").get("extension").textValue().substring(11));

                    String personnummer = node.get("patient").get("id").get("extension").textValue();

                    String icd10 = filter(node.get("observationer"), "ICD-10", "observationskod", "codeSystemName").get("observationskod").get("code").textValue();
                    JsonNode nedsattning = filter(node.get("observationer"), "302119000", "observationskod", "code");
                    LocalDate fromDate = new LocalDate(nedsattning.get("observationsperiod").get("from").textValue());
                    int from = day(fromDate);
                    int tom = day(new LocalDate(nedsattning.get("observationsperiod").get("tom").textValue()));
                    int sjukskrivningsgrad = 100 - nedsattning.get("varde").get(0).get("quantity").asInt();
                    int kon = 1 + ((personnummer.charAt(12)) & 1);
                    int alder = Years.yearsBetween(LocalDate.parse(personnummer.substring(0, 8), PERSONNUMMER), fromDate).getYears();
                    start();
                    add(id);
                    int personid = personToInt(personnummer);
                    add(personid);
                    add(alder);
                    add(icd10);
                    add(kon);
                    add(sjukskrivningsgrad);
                    add(from);
                    add(tom - from + 1);
                    add(lakare);
                    add(enhet);
                    add(vardgivare);
                    add(enhet % 290);
                    add(sjukfall.getUpsertedId(personid, vardgivare, from, tom));
                    end();
                    out.write(builder.toString().getBytes(ENCODING));
                    if (pruneLimit != from) {
                        pruneLimit = from;
                        int pruned = sjukfall.prune(pruneLimit);
                        System.err.println("Pruned " + pruneLimit + " " + pruned);
                    }
                }
            }
            out.close();
        }
    }

    private static int day(LocalDate date) {
        return Days.daysBetween(BASE, date).getDays();
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

    private static class Sjukfall {
        private static final int FALL_GLAPP = 5;
        private Map<Key, Fall> known = new HashMap<>();
        public int getUpsertedId(int personid, int vardgivare, int from, int tom) {
            Key key = new Key(personid, vardgivare);
            Fall fall = known.get(key);
            if (fall == null) {
                fall = new Fall(tom);
                known.put(key, fall);
            } else {
                if (from <= fall.tom + FALL_GLAPP) {
                    if (fall.tom < tom) {
                        fall.tom = tom;
                    }
                } else {
                    fall = new Fall(tom);
                    known.put(key, fall);
                }
            }
            return fall.id;
        }
        public int prune(int today) {
            int limit = today - 6;
            int pruned = 0;
            Iterator<Entry<Key, Fall>> iterator = known.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Key, Fall> entry = iterator.next();
                if (entry.getValue().tom < limit) {
                    iterator.remove();
                    pruned++;
                }
            }
            return pruned;
        }
    }
    private static class Fall {
        private static int count = 0;
        private int id;
        private int tom;
        public Fall(int tom) {
            this.tom = tom;
            id = ++count;
        }
    }
    public static class Key {

        private int personid;
        private int vardgivare;

        public Key(int personid, int vardgivare) {
            this.personid = personid;
            this.vardgivare = vardgivare;
        }

        @Override
        public int hashCode() {
            return personid + vardgivare;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key other = (Key) obj;
                return personid == other.personid && vardgivare == other.vardgivare;
            } else {
                return false;
            }
        }
    }
    // CHECKSTYLE:ON MagicNumber
}
