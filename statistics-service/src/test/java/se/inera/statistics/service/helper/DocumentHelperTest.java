package se.inera.statistics.service.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.inera.statistics.service.JSONSource;

import com.fasterxml.jackson.databind.JsonNode;

public class DocumentHelperTest {

    private JsonNode document = JSONParser.parse(JSONSource.readTemplateAsString());

    @Test
    public void get_enhet() {
        assertEquals("enhetId", DocumentHelper.getEnhetId(document));
    }

    @Test
    public void get_diagnos() {
        assertEquals("H81", DocumentHelper.getDiagnos(document));
        test(null, null);
    }

    private void test(Object a, Object b) {
        if ( a!= null && b != null && !a.equals(b)) {
            
        } else if (a != null ^ b != null) {
            
        } else {
            
        }
        
    }
}
