package se.inera.statistics.web.model;

import static org.apache.commons.lang3.text.translate.UnicodeEscaper.above;
import static org.apache.commons.lang3.text.translate.UnicodeEscaper.between;

import java.io.Serializable;

import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

public class Verksamhet implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private static final CharSequenceTranslator ESCAPER = UnicodeEscaper.below('-').with(excludeBetween('-', '0'), excludeBetween('9', 'A'), excludeBetween('Z', 'a'), above('z'));

    private static  UnicodeEscaper excludeBetween(int codepointLow, int codepointHigh) {
        return between(codepointLow + 1, codepointHigh - 1);
    }

    public Verksamhet(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return encodeId(id);
    }

    public String getName() {
        return name;
    }

    public static String encodeId(String id) {
        return ESCAPER .translate(id).replace('\\', '_');
    }

    public static String decodeId(String encodedId) {
        return new UnicodeUnescaper().translate(encodedId.replace('_', '\\'));
    }

}
