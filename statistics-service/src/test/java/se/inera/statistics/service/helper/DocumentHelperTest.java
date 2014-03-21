package se.inera.statistics.service.helper;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.LocalDate;
import org.junit.Test;
import se.inera.statistics.service.JSONSource;

import static org.junit.Assert.assertEquals;

public class DocumentHelperTest {

    private JsonNode document = JSONParser.parse(JSONSource.readTemplateAsString());
    // CHECKSTYLE:OFF MagicNumber
    @Test
    public void get_enhet() {
        assertEquals("enhetId", DocumentHelper.getEnhetId(document));
    }

    @Test
    public void get_diagnos() {
        assertEquals("H81", DocumentHelper.getDiagnos(document));
    }

    @Test
    public void get_age() {
        assertEquals(33, DocumentHelper.getAge(DocumentHelper.anonymize(document)));
    }

    @Test
    public void processor_extract_alder_from_intyg() {
        String personId = "19121212-1212";
        LocalDate date = new LocalDate(0L); // 1970

        int alder = DocumentHelper.extractAlder(personId, date);

        assertEquals(57, alder);
    }

    @Test
    public void processor_extract_kon_man_from_intyg() {
        String personId = "19121212-1212";

        String kon = DocumentHelper.extractKon(personId);

        assertEquals("man", kon);
    }

    @Test
    public void processor_extract_kon_kvinna_from_intyg() {
        String personId = "19121212-0000";

        String kon = DocumentHelper.extractKon(personId);

        assertEquals("kvinna", kon);
    }

    @Test
    public void getForstaDag() {
        String date = DocumentHelper.getForstaNedsattningsdag(document);

        assertEquals("2011-01-24", date);
    }

    @Test
    public void getSistaDag() {
        String date = DocumentHelper.getSistaNedsattningsdag(document);

        assertEquals("2011-02-20", date);
    }

    @Test
    public void getArbetsformaga() {
        assertEquals(0, DocumentHelper.getArbetsformaga(document).get(0).intValue());
    }

    // CHECKSTYLE:ON MagicNumber
}
