package se.inera.statistics.web.model;

import java.io.Serializable;

import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

public class Verksamhet implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;

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
        CharSequenceTranslator escaper = UnicodeEscaper.below('0').with(UnicodeEscaper.between(':', '@'), UnicodeEscaper.between('[', '`'), UnicodeEscaper.above('z'));
        return escaper.translate(id);
    }

    public static String decodeId(String encodedId) {
        return new UnicodeUnescaper().translate(encodedId);
    }

}
