package se.inera.statistics.tool;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class AnonymizeDbDump {

    private static final String INTYGMARKER = "INSERT INTO `intyghandelse`";
    public static final int END_OF_BIRTHDATE = 8;
    public static final int DAY_INDEX = 6;
    private static final int SAMORDNING_OFFSET = 6;
    private  final Random random = new Random();

    private  final Map<String, String> actualToAnonymized = new HashMap<>();
    private  final Set<String> anonymizedSet = new HashSet<>();
    public static final String PATIENT = fieldify("patient");
    public static final String EXTENSION = fieldify("extension");
    public static final List<String> XIFY_FIELDS = Arrays.asList("efternamn", "fullstandigtNamn", "beskrivning", "typAvArbetsuppgift", "kommentarer");
    public static final int BIRTHDAY_RANGE = 1000;
    public static final int SEX_INDEX = 11;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0])), "utf-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(args[1])), "utf-8"));
        String line;

        AnonymizeDbDump anonymizer = new AnonymizeDbDump();
        while ((line = reader.readLine()) != null) {
            writer.append(anonymizer.anonymize(line));
            writer.append('\n');
        }
        writer.close();
    }

    private AnonymizeDbDump() {
    }

    /**
     * @param line text to anonymized (as needed).
     * @return Anonymized line

     */
    private CharSequence anonymize(String line) {
        if (needAnonymization(line)) {
            StringBuilder buffer = new StringBuilder(line);
            anonymizePatientid(buffer);
            xify(buffer);
            return buffer;
        } else {
            return line;
        }
    }

    private static void xify(StringBuilder buffer) {
        for (String field : XIFY_FIELDS) {
            xify(field, buffer);
            xifyArray(field, buffer);
        }
    }

    private static void xify(String field, StringBuilder buffer) {
        int index = -1;
        String actual = fieldify(field);
        while ((index = buffer.indexOf(actual, index + 1)) > -1) {
            int start = index + actual.length();
            int end = findEnd(buffer, start);
            xify(buffer, start, end);
        }
    }

    private static void xifyArray(String field, StringBuilder buffer) {
        int index = -1;
        String actual = arrayify(field);
        while ((index = buffer.indexOf(actual, index + 1)) > -1) {
            int start = index + actual.length();
            int end = findEnd(buffer, start);
            xify(buffer, start, end);
        }
    }

    private static void xify(StringBuilder buffer, int start, int end) {
        boolean skipNext = false;
        for (int i = start; i < end; i++) {
            if (buffer.charAt(i) == '\\') {
                i++;
                skipNext = buffer.charAt(i) == '\\';
            } else {
                if (!skipNext) {
                    buffer.setCharAt(i, 'x');
                }
                skipNext = false;
            }
        }
    }

    private void anonymizePatientid(StringBuilder buffer) {
        int start = -1;
        while ((start = buffer.indexOf(PATIENT, start + 1)) > -1) {
            int startIndex = buffer.indexOf(EXTENSION, start) + EXTENSION.length();
            int endIndex = findEnd(buffer, start);
            String id = buffer.substring(startIndex, endIndex);
            String anonymized = actualToAnonymized.get(id);
            if (anonymized == null) {
                anonymized = getUniqueRandomPersonid(id);
            }
            buffer.replace(startIndex, endIndex, anonymized);
        }
    }

    private String getUniqueRandomPersonid(String nummer) {
        String anonymized;
        do {
            anonymized = newRandomPersonid(nummer);
        } while (anonymizedSet.contains(anonymized));
        anonymizedSet.add(anonymized);
        actualToAnonymized.put(nummer, anonymized);
        return anonymized;
    }

    //CHECKSTYLE:OFF MagicNumber
    private String newRandomPersonid(String nummer) {
        LocalDate date;
        boolean samordning = false;
        try {
            date = ISODateTimeFormat.basicDate().parseLocalDate(nummer.substring(0, END_OF_BIRTHDATE));
        } catch (Exception e) {
            StringBuilder b = new StringBuilder(nummer.substring(0, END_OF_BIRTHDATE));
            b.setCharAt(DAY_INDEX, (char) (b.charAt(DAY_INDEX) - SAMORDNING_OFFSET));
            try {
                date = ISODateTimeFormat.basicDate().parseLocalDate(b.toString());
                samordning = true;
            } catch (Exception ee) {
                System.err.println("Unrecognized personid " + nummer);
                return nummer;
            }
        }
        date = date.plusDays(random.nextInt(BIRTHDAY_RANGE) - BIRTHDAY_RANGE / 2);
        int extension = random.nextInt(9989);
        // Fix sex if needed
        if (((nummer.charAt(SEX_INDEX) - '0') & 1) != ((extension / 10) & 1)) {
            extension += 10;
        }
        String prefix = date.toString("yyyyMMdd");
        // Make samordning if needed
        if (samordning) {
            StringBuilder b = new StringBuilder(prefix);
            b.setCharAt(DAY_INDEX, (char) (prefix.charAt(DAY_INDEX) + DAY_INDEX));
            prefix = b.toString();
        }

        return prefix + String.format("-%1$04d", extension);
    }
    //CHECKSTYLE:ON MagicNumber

    /**
     * @param field name of filed
     * @return name with prefix and suffix as it will look in the line
     */
    private static String fieldify(String field) {
        return "\\\"" + field + "\\\":\\\"";
    }

    private static String arrayify(String field) {
        return "\\\"" + field + "\\\":[\\\"";
    }

    private static int findEnd(StringBuilder buffer, int start) {
        int end = start;
        boolean escape = false;
        while (escape || !(buffer.charAt(end) == '\\' && buffer.charAt(end + 1) == '"')) {
            escape = false;
            if (buffer.charAt(end) == '\\') {
                end++;
                if (buffer.charAt(end) == '\\') {
                    escape = true;
                }
            }
            end++;
        }
        return end;
    }

    private static boolean needAnonymization(String line) {
        return line.startsWith(INTYGMARKER);
    }
}
