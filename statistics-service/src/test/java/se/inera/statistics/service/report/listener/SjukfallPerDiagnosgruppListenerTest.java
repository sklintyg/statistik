package se.inera.statistics.service.report.listener;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import se.inera.statistics.service.JSONSource;
import se.inera.statistics.service.helper.JSONParser;

public class SjukfallPerDiagnosgruppListenerTest {

    private SjukfallPerDiagnosgruppListener sjukfallPerDiagnosgruppListener = new SjukfallPerDiagnosgruppListener();

    @Test
    public void extractsCorrectGroup() {
        JsonNode intyg = JSONParser.parse(JSONSource.readTemplateAsString());
        //sjukfallPerDiagnosgruppListener.
    }

}
